package com.jvpichowski.jme3.es.bullet;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.math.Vector3f;
import com.jvpichowski.jme3.es.bullet.systems.*;
import com.simsilica.es.*;

import java.util.LinkedList;
import java.util.List;


/**
 * The main system. Create an instance and call update to update
 * the physics simulation. Don't forget to call destroy in the end.
 */
public final class BulletSystem implements PhysicsTickListener{

    private PhysicsInstanceFilter instanceFilter;

    private PhysicsSpace.BroadphaseType broadphaseType;
    private Vector3f worldMin;
    private Vector3f worldMax;
    private PhysicsSpace physicsSpace = null;
    private EntityData entityData = null;

    private RigidBodyContainer rigidBodies;
    private GhostObjectContainer ghostObjects;

    //This list contains all physics subsystems
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
                PhysicsSpace.BroadphaseType.DBVT, 0, systems);
    }

    public BulletSystem(EntityData entityData, int instance, PhysicsSystem... systems){
        this(entityData,
                new Vector3f(-10000f, -10000f, -10000f),
                new Vector3f(10000f, 10000f, 10000f),
                PhysicsSpace.BroadphaseType.DBVT, instance, systems);
    }

    public BulletSystem(EntityData entityData, Vector3f worldMin, Vector3f worldMax,
                        PhysicsSpace.BroadphaseType broadphaseType, int instance, PhysicsSystem... systems){
        if(instance > 0){
            this.instanceFilter = new PhysicsInstanceFilter(instance);
        }

        this.entityData = entityData;
        this.broadphaseType = broadphaseType;
        this.worldMin = worldMin;
        this.worldMax = worldMax;

        physicsSpace = new PhysicsSpace(worldMin, worldMax, broadphaseType);

        if(instanceFilter == null) {
            rigidBodies = new RigidBodyContainer(entityData, physicsSpace);
            ghostObjects = new GhostObjectContainer(entityData, physicsSpace);
        }else{
            rigidBodies = new RigidBodyContainer(entityData, physicsSpace, instanceFilter);
            ghostObjects = new GhostObjectContainer(entityData, physicsSpace, instanceFilter);
        }

        physicsSpace.addTickListener(this);
        //physicsSpace.addCollisionObject();
        //physicsSpace.addCollisionGroupListener();

        rigidBodies.start();
        ghostObjects.start();

        //init all already added components
        //positionComponents.applyChanges();
        //applyPhysicsPositions(positionComponents);

        if(systems.length == 0){
            physicsSystems.add(new CollisionSystem());
            physicsSystems.add(new PhysicsPositionSystem());
            physicsSystems.add(new ForceSystem());
            physicsSystems.add(new GravitySystem());
            physicsSystems.add(new VelocitySystem());
            physicsSystems.add(new ImpulseSystem());
            physicsSystems.add(new RigidBodyPropertySystem());
        }else{
            for(PhysicsSystem system : systems){
                physicsSystems.add(system);
            }
        }

        physicsSystems.forEach(physicsSystem -> physicsSystem.initialize(entityData, this));
    }

    /**
     * update the physics simulation
     *
     * @param time
     */
    public void update(float time){
        if(physicsSpace == null){
            throw new IllegalStateException("BulletSystem is already destroyed!");
        }

        //update physics
        physicsSpace.update(time/*,maxSteps*/);
        physicsSpace.distributeEvents();
    }

    /**
     * destroy the physics simulation
     */
    public void destroy(){
        physicsSystems.forEach(physicsSystem -> physicsSystem.destroy(entityData, this));

        physicsSpace.removeTickListener(this);
        ghostObjects.stop();
        rigidBodies.stop();

        physicsSpace.destroy();
        physicsSpace = null;
    }

    /**
     * Add a sub physics system which could be hooked in the life cycle
     * of this physics space.
     *
     * @param physicsSystem
     */
    public void addPhysicsSystem(PhysicsSystem physicsSystem){
        physicsSystem.initialize(entityData, this);
        physicsSystems.add(physicsSystem);
    }

    /**
     * Remove a sub physics system
     *
     * @param physicsSystem
     */
    public void removePhysicsSystem(PhysicsSystem physicsSystem){
        if(physicsSystems.remove(physicsSystem)){
            physicsSystem.destroy(entityData, this);
        }
    }

    /**
     * For debugging with BulletDebugAppState or advanced behavior.
     * E.G. attach your own event listeners and so on.
     * Be aware that every physics object should have an EntityId as UserObject attached.
     * Let the bullet system handle the creation of physics objects and use
     * the physics space read only.
     *
     * @return
     */
    public PhysicsSpace getPhysicsSpace(){
        return physicsSpace;
    }

    /**
     * Obtain a collection of all rigid bodies.
     *
     * @return
     */
    public RigidBodyContainer getRigidBodies() {
        return rigidBodies;
    }

    /**
     * Obtain a collection of all ghost objects
     *
     * @return
     */
    public GhostObjectContainer getGhostObjects() {
        return ghostObjects;
    }

    /**
     * Get the instance filter for this bullet system. If you are unsure whether
     * an entity belongs to this system use the filter.
     *
     * @return the insatnce filter or null if there is only one bullet system
     */
    public PhysicsInstanceFilter getInstanceFilter() {
        return instanceFilter;
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
}
