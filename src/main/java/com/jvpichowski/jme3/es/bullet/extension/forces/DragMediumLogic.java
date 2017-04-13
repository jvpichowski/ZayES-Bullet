package com.jvpichowski.jme3.es.bullet.extension.forces;

import com.jme3.math.Vector3f;
import com.jvpichowski.jme3.es.bullet.BulletSystem;
import com.jvpichowski.jme3.es.bullet.components.LinearVelocity;
import com.jvpichowski.jme3.es.logic.BaseSimpleEntityLogic;

/**
 * The logic of the DragMedium component. It should be updated before the physics tick happens.
 */
public final class DragMediumLogic extends BaseSimpleEntityLogic {

    @Override
    public void registerComponents() {
        dependsOn(DragMedium.class, LinearVelocity.class);
    }

    public void initLogic(BulletSystem bulletSystem){
        bulletSystem.getForceSystem().registerForce(DragMediumForce.class);
        //TODO unregister on destroy
    }

    @Override
    public void run() {
        Vector3f v = get(LinearVelocity.class).getVelocity();
        float area = get(DragMedium.class).getArea();
        float density = get(DragMedium.class).getDensity();
        float coeff = get(DragMedium.class).getCoefficient();
        Vector3f force = v.clone().normalizeLocal().multLocal(-0.5f*v.lengthSquared()*area*density*coeff);
        set(new DragMediumForce(force));
    }

    @Override
    public void destroy() {
        clear(DragMediumForce.class);
    }

}
