package com.jvpichowski.jme3.es.bullet.components;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jvpichowski.jme3.es.bullet.BulletSystem;
import com.jvpichowski.jme3.es.bullet.PhysicsSystem;
import com.jvpichowski.jme3.es.bullet.RigidBodyContainer;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;

/**
 * Created by jan on 12.02.2017.
 */
public final class LinearVelocitySystem implements PhysicsSystem, PhysicsTickListener {

    private EntityData entityData;
    private RigidBodyContainer rigidBodies;
    private EntitySet linearVelocities;

    @Override
    public void initialize(EntityData entityData, BulletSystem bulletSystem) {
        this.entityData = entityData;
        rigidBodies = bulletSystem.getRigidBodies();
        linearVelocities = entityData.getEntities(LinearVelocity.class);
        bulletSystem.getPhysicsSpace().addTickListener(this);
    }

    @Override
    public void destroy(EntityData entityData, BulletSystem bulletSystem) {
        bulletSystem.getPhysicsSpace().removeTickListener(this);
        linearVelocities.release();
    }


    @Override
    public void prePhysicsTick(PhysicsSpace space, float tpf) {
        linearVelocities.applyChanges();
        linearVelocities.forEach(entity -> {
            PhysicsRigidBody rigidBody = rigidBodies.getObject(entity.getId());
            if(rigidBody != null){
                rigidBody.setLinearVelocity(entity.get(LinearVelocity.class).getLinearVelocity());
            }
        });
    }

    @Override
    public void physicsTick(PhysicsSpace space, float tpf) {
        space.getRigidBodyList().forEach(rigidBody -> entityData.setComponent((EntityId)rigidBody.getUserObject(),
                new LinearVelocity(rigidBody.getLinearVelocity())));
    }
}
