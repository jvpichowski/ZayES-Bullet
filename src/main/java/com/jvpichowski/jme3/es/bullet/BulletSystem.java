package com.jvpichowski.jme3.es.bullet;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.objects.PhysicsGhostObject;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Vector3f;
import com.jvpichowski.jme3.es.ObjectContainer;
import com.jvpichowski.jme3.es.bullet.systems.*;
import com.simsilica.es.*;

import java.util.LinkedList;
import java.util.List;


/**
 * The main system. Create an instance and call update to update
 * the physics simulation. Don't forget to call destroy in the end.
 */
public final class BulletSystem {

    private PhysicsInstanceFilter instanceFilter;

    private PhysicsSpace.BroadphaseType broadphaseType;
    private Vector3f worldMin;
    private Vector3f worldMax;
    private PhysicsSpace physicsSpace = null;
    private EntityData entityData = null;
    private CustomShapeFactory customShapeFactory;

    private RigidBodyContainer rigidBodies;
    private GhostObjectContainer ghostObjects;

    //This list contains all physics subsystems
    private List<PhysicsSystem> physicsSystems = new LinkedList<>();
    private OrderedPhysicsTickListener orderedTickListener = new OrderedPhysicsTickListener();

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
                PhysicsSpace.BroadphaseType.DBVT, 0, new CollisionShapeFactory(), systems);
    }

    public BulletSystem(EntityData entityData, int instance, PhysicsSystem... systems){
        this(entityData,
                new Vector3f(-10000f, -10000f, -10000f),
                new Vector3f(10000f, 10000f, 10000f),
                PhysicsSpace.BroadphaseType.DBVT, instance, new CollisionShapeFactory(), systems);
    }

    public BulletSystem(EntityData entityData, Vector3f worldMin, Vector3f worldMax,
                        PhysicsSpace.BroadphaseType broadphaseType, int instance, CustomShapeFactory customShapeFactory, PhysicsSystem... systems){
        if(instance > 0){
            this.instanceFilter = new PhysicsInstanceFilter(instance);
        }

        this.customShapeFactory = customShapeFactory;
        this.entityData = entityData;
        this.broadphaseType = broadphaseType;
        this.worldMin = worldMin;
        this.worldMax = worldMax;

        physicsSpace = new PhysicsSpace(worldMin, worldMax, broadphaseType);

        if(instanceFilter == null) {
            rigidBodies = new RigidBodyContainer(entityData, physicsSpace, customShapeFactory);
            ghostObjects = new GhostObjectContainer(entityData, physicsSpace, customShapeFactory);
        }else{
            rigidBodies = new RigidBodyContainer(entityData, physicsSpace, customShapeFactory, instanceFilter);
            ghostObjects = new GhostObjectContainer(entityData, physicsSpace, customShapeFactory, instanceFilter);
        }

        //will be called before every other tick listener
        physicsSpace.addTickListener(orderedTickListener);
        //physicsSpace.addCollisionObject();
        //physicsSpace.addCollisionGroupListener();

        rigidBodies.start();
        ghostObjects.start();

        //init all already added components
        //positionComponents.applyChanges();
        //applyPhysicsPositions(positionComponents);

        if(systems.length == 0){
            physicsSystems.add(new CollisionSystem());
            physicsSystems.add(new RigidBodyPropertySystem());
            physicsSystems.add(new PhysicsPositionSystem());
            physicsSystems.add(new VelocitySystem());
            physicsSystems.add(new ForceSystem());
            physicsSystems.add(new ImpulseSystem());
            physicsSystems.add(new GravitySystem());
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
        physicsSpace.distributeEvents(); //if you delete this you will get a memory leak!
    }

    /**
     * destroy the physics simulation
     */
    public void destroy(){
        physicsSystems.forEach(physicsSystem -> physicsSystem.destroy(entityData, this));

        physicsSpace.removeTickListener(orderedTickListener);
        ghostObjects.stop();
        rigidBodies.stop();

        physicsSpace.destroy();
        physicsSpace = null;
    }

    /**
     * Add a sub physics system which could be hooked in the life cycle
     * of this physics space. The system will be executed after all other systems.
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

    public CustomShapeFactory getCustomShapeFactory(){
        return customShapeFactory;
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
    public ObjectContainer<PhysicsRigidBody> getRigidBodies() {
        return rigidBodies;
    }

    /**
     * Obtain a collection of all ghost objects
     *
     * @return
     */
    public ObjectContainer<PhysicsGhostObject> getGhostObjects() {
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
     *
     * @return the extensible force system or null if no extensible force system is present
     */
    public ExtensibleForceSystem getForceSystem() {
        for (PhysicsSystem physicsSystem : physicsSystems) {
            if(physicsSystem instanceof ExtensibleForceSystem){
                return (ExtensibleForceSystem) physicsSystem;
            }
        }
        return null;
    }

    /**
     * Add a physics tick listener. This method helps to keep the order of execution clean.
     * It is recommend to use this method to register listeners over the method from the PhysicsSystem.
     *
     * @param listener
     * @param preTickLast false if the new prePhysicsTick listener should be called before each others, true if afterwards
     * @param tickLast false if the new physicsTick listener should be called before each other, true if afterwards
     */
    public void addTickListener(PhysicsTickListener listener, boolean preTickLast, boolean tickLast){
        orderedTickListener.preTickOrder.add(preTickLast ? orderedTickListener.preTickOrder.size() : 0, listener);
        orderedTickListener.tickOrder.add(tickLast ? orderedTickListener.tickOrder.size() : 0, listener);
    }

    public void removeTickListener(PhysicsTickListener listener){
        orderedTickListener.preTickOrder.remove(listener);
        orderedTickListener.tickOrder.remove(listener);
    }

    private final class OrderedPhysicsTickListener implements PhysicsTickListener {

        public List<PhysicsTickListener> preTickOrder = new LinkedList<>();
        public List<PhysicsTickListener> tickOrder = new LinkedList<>();

        /**
         * Called before the physics is actually stepped, use to apply forces etc.
         *
         * @param space the physics space
         * @param tpf   the time per frame in seconds
         */
        @Override
        public void prePhysicsTick(PhysicsSpace space, float tpf) {
            rigidBodies.update();
            ghostObjects.update();
            preTickOrder.forEach(l -> l.prePhysicsTick(space, tpf));
        }

        /**
         * Called after the physics has been stepped, use to check for forces etc.
         *
         * @param space the physics space
         * @param tpf   the time per frame in seconds
         */
        @Override
        public void physicsTick(PhysicsSpace space, float tpf) {
            tickOrder.forEach(l -> l.physicsTick(space, tpf));
        }
    }
}
