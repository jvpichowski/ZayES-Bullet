package com.jvpichowski.jme3.es.bullet.systems;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jvpichowski.jme3.es.bullet.BulletSystem;
import com.jvpichowski.jme3.es.bullet.PhysicsInstanceFilter;
import com.jvpichowski.jme3.es.bullet.PhysicsSystem;
import com.jvpichowski.jme3.es.bullet.RigidBodyContainer;
import com.jvpichowski.jme3.es.bullet.components.CentralForce;
import com.jvpichowski.jme3.es.bullet.components.Force;
import com.jvpichowski.jme3.es.bullet.components.Torque;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntitySet;

/**
 * Created by jan on 12.02.2017.
 */
public final class ForceSystem implements PhysicsSystem, PhysicsTickListener {

    private RigidBodyContainer rigidBodies;
    private EntitySet forces;
    private EntitySet centralForces;
    private EntitySet torques;
    private EntityData entityData;

    @Override
    public void initialize(EntityData entityData, BulletSystem bulletSystem) {
        this.entityData = entityData;
        rigidBodies = bulletSystem.getRigidBodies();
        PhysicsInstanceFilter filter = bulletSystem.getInstanceFilter();
        if(filter == null) {
            forces = entityData.getEntities(Force.class);
            centralForces = entityData.getEntities(CentralForce.class);
            torques = entityData.getEntities(Torque.class);
        }else{
            forces = entityData.getEntities(filter, filter.getComponentType(), Force.class);
            centralForces = entityData.getEntities(filter, filter.getComponentType(), CentralForce.class);
            torques = entityData.getEntities(filter, filter.getComponentType(), Torque.class);
        }
        bulletSystem.getPhysicsSpace().addTickListener(this);
    }

    @Override
    public void destroy(EntityData entityData, BulletSystem bulletSystem) {
        bulletSystem.getPhysicsSpace().removeTickListener(this);
        forces.release();
        centralForces.release();
        torques.release();
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
        centralForces.applyChanges();
        centralForces.forEach(entity -> {
            CentralForce force = entity.get(CentralForce.class);
            PhysicsRigidBody rigidBody = rigidBodies.getObject(entity.getId());
            if(rigidBody != null){
                rigidBody.applyCentralForce(force.getForce());
            }
            entityData.removeComponent(entity.getId(), CentralForce.class);
        });
        torques.applyChanges();
        torques.forEach(entity -> {
            Torque torque = entity.get(Torque.class);
            PhysicsRigidBody rigidBody = rigidBodies.getObject(entity.getId());
            if(rigidBody != null){
                rigidBody.applyTorque(torque.getTorque());
            }
            entityData.removeComponent(entity.getId(), Torque.class);
        });
    }

    @Override
    public void physicsTick(PhysicsSpace space, float tpf) {

    }
}
