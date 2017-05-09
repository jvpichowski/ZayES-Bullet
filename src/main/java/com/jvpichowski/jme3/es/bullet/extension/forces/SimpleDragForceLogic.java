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
        //this is a custom initialization method which must be called by the creator of the logic instance.
        //you can add any method you want to the logic.

        bulletSystem.getForceSystem().registerForce(SimpleDragForce.class);
    }

    /**
     * Call this to free resources
     *
     * @param bulletSystem
     */
    public void destroyLogic(BulletSystem bulletSystem){
        bulletSystem.getForceSystem().unregisterForce(SimpleDragForce.class);
    }

    @Override
    public void run() {
        //this method is called every time when the logic manager wants to update its attached logic's.

        //obtain the values
        Vector3f v = get(LinearVelocity.class).getVelocity();
        float factor = get(SimpleDrag.class).getFactor();
        //do the logic
        Vector3f force = v.normalize().multLocal(-factor*v.lengthSquared());
        //save the results
        set(new SimpleDragForce(force));
    }

    @Override
    public void destroy() {
        //this method is called if a component from the entity is removed on which this logic depends.

        //we don't need this component any more. Remove it from the entity to prevent mysterious errors..
        clear(SimpleDragForce.class);
    }

}
