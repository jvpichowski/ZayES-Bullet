package com.jvpichowski.jme3.es.bullet;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.math.Vector3f;
import com.jvpichowski.jme3.es.bullet.components.*;
import com.simsilica.es.*;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


/**
 * Created by Jan on 30.01.2017.
 */
public final class BulletSystem implements PhysicsTickListener, PhysicsCollisionListener{

    private PhysicsSpace.BroadphaseType broadphaseType;
    private Vector3f worldMin;
    private Vector3f worldMax;
    private PhysicsSpace physicsSpace = null;
    private EntityData entityData = null;

    private RigidBodyContainer rigidBodies;
    private GhostObjectContainer ghostObjects;
    private EntitySet collidingObjects;

    private List<PhysicsSystem> physicsSystems = new LinkedList<>();

    /**
     *
     * @param entityData
     * @param systems the systems which should change the physics state.
     *                    If no controllers are given the default controllers will be added.
     */
    public BulletSystem(EntityData entityData, PhysicsSystem... systems){
        this(entityData,
                new Vector3f(-10000f, -10000f, -10000f),
                new Vector3f(10000f, 10000f, 10000f),
                PhysicsSpace.BroadphaseType.DBVT, systems);
    }

    public BulletSystem(EntityData entityData, Vector3f worldMin, Vector3f worldMax,
                        PhysicsSpace.BroadphaseType broadphaseType, PhysicsSystem... systems){
        this.entityData = entityData;
        this.broadphaseType = broadphaseType;
        this.worldMin = worldMin;
        this.worldMax = worldMax;

        collidingObjects = entityData.getEntities(Collision.class);

        physicsSpace = new PhysicsSpace(worldMin, worldMax, broadphaseType);
        rigidBodies = new RigidBodyContainer(entityData, physicsSpace);
        ghostObjects = new GhostObjectContainer(entityData, physicsSpace);

        physicsSpace.addTickListener(this);
        physicsSpace.addCollisionListener(this);
        //physicsSpace.addCollisionObject();
        //physicsSpace.addCollisionGroupListener();

        rigidBodies.start();
        ghostObjects.start();

        //init all already added components
        //positionComponents.applyChanges();
        //applyPhysicsPositions(positionComponents);

        if(systems.length == 0){
            physicsSystems.add(new PhysicsPositionSystem());
            physicsSystems.add(new ForceSystem());
            physicsSystems.add(new GravitySystem());
        }else{
            for(PhysicsSystem system : systems){
                physicsSystems.add(system);
            }
        }

        physicsSystems.forEach(physicsSystem -> physicsSystem.initialize(entityData, this));
    }

    public void update(float time){
        if(physicsSpace == null){
            throw new IllegalStateException("BulletSystem is already destroyed!");
        }

        //update physics
        physicsSpace.update(time/*,maxSteps*/);
        physicsSpace.distributeEvents();
    }

    public void destroy(){
        physicsSystems.forEach(physicsSystem -> physicsSystem.destroy(entityData, this));

        physicsSpace.removeCollisionListener(this);
        physicsSpace.removeTickListener(this);
        ghostObjects.stop();
        rigidBodies.stop();

        physicsSpace.destroy();
        physicsSpace = null;
    }

    /**
     * For debugging with BulletDebugAppState or advanced behavior.
     * E.G. attach your own event listeners and so on.
     * Be aware that every physics object has an EntityId as UserObject attached.
     *
     * @return
     */
    public PhysicsSpace getPhysicsSpace(){
        return physicsSpace;
    }

    public RigidBodyContainer getRigidBodies() {
        return rigidBodies;
    }

    public GhostObjectContainer getGhostObjects() {
        return ghostObjects;
    }

    /**
     * DO NOT CALL
     *
     * @param space
     * @param tpf
     */
    @Override
    public void prePhysicsTick(PhysicsSpace space, float tpf) {
        //remove all collisions
        collidingObjects.applyChanges();
        collidingObjects.forEach(entity -> entityData.removeComponent(entity.getId(), Collision.class));

        //update rigid bodies
        rigidBodies.update();
        ghostObjects.update();
        //copy position to physics

    }

    /**
     * DO NOT CALL
     *
     * @param space
     * @param tpf
     */
    @Override
    public void physicsTick(PhysicsSpace space, float tpf) {
        //copy position to es

        //safe changes to know at the next call which are updated by the user
        //positionComponents.applyChanges(); //not good because of multithreading
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
