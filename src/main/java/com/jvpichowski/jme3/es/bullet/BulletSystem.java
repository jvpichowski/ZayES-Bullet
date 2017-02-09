package com.jvpichowski.jme3.es.bullet;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Vector3f;
import com.jvpichowski.jme3.es.bullet.components.Force;
import com.jvpichowski.jme3.es.bullet.components.ForceController;
import com.jvpichowski.jme3.es.bullet.components.LinearVelocityController;
import com.jvpichowski.jme3.es.bullet.components.PhysicsPositionController;
import com.simsilica.es.*;


/**
 * Created by Jan on 30.01.2017.
 */
public final class BulletSystem implements PhysicsTickListener{

    private PhysicsSpace.BroadphaseType broadphaseType;
    private Vector3f worldMin;
    private Vector3f worldMax;
    private PhysicsSpace physicsSpace = null;
    private EntityData entityData = null;

    private EntityContainer<PhysicsRigidBody> rigidBodies;
    private PhysicsController[] controllers;

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

        physicsSpace = new PhysicsSpace(worldMin, worldMax, broadphaseType);
        rigidBodies = new RigidBodyContainer(entityData, physicsSpace);
        for(PhysicsController controller : this.controllers){
            controller.initialize(entityData, rigidBodies);
        }

        physicsSpace.addTickListener(this);
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
        physicsSpace.removeTickListener(this);
        rigidBodies.stop();
        for(PhysicsController controller : controllers){
            controller.destroy();
        }
        physicsSpace.destroy();
        physicsSpace = null;
    }

    /**
     * For debugging with BulletDebugAppState
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


}
