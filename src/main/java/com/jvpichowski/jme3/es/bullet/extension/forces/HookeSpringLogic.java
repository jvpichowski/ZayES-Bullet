package com.jvpichowski.jme3.es.bullet.extension.forces;

import com.jme3.math.Vector3f;
import com.jvpichowski.jme3.es.bullet.BulletSystem;
import com.jvpichowski.jme3.es.bullet.components.PhysicsPosition;
import com.jvpichowski.jme3.es.logic.BaseSimpleEntityLogic;

/**
 * The logic for the HookeSpring component. It should be update before the physics tick happens.
 */
public final class HookeSpringLogic extends BaseSimpleEntityLogic {

    public void initLogic(BulletSystem bulletSystem){
        bulletSystem.getForceSystem().registerForce(HookeSpringForce.class);
        //TODO unregister on destroy
    }

    @Override
    public void registerComponents() {
        dependsOn(HookeSpring.class, PhysicsPosition.class);
    }

    @Override
    public void run() {
        Vector3f location = get(PhysicsPosition.class).getLocation();
        Vector3f equilibriumLocation = get(HookeSpring.class).getEquilibriumLocation();
        float springConstant = get(HookeSpring.class).getConstant();
        Vector3f force = equilibriumLocation.subtract(location).mult(springConstant);
        set(new HookeSpringForce(force, get(HookeSpring.class).getConnectionLocation()));
    }

    @Override
    public void destroy() {
        clear(HookeSpringForce.class);
    }

}
