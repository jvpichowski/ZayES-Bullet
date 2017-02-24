package com.jvpichowski.jme3.es.bullet.systems;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.objects.PhysicsGhostObject;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jvpichowski.jme3.es.bullet.*;
import com.jvpichowski.jme3.es.bullet.components.PhysicsPosition;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;

/**
 * Copies the physics position to the entities every physics tick.
 */
public final class PhysicsPositionSystem implements PhysicsSystem, PhysicsTickListener {

    private EntityData entityData;

    private RigidBodyContainer rigidBodies;
    private GhostObjectContainer ghostObjects;

    private EntitySet physicsPositions;

    @Override
    public void initialize(EntityData entityData, BulletSystem bulletSystem) {
        this.entityData = entityData;
        rigidBodies = bulletSystem.getRigidBodies();
        ghostObjects = bulletSystem.getGhostObjects();
        PhysicsInstanceFilter instanceFilter = bulletSystem.getInstanceFilter();
        if(instanceFilter == null) {
            physicsPositions = entityData.getEntities(PhysicsPosition.class);
        }else{
            physicsPositions = entityData.getEntities(instanceFilter, instanceFilter.getComponentType(), PhysicsPosition.class);
        }
        bulletSystem.getPhysicsSpace().addTickListener(this);
    }

    @Override
    public void destroy(EntityData entityData, BulletSystem bulletSystem) {
        bulletSystem.getPhysicsSpace().removeTickListener(this);
        physicsPositions.release();
    }

    @Override
    public void prePhysicsTick(PhysicsSpace space, float tpf) {
        physicsPositions.applyChanges();
        physicsPositions.forEach(entity -> {
            PhysicsPosition position = entity.get(PhysicsPosition.class);
            PhysicsRigidBody rigidBody = rigidBodies.getObject(entity.getId());
            if(rigidBody != null){
                rigidBody.setPhysicsLocation(position.getLocation());
                rigidBody.setPhysicsRotation(position.getRotation());
            }
            PhysicsGhostObject ghostObject = ghostObjects.getObject(entity.getId());
            if(ghostObject != null){
                ghostObject.setPhysicsLocation(position.getLocation());
                ghostObject.setPhysicsRotation(position.getRotation());
            }
        });
    }

    @Override
    public void physicsTick(PhysicsSpace space, float tpf) {
        //apply ghost position before rigidBody so that it can moev with an rigidBody if they are coupled
        space.getGhostObjectList().forEach(ghostObject -> entityData.setComponent((EntityId)ghostObject.getUserObject(),
                new PhysicsPosition(ghostObject.getPhysicsLocation(), ghostObject.getPhysicsRotation())));
        space.getRigidBodyList().forEach(rigidBody -> entityData.setComponent((EntityId)rigidBody.getUserObject(),
                new PhysicsPosition(rigidBody.getPhysicsLocation(), rigidBody.getPhysicsRotation())));

    }
}
