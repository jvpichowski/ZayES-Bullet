package com.jvpichowski.jme3.es.bullet.systems;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.objects.PhysicsGhostObject;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jvpichowski.jme3.es.ObjectContainer;
import com.jvpichowski.jme3.es.bullet.*;
import com.jvpichowski.jme3.es.bullet.components.PhysicsPosition;
import com.jvpichowski.jme3.es.bullet.components.WarpPosition;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;

/**
 * Copies the physics position to the entities every physics tick.
 */
public final class PhysicsPositionSystem implements PhysicsSystem, PhysicsTickListener {

    private EntityData entityData;

    private ObjectContainer<PhysicsRigidBody> rigidBodies;
    private ObjectContainer<PhysicsGhostObject> ghostObjects;

    private EntitySet physicsPositions;
    private EntitySet warpPositions;

    @Override
    public void initialize(EntityData entityData, BulletSystem bulletSystem) {
        this.entityData = entityData;
        rigidBodies = bulletSystem.getRigidBodies();
        ghostObjects = bulletSystem.getGhostObjects();
        PhysicsInstanceFilter instanceFilter = bulletSystem.getInstanceFilter();
        if(instanceFilter == null) {
            physicsPositions = entityData.getEntities(PhysicsPosition.class);
            warpPositions = entityData.getEntities(WarpPosition.class);
        }else{
            physicsPositions = entityData.getEntities(instanceFilter, instanceFilter.getComponentType(), PhysicsPosition.class);
            warpPositions = entityData.getEntities(instanceFilter, instanceFilter.getComponentType(), WarpPosition.class);
        }
        bulletSystem.getPhysicsSpace().addTickListener(this);
    }

    @Override
    public void destroy(EntityData entityData, BulletSystem bulletSystem) {
        bulletSystem.getPhysicsSpace().removeTickListener(this);
        physicsPositions.release();
        warpPositions.release();
    }

    @Override
    public void prePhysicsTick(PhysicsSpace space, float tpf) {
        warpPositions.applyChanges();
        warpPositions.forEach(entity -> {
            WarpPosition position = entity.get(WarpPosition.class);
            PhysicsRigidBody rigidBody = rigidBodies.getObject(entity.getId());
            if(rigidBody != null){
                rigidBody.setPhysicsLocation(position.getLocation());
                rigidBody.setPhysicsRotation(position.getRotation());
                entityData.removeComponent(entity.getId(), WarpPosition.class);
            }
            PhysicsGhostObject ghostObject = ghostObjects.getObject(entity.getId());
            if(ghostObject != null){
                ghostObject.setPhysicsLocation(position.getLocation());
                ghostObject.setPhysicsRotation(position.getRotation());
                entityData.removeComponent(entity.getId(), WarpPosition.class);
            }
        });
    }

    @Override
    public void physicsTick(PhysicsSpace space, float tpf) {
        //apply ghost position before rigidBody so that it can move with an rigidBody if they are coupled
        space.getGhostObjectList().forEach(ghostObject -> entityData.setComponent((EntityId)ghostObject.getUserObject(),
                new PhysicsPosition(ghostObject.getPhysicsLocation(), ghostObject.getPhysicsRotation())));
        space.getRigidBodyList().forEach(rigidBody -> entityData.setComponent((EntityId)rigidBody.getUserObject(),
                new PhysicsPosition(rigidBody.getPhysicsLocation(), rigidBody.getPhysicsRotation())));

    }
}
