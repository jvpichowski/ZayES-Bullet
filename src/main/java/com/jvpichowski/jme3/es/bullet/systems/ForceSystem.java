package com.jvpichowski.jme3.es.bullet.systems;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jvpichowski.jme3.es.ObjectContainer;
import com.jvpichowski.jme3.es.bullet.BulletSystem;
import com.jvpichowski.jme3.es.bullet.PhysicsInstanceFilter;
import com.jvpichowski.jme3.es.bullet.PhysicsSystem;
import com.jvpichowski.jme3.es.bullet.components.*;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntitySet;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Applies the various forces and torques to the rigid body objects.
 * Afterwards they will be removed from the component.
 */
public final class ForceSystem implements PhysicsSystem, PhysicsTickListener, ExtensibleForceSystem {

    private static final Logger LOGGER = Logger.getLogger(ForceSystem.class.getName());

    private ObjectContainer<PhysicsRigidBody> rigidBodies;
    private EntitySet forces;
    private EntitySet centralForces;
    private EntitySet torques;
    private EntityData entityData;

    private PhysicsInstanceFilter filter = null;
    private Map<Class<? extends ForceComponent>, EntitySet> extensibleForces = new HashMap<>();
    private Map<Class<? extends TorqueComponent>, EntitySet> extensibleTorques = new HashMap<>();

    @Override
    public void initialize(EntityData entityData, BulletSystem bulletSystem) {
        this.entityData = entityData;
        rigidBodies = bulletSystem.getRigidBodies();
        filter = bulletSystem.getInstanceFilter();
        if(filter == null) {
            forces = entityData.getEntities(Force.class);
            centralForces = entityData.getEntities(CentralForce.class);
            torques = entityData.getEntities(Torque.class);
        }else{
            forces = entityData.getEntities(filter, filter.getComponentType(), Force.class);
            centralForces = entityData.getEntities(filter, filter.getComponentType(), CentralForce.class);
            torques = entityData.getEntities(filter, filter.getComponentType(), Torque.class);
        }
        bulletSystem.addTickListener(this, true, true);
    }

    @Override
    public void destroy(EntityData entityData, BulletSystem bulletSystem) {
        bulletSystem.removeTickListener(this);
        forces.release();
        centralForces.release();
        torques.release();

        extensibleForces.forEach((comp, set) -> set.release());
        extensibleForces.clear();
        extensibleTorques.forEach((comp, set) -> set.release());
        extensibleTorques.clear();

        filter = null;
    }

    @Override
    public void registerForce(Class<? extends ForceComponent> forceComponent) {
        unregisterForce(forceComponent);
        if(filter == null) {
            extensibleForces.put(forceComponent, entityData.getEntities(forceComponent));
        }else{
            extensibleForces.put(forceComponent, entityData.getEntities(filter, filter.getComponentType(), forceComponent));
        }
        LOGGER.fine("registered force "+forceComponent);
    }

    @Override
    public void registerTorque(Class<? extends TorqueComponent> torqueComponent) {
        unregisterTorque(torqueComponent);
        if(filter == null) {
            extensibleTorques.put(torqueComponent, entityData.getEntities(torqueComponent));
        }else{
            extensibleTorques.put(torqueComponent, entityData.getEntities(filter, filter.getComponentType(), torqueComponent));
        }
        LOGGER.fine("registered torque "+torqueComponent);
    }

    @Override
    public void unregisterForce(Class<? extends ForceComponent> forceComponent) {
        if(extensibleForces.containsKey(forceComponent)){
            extensibleForces.get(forceComponent).release();
            extensibleForces.remove(forceComponent);
        }
        LOGGER.fine("unregistered force "+forceComponent);
    }

    @Override
    public void unregisterTorque(Class<? extends TorqueComponent> torqueComponent) {
        if(extensibleTorques.containsKey(torqueComponent)){
            extensibleTorques.get(torqueComponent).release();
            extensibleTorques.remove(torqueComponent);
        }
        LOGGER.fine("unregistered torque "+torqueComponent);
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

        extensibleForces.forEach((comp, set) -> {
            set.applyChanges();
            set.forEach(entity -> {
                ForceComponent force = entity.get(comp);
                if (force.getForce() == null) {
                    LOGGER.log(Level.WARNING, "Force vector of {0} is null", force);
                } else {
                    PhysicsRigidBody rigidBody = rigidBodies.getObject(entity.getId());
                    if (rigidBody != null) {
                        if (force.getLocation() == null) {
                            rigidBody.applyCentralForce(force.getForce());
                        } else {
                            rigidBody.applyForce(force.getForce(), force.getLocation());
                        }
                    }
                }
            });
        });

        extensibleTorques.forEach((comp, set) -> {
            set.applyChanges();
            set.forEach(entity -> {
                TorqueComponent torque = entity.get(comp);
                PhysicsRigidBody rigidBody = rigidBodies.getObject(entity.getId());
                if(rigidBody != null){
                    rigidBody.applyTorque(torque.getTorque());
                }
            });
        });
    }

    @Override
    public void physicsTick(PhysicsSpace space, float tpf) {

    }
}
