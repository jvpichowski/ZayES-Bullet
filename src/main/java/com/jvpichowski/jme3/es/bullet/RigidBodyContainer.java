package com.jvpichowski.jme3.es.bullet;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jvpichowski.jme3.es.ObjectContainer;
import com.jvpichowski.jme3.es.bullet.components.BoxShape;
import com.jvpichowski.jme3.es.bullet.components.CustomShape;
import com.jvpichowski.jme3.es.bullet.components.RigidBody;
import com.jvpichowski.jme3.es.bullet.components.SphereShape;
import com.simsilica.es.*;

/**
 * A collection for all RigidBodies.
 */
final class RigidBodyContainer implements ObjectContainer<PhysicsRigidBody> {

    private SingleTypeContainer<CustomShape> customShapes;
    private SingleTypeContainer<SphereShape> sphereShapes;
    private SingleTypeContainer<BoxShape> boxShapes;

    public RigidBodyContainer(EntityData ed, PhysicsSpace physicsSpace, CustomShapeFactory customShapeFactory){
        customShapes = new SingleTypeContainer<>(ed, physicsSpace, CustomShape.class,
                CollisionShapeProvider.customShapeProvider(customShapeFactory));
        sphereShapes = new SingleTypeContainer<>(ed, physicsSpace, SphereShape.class,
                CollisionShapeProvider.sphereShapeProvider());
        boxShapes = new SingleTypeContainer<>(ed, physicsSpace, BoxShape.class,
                CollisionShapeProvider.boxShapeProvider());
    }


    public RigidBodyContainer(EntityData ed, PhysicsSpace physicsSpace, CustomShapeFactory customShapeFactory, PhysicsInstanceFilter instanceFilter){
        customShapes = new SingleTypeContainer<>(ed, physicsSpace, CustomShape.class,
                CollisionShapeProvider.customShapeProvider(customShapeFactory), instanceFilter);
        sphereShapes = new SingleTypeContainer<>(ed, physicsSpace, SphereShape.class,
                CollisionShapeProvider.sphereShapeProvider(), instanceFilter);
        boxShapes = new SingleTypeContainer<>(ed, physicsSpace, BoxShape.class,
                CollisionShapeProvider.boxShapeProvider(), instanceFilter);
    }

    @Override
    public PhysicsRigidBody getObject(EntityId entityId) {
        PhysicsRigidBody body = customShapes.getObject(entityId);
        if(body != null){
            return body;
        }
        body = sphereShapes.getObject(entityId);
        if(body != null){
            return body;
        }
        body = boxShapes.getObject(entityId);
        return body;
    }

    public void start(){
        customShapes.start();
        sphereShapes.start();
        boxShapes.start();
    }

    public void update(){
        customShapes.update();
        sphereShapes.update();
        boxShapes.update();
    }

    public void stop(){
        customShapes.stop();
        sphereShapes.stop();
        boxShapes.stop();
    }

    /**
     * Contains a collection of rigid bodies.
     */
    private static final class SingleTypeContainer<T extends EntityComponent> extends EntityContainer<PhysicsRigidBody> {

        private PhysicsSpace physicsSpace;
        private CollisionShapeProvider<T> provider;
        private Class<T> shapeType;


        SingleTypeContainer(EntityData ed, PhysicsSpace physicsSpace, Class<T> shapeType, CollisionShapeProvider<T> provider, PhysicsInstanceFilter filter) {
            super(ed, filter, filter.getComponentType(), RigidBody.class, shapeType);
            this.physicsSpace = physicsSpace;
            this.provider = provider;
            this.shapeType = shapeType;
        }

        SingleTypeContainer(EntityData ed, PhysicsSpace physicsSpace, Class<T> shapeType, CollisionShapeProvider<T> provider) {
            super(ed, RigidBody.class, shapeType);
            this.physicsSpace = physicsSpace;
            this.provider =  provider;
            this.shapeType = shapeType;
        }

        @Override
        protected PhysicsRigidBody addObject(Entity e) {
            RigidBody rigidBodyInfo = e.get(RigidBody.class);
            CollisionShape shape = provider.getShape(e.get(shapeType));
            PhysicsRigidBody rigidBody = new PhysicsRigidBody(shape, rigidBodyInfo.getMass());
            rigidBody.setMass(rigidBodyInfo.getMass());
            rigidBody.setKinematic(rigidBodyInfo.isKinematic());
            //rigidBody.setFriction(rigidBodyInfo.getFriction());
            rigidBody.setRestitution(rigidBodyInfo.getRestitution());
            rigidBody.setUserObject(e.getId());
            physicsSpace.add(rigidBody);
            return rigidBody;
        }

        @Override
        protected void updateObject(PhysicsRigidBody rigidBody, Entity e) {
            RigidBody rigidBodyInfo = e.get(RigidBody.class);
            CustomShape collisionShapeInfo = e.get(CustomShape.class);
            rigidBody.setKinematic(rigidBodyInfo.isKinematic());
            rigidBody.setMass(rigidBodyInfo.getMass());
            //rigidBody.setFriction(rigidBodyInfo.getFriction());
            rigidBody.setRestitution(rigidBodyInfo.getRestitution());
            if(!rigidBody.getCollisionShape().equals(collisionShapeInfo.getDefinition())) {
                physicsSpace.remove(rigidBody);
                CollisionShape shape = provider.getShape(e.get(shapeType));
                rigidBody.setCollisionShape(shape);
                physicsSpace.add(rigidBody);
            }
    //            rigidBody.setUserObject(e.getId()); already set?
        }

        @Override
        protected void removeObject(PhysicsRigidBody object, Entity e) {
            physicsSpace.remove(object);
        }
    }
}
