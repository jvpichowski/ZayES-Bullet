package com.jvpichowski.jme3.es.bullet.systems;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jvpichowski.jme3.es.ObjectContainer;
import com.jvpichowski.jme3.es.bullet.BulletSystem;
import com.jvpichowski.jme3.es.bullet.PhysicsInstanceFilter;
import com.jvpichowski.jme3.es.bullet.PhysicsSystem;
import com.jvpichowski.jme3.es.bullet.components.AngularVelocity;
import com.jvpichowski.jme3.es.bullet.components.LinearVelocity;
import com.jvpichowski.jme3.es.bullet.components.WarpVelocity;
import com.simsilica.es.*;

/**
 * Copies the velocity of the rigid bodies to the entity every physics tick.
 */
public final class VelocitySystem implements PhysicsSystem, PhysicsTickListener {

    private EntityData entityData;
    private ObjectContainer<PhysicsRigidBody> rigidBodies;
    private EntitySet linearVelocities;
    private EntitySet angularVelocities;
    private EntitySet warpVelocities;

    @Override
    public void initialize(EntityData entityData, BulletSystem bulletSystem) {
        this.entityData = entityData;
        rigidBodies = bulletSystem.getRigidBodies();
        PhysicsInstanceFilter filter = bulletSystem.getInstanceFilter();
        if(filter == null) {
            linearVelocities = entityData.getEntities(LinearVelocity.class);
            angularVelocities = entityData.getEntities(AngularVelocity.class);
            warpVelocities = entityData.getEntities(WarpVelocity.class);
        }else{
            linearVelocities = entityData.getEntities(filter, filter.getComponentType(), LinearVelocity.class);
            angularVelocities = entityData.getEntities(filter, filter.getComponentType(), AngularVelocity.class);
            warpVelocities = entityData.getEntities(filter, filter.getComponentType(), WarpVelocity.class);
        }
        bulletSystem.addTickListener(this, true, true);
    }

    @Override
    public void destroy(EntityData entityData, BulletSystem bulletSystem) {
        bulletSystem.removeTickListener(this);
        linearVelocities.release();
        angularVelocities.release();
        warpVelocities.release();
    }


    @Override
    public void prePhysicsTick(PhysicsSpace space, float tpf) {
        warpVelocities.applyChanges();
        warpVelocities.forEach(entity -> {
            PhysicsRigidBody rigidBody = rigidBodies.getObject(entity.getId());
            if(rigidBody != null){
                WarpVelocity warpVelocity = entity.get(WarpVelocity.class);
                rigidBody.setLinearVelocity(warpVelocity.getLinearVelocity());
                rigidBody.setAngularVelocity(warpVelocity.getAngularVelocity());
            }
            entityData.removeComponent(entity.getId(), WarpVelocity.class);
        });
    }

    @Override
    public void physicsTick(PhysicsSpace space, float tpf) {
        space.getRigidBodyList().forEach(rigidBody -> entityData.setComponents((EntityId)rigidBody.getUserObject(),
                new LinearVelocity(rigidBody.getLinearVelocity()),
                new AngularVelocity(rigidBody.getAngularVelocity())));
    }
}
