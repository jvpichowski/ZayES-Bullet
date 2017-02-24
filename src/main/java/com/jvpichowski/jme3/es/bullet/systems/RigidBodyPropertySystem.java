package com.jvpichowski.jme3.es.bullet.systems;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Vector3f;
import com.jvpichowski.jme3.es.bullet.BulletSystem;
import com.jvpichowski.jme3.es.bullet.PhysicsSystem;
import com.jvpichowski.jme3.es.bullet.RigidBodyContainer;
import com.jvpichowski.jme3.es.bullet.components.Factor;
import com.jvpichowski.jme3.es.bullet.components.Friction;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntitySet;

/**
 * Applies the Factor and Friction components to a rigid body.
 */
public final class RigidBodyPropertySystem implements PhysicsSystem, PhysicsTickListener {

    private RigidBodyContainer rigidBodies;
    private EntitySet frictions;
    private EntitySet factors;

    @Override
    public void initialize(EntityData entityData, BulletSystem bulletSystem) {
        rigidBodies = bulletSystem.getRigidBodies();
        frictions = entityData.getEntities(Friction.class);
        factors = entityData.getEntities(Factor.class);
        bulletSystem.getPhysicsSpace().addTickListener(this);
    }

    @Override
    public void destroy(EntityData entityData, BulletSystem bulletSystem) {
        bulletSystem.getPhysicsSpace().removeTickListener(this);
        frictions.release();
        factors.release();
    }


    @Override
    public void prePhysicsTick(PhysicsSpace space, float tpf) {
        frictions.applyChanges();
        //TODO only for changed entities!
        frictions.forEach(e -> {
            PhysicsRigidBody physicsRigidBody = rigidBodies.getObject(e.getId());
            if(physicsRigidBody != null) {
                physicsRigidBody.setFriction(e.get(Friction.class).getFriction());
            }
        });
        factors.applyChanges();
        factors.forEach(e -> {
            PhysicsRigidBody physicsRigidBody = rigidBodies.getObject(e.getId());
            if(physicsRigidBody != null) {
                Factor factor = e.get(Factor.class);
                physicsRigidBody.setLinearFactor(factor.getLinearFactor());
                physicsRigidBody.setAngularFactor(factor.getAngularFactor());
                //physicsRigidBody.setAngularFactor(factor.getSingleAngularFactor());
            }
        });
    }

    @Override
    public void physicsTick(PhysicsSpace space, float tpf) {

    }
}
