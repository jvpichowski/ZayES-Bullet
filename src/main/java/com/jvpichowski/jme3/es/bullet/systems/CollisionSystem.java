package com.jvpichowski.jme3.es.bullet.systems;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jvpichowski.jme3.es.ObjectContainer;
import com.jvpichowski.jme3.es.bullet.BulletSystem;
import com.jvpichowski.jme3.es.bullet.PhysicsInstanceFilter;
import com.jvpichowski.jme3.es.bullet.PhysicsSystem;
import com.jvpichowski.jme3.es.bullet.components.Collision;
import com.jvpichowski.jme3.es.bullet.components.CollisionGroup;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;

import java.util.HashSet;
import java.util.Set;

/**
 * Creates new collision component if two physics objects overlap.
 */
public final class CollisionSystem implements PhysicsSystem, PhysicsCollisionListener, PhysicsTickListener {

    private EntityData entityData;
    private EntitySet collidingObjects;
    private EntitySet collisionGroups;

    private ObjectContainer<PhysicsRigidBody> rigidBodies;

    @Override
    public void initialize(EntityData entityData, BulletSystem bulletSystem) {
        this.entityData = entityData;
        PhysicsInstanceFilter filter = bulletSystem.getInstanceFilter();
        if(filter == null) {
            collidingObjects = entityData.getEntities(Collision.class);
            collisionGroups = entityData.getEntities(CollisionGroup.class);
        }else{
            collidingObjects = entityData.getEntities(filter, filter.getComponentType(), Collision.class);
            collisionGroups = entityData.getEntities(filter, filter.getComponentType(), CollisionGroup.class);
        }
        rigidBodies = bulletSystem.getRigidBodies();
        bulletSystem.addTickListener(this, true, true);
        bulletSystem.getPhysicsSpace().addCollisionListener(this);
    }

    @Override
    public void destroy(EntityData entityData, BulletSystem bulletSystem) {
        bulletSystem.getPhysicsSpace().removeCollisionListener(this);
        bulletSystem.removeTickListener(this);
        collidingObjects.release();
        collisionGroups.release();
    }

    @Override
    public void prePhysicsTick(PhysicsSpace space, float tpf) {
        collidingObjects.applyChanges();
        collidingObjects.forEach(entity -> entityData.removeComponent(entity.getId(), Collision.class));
        collisionGroups.applyChanges();
        collisionGroups.forEach(entity -> {
            PhysicsRigidBody rigidBody = rigidBodies.getObject(entity.getId());
            if(rigidBody != null){
                CollisionGroup collisionGroup = entity.get(CollisionGroup.class);
                rigidBody.setCollideWithGroups(collisionGroup.getCollideWithGroups());
                rigidBody.setCollisionGroup(collisionGroup.getCollisionGroup());
            }
        });
    }

    @Override
    public void physicsTick(PhysicsSpace space, float tpf) {

    }

    @Override
    public void collision(PhysicsCollisionEvent event) {
        EntityId a = (EntityId)event.getObjectA().getUserObject();
        EntityId b = (EntityId)event.getObjectB().getUserObject();
        //change collision list of a
        Collision collisionComponent = entityData.getComponent(a, Collision.class);
        if(collisionComponent == null){
            collisionComponent = new Collision(new HashSet<>());
        }
        Set<EntityId> collisions = collisionComponent.getCollisions();
        collisions.add(b);
        entityData.setComponent(a, new Collision(collisions));
        //change collision list of b
        collisionComponent = entityData.getComponent(b, Collision.class);
        if(collisionComponent == null){
            collisionComponent = new Collision(new HashSet<>());
        }
        collisions = collisionComponent.getCollisions();
        collisions.add(a);
        entityData.setComponent(b, new Collision(collisions));
    }
}
