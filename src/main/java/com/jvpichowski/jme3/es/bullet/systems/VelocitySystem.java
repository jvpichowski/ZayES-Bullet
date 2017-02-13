package com.jvpichowski.jme3.es.bullet.systems;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jvpichowski.jme3.es.bullet.BulletSystem;
import com.jvpichowski.jme3.es.bullet.PhysicsSystem;
import com.jvpichowski.jme3.es.bullet.RigidBodyContainer;
import com.jvpichowski.jme3.es.bullet.components.AngularVelocity;
import com.jvpichowski.jme3.es.bullet.components.LinearVelocity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;

/**
 * Created by jan on 12.02.2017.
 */
public final class VelocitySystem implements PhysicsSystem, PhysicsTickListener {

    private EntityData entityData;
    private RigidBodyContainer rigidBodies;
    private EntitySet linearVelocities;
    private EntitySet angularVelocities;

    @Override
    public void initialize(EntityData entityData, BulletSystem bulletSystem) {
        this.entityData = entityData;
        rigidBodies = bulletSystem.getRigidBodies();
        linearVelocities = entityData.getEntities(LinearVelocity.class);
        angularVelocities = entityData.getEntities(AngularVelocity.class);
        bulletSystem.getPhysicsSpace().addTickListener(this);
    }

    @Override
    public void destroy(EntityData entityData, BulletSystem bulletSystem) {
        bulletSystem.getPhysicsSpace().removeTickListener(this);
        linearVelocities.release();
        angularVelocities.release();
    }


    @Override
    public void prePhysicsTick(PhysicsSpace space, float tpf) {
        linearVelocities.applyChanges();
        linearVelocities.forEach(entity -> {
            PhysicsRigidBody rigidBody = rigidBodies.getObject(entity.getId());
            if(rigidBody != null){
                rigidBody.setLinearVelocity(entity.get(LinearVelocity.class).getVelocity());
            }
        });
        angularVelocities.applyChanges();
        angularVelocities.forEach(entity -> {
            PhysicsRigidBody rigidBody = rigidBodies.getObject(entity.getId());
            if(rigidBody != null){
                rigidBody.setAngularVelocity(entity.get(AngularVelocity.class).getVelocity());
            }
        });
    }

    @Override
    public void physicsTick(PhysicsSpace space, float tpf) {
        space.getRigidBodyList().forEach(rigidBody -> entityData.setComponents((EntityId)rigidBody.getUserObject(),
                new LinearVelocity(rigidBody.getLinearVelocity()),
                new AngularVelocity(rigidBody.getAngularVelocity())));
    }
}
