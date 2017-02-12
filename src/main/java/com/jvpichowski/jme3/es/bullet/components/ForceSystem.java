package com.jvpichowski.jme3.es.bullet.components;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jvpichowski.jme3.es.bullet.BulletSystem;
import com.jvpichowski.jme3.es.bullet.PhysicsSystem;
import com.jvpichowski.jme3.es.bullet.RigidBodyContainer;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntitySet;

/**
 * Created by jan on 12.02.2017.
 */
public final class ForceSystem implements PhysicsSystem, PhysicsTickListener {

    private RigidBodyContainer rigidBodies;
    private EntitySet forces;
    private EntityData entityData;

    @Override
    public void initialize(EntityData entityData, BulletSystem bulletSystem) {
        this.entityData = entityData;
        rigidBodies = bulletSystem.getRigidBodies();
        forces = entityData.getEntities(Force.class);
        bulletSystem.getPhysicsSpace().addTickListener(this);
    }

    @Override
    public void destroy(EntityData entityData, BulletSystem bulletSystem) {
        bulletSystem.getPhysicsSpace().removeTickListener(this);
        forces.release();
    }


    @Override
    public void prePhysicsTick(PhysicsSpace space, float tpf) {
        forces.applyChanges();
        forces.forEach(entity -> {
            Force force = entity.get(Force.class);
            PhysicsRigidBody rigidBody = rigidBodies.getObject(entity.getId());
            if(rigidBody != null){
                rigidBody.applyForce(force.getForce(), force.getLocation());
            }
            entityData.removeComponent(entity.getId(), Force.class);
        });
    }

    @Override
    public void physicsTick(PhysicsSpace space, float tpf) {

    }
}
