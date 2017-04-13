package com.jvpichowski.jme3.es.bullet.extension.forces;

import com.jme3.math.Vector3f;
import com.jvpichowski.jme3.es.bullet.BulletSystem;
import com.jvpichowski.jme3.es.bullet.components.LinearVelocity;
import com.jvpichowski.jme3.es.logic.BaseSimpleEntityLogic;

/**
 * The logic of the SimpleDrag component. It should be updated before the physics tick happens.
 */
public final class SimpleDragForceLogic extends BaseSimpleEntityLogic {

    @Override
    public void registerComponents() {
        dependsOn(SimpleDrag.class, LinearVelocity.class);
    }

    /**
     * Call this method to initialize the logic before running it.
     *
     * @param bulletSystem
     */
    public void initLogic(BulletSystem bulletSystem){
        bulletSystem.getForceSystem().registerForce(SimpleDragForce.class);
        //TODO unregister on destroy
    }

    @Override
    public void init() {
        updateForce();
    }

    @Override
    public void update() {
        updateForce();
    }

    @Override
    public void destroy() {
        clear(DragMediumForce.class);
    }

    private void updateForce(){
        Vector3f v = get(LinearVelocity.class).getVelocity();
        float factor = get(SimpleDrag.class).getFactor();
        Vector3f force = v.clone().normalizeLocal().multLocal(-factor*v.lengthSquared());
        set(new SimpleDragForce(force));
    }

}
