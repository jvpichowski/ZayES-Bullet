package com.jvpichowski.jme3.es.bullet;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Vector3f;
import com.jvpichowski.jme3.es.bullet.components.*;
import com.simsilica.es.*;

import java.util.HashSet;
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

    private EntityContainer<PhysicsRigidBody> rigidBodies;
    private PhysicsController[] controllers;
    private EntitySet collidingObjects;

    private static PhysicsController[] defaultControllers(){
        return new PhysicsController[]{
                new PhysicsPositionController(),
                new LinearVelocityController(),
                new ForceController()
        };
    }

    /**
     *
     * @param entityData
     * @param controllers the controllers which should change the physics state.
     *                    If no controllers are given the default controllers will be added.
     */
    public BulletSystem(EntityData entityData, PhysicsController... controllers){
        this(entityData,
                new Vector3f(-10000f, -10000f, -10000f),
                new Vector3f(10000f, 10000f, 10000f),
                PhysicsSpace.BroadphaseType.DBVT, controllers);
    }

    public BulletSystem(EntityData entityData, Vector3f worldMin, Vector3f worldMax, PhysicsSpace.BroadphaseType broadphaseType, PhysicsController... controllers){
        this.entityData = entityData;
        this.broadphaseType = broadphaseType;
        this.worldMin = worldMin;
        this.worldMax = worldMax;
        if(controllers.length == 0){
            this.controllers = defaultControllers();
        }else{
            this.controllers = controllers;
        }
        collidingObjects = entityData.getEntities(Collision.class);

        physicsSpace = new PhysicsSpace(worldMin, worldMax, broadphaseType);
        rigidBodies = new RigidBodyContainer(entityData, physicsSpace);
        for(PhysicsController controller : this.controllers){
            controller.initialize(entityData, rigidBodies);
        }

        physicsSpace.addTickListener(this);
        physicsSpace.addCollisionListener(this);
        //physicsSpace.addCollisionObject();
        //physicsSpace.addCollisionGroupListener();

        rigidBodies.start();

        //init all already added components
        //positionComponents.applyChanges();
        //applyPhysicsPositions(positionComponents);
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
        physicsSpace.removeCollisionListener(this);
        physicsSpace.removeTickListener(this);
        rigidBodies.stop();
        for(PhysicsController controller : controllers){
            controller.destroy();
        }
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
        //copy position to physics
        for(PhysicsController controller : controllers){
            controller.prePhysicsTick(space, tpf);
        }
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

        for(PhysicsController controller : controllers){
            controller.physicsTick(space, tpf);
        }

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
