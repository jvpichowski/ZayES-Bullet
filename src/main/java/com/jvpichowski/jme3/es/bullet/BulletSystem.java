package com.jvpichowski.jme3.es.bullet;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Vector3f;
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
    private EntitySet positionComponents;

    private EntitySet forces;

    public BulletSystem(EntityData entityData){
        this(entityData,
                new Vector3f(-10000f, -10000f, -10000f),
                new Vector3f(10000f, 10000f, 10000f),
                PhysicsSpace.BroadphaseType.DBVT);
    }

    public BulletSystem(EntityData entityData, Vector3f worldMin, Vector3f worldMax, PhysicsSpace.BroadphaseType broadphaseType){
        this.entityData = entityData;
        this.broadphaseType = broadphaseType;
        this.worldMin = worldMin;
        this.worldMax = worldMax;
        positionComponents = entityData.getEntities(PhysicsPositionComponent.class);
        forces = entityData.getEntities(PhysicsForceComponent.class);
        rigidBodies = new RigidBodyContainer(entityData);
        physicsSpace = new PhysicsSpace(worldMin, worldMax, broadphaseType);
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
        positionComponents.release();
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

    @Override
    public void prePhysicsTick(PhysicsSpace space, float tpf) {
        //update rigid bodies
        rigidBodies.update();
        //copy position to physics
        applyPhysicsPositions();
        //apply force
        applyForces();
    }

    @Override
    public void physicsTick(PhysicsSpace space, float tpf) {
        //copy position to es
        physicsSpace.getRigidBodyList().forEach(rigidBody ->
            entityData.setComponent((EntityId)rigidBody.getUserObject(),
                    new PhysicsPositionComponent(rigidBody.getPhysicsLocation(), rigidBody.getPhysicsRotation())));

        //safe changes to know at the next call which are updated by the user
        //positionComponents.applyChanges(); //not good because of multithreading
    }

    /**
     * forwards the user changes to the physics engine
     */
    private void applyPhysicsPositions(){
        positionComponents.applyChanges();
        //only from user changed entities? do I need to ask for added and remove too?
        //applyPhysicsPositions(positionComponents.getAddedEntities());
        //applyPhysicsPositions(positionComponents.getChangedEntities());
        positionComponents.forEach(entity -> {
            PhysicsRigidBody rigidBody = rigidBodies.getObject(entity.getId());
            if (rigidBody != null) {
                PhysicsPositionComponent positionInfo = entity.get(PhysicsPositionComponent.class);
                rigidBody.setPhysicsLocation(positionInfo.getLocation());
                rigidBody.setPhysicsRotation(positionInfo.getRotation());
            }
        });
    }

    private void applyForces(){
        forces.applyChanges();
        forces.forEach(entity -> {
            PhysicsForceComponent forceInfo = entity.get(PhysicsForceComponent.class);
            entityData.removeComponent(entity.getId(), PhysicsForceComponent.class); //set to 0?
            PhysicsRigidBody rigidBody = rigidBodies.getObject(entity.getId());
            if (rigidBody != null){
                //should be applied in physics tick listener?
                rigidBody.applyForce(forceInfo.getForce(), forceInfo.getLocation());
            }
        });

    }


    private class RigidBodyContainer extends EntityContainer<PhysicsRigidBody>{

        public RigidBodyContainer(EntityData ed) {
            super(ed, PhysicsRigidBodyComponent.class, CollisionShapeComponent.class);
        }

        @Override
        protected PhysicsRigidBody addObject(Entity e) {
            PhysicsRigidBodyComponent rigidBodyInfo = e.get(PhysicsRigidBodyComponent.class);
            CollisionShapeComponent collisionShapeInfo = e.get(CollisionShapeComponent.class);
            PhysicsRigidBody rigidBody = new PhysicsRigidBody(collisionShapeInfo.getCollisionShape(), rigidBodyInfo.getMass());
            rigidBody.setMass(rigidBodyInfo.getMass());
            rigidBody.setKinematic(rigidBodyInfo.isKinematic());
            rigidBody.setUserObject(e.getId());
            physicsSpace.add(rigidBody);
            return rigidBody;
        }

        @Override
        protected void updateObject(PhysicsRigidBody object, Entity e) {
            PhysicsRigidBodyComponent rigidBodyInfo = e.get(PhysicsRigidBodyComponent.class);
            CollisionShapeComponent collisionShapeInfo = e.get(CollisionShapeComponent.class);
            object.setKinematic(rigidBodyInfo.isKinematic());
            object.setMass(rigidBodyInfo.getMass());
            object.setCollisionShape(collisionShapeInfo.getCollisionShape());
//            object.setUserObject(e.getId()); already set?
        }

        @Override
        protected void removeObject(PhysicsRigidBody object, Entity e) {
            physicsSpace.remove(object);
        }
    }


}
