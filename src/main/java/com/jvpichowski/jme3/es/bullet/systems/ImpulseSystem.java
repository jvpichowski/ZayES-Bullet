package com.jvpichowski.jme3.es.bullet.systems;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jvpichowski.jme3.es.bullet.BulletSystem;
import com.jvpichowski.jme3.es.bullet.PhysicsInstanceFilter;
import com.jvpichowski.jme3.es.bullet.PhysicsSystem;
import com.jvpichowski.jme3.es.bullet.RigidBodyContainer;
import com.jvpichowski.jme3.es.bullet.components.Impulse;
import com.jvpichowski.jme3.es.bullet.components.TorqueImpulse;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;

/**
 * Created by jan on 13.02.2017.
 */
public final class ImpulseSystem implements PhysicsSystem, PhysicsTickListener {

    private EntityData entityData;
    private RigidBodyContainer rigidBodies;
    private EntitySet impulses;
    private EntitySet torqueImpulses;

    @Override
    public void initialize(EntityData entityData, BulletSystem bulletSystem) {
        this.entityData = entityData;
        rigidBodies = bulletSystem.getRigidBodies();
        PhysicsInstanceFilter filter = bulletSystem.getInstanceFilter();
        if(filter == null) {
            impulses = entityData.getEntities(Impulse.class);
            torqueImpulses = entityData.getEntities(TorqueImpulse.class);
        }else {
            impulses = entityData.getEntities(filter, filter.getComponentType(), Impulse.class);
            torqueImpulses = entityData.getEntities(filter, filter.getComponentType(), TorqueImpulse.class);
        }
        bulletSystem.getPhysicsSpace().addTickListener(this);
    }

    @Override
    public void destroy(EntityData entityData, BulletSystem bulletSystem) {
        bulletSystem.getPhysicsSpace().removeTickListener(this);
        impulses.release();
        torqueImpulses.release();
    }


    @Override
    public void prePhysicsTick(PhysicsSpace space, float tpf) {
        impulses.applyChanges();
        impulses.forEach(entity -> {
            PhysicsRigidBody rigidBody = rigidBodies.getObject(entity.getId());
            if(rigidBody != null){
                Impulse impulse = entity.get(Impulse.class);
                rigidBody.applyImpulse(impulse.getImpulse(), impulse.getRelativeLocation());
            }
            entityData.removeComponent(entity.getId(), Impulse.class);
        });
        torqueImpulses.applyChanges();
        torqueImpulses.forEach(entity -> {
            PhysicsRigidBody rigidBody = rigidBodies.getObject(entity.getId());
            if(rigidBody != null){
                rigidBody.applyTorqueImpulse(entity.get(TorqueImpulse.class).getImpulse());
            }
            entityData.removeComponent(entity.getId(), TorqueImpulse.class);
        });
    }

    @Override
    public void physicsTick(PhysicsSpace space, float tpf) {

    }
}
