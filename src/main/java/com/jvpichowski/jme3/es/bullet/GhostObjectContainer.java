package com.jvpichowski.jme3.es.bullet;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.objects.PhysicsGhostObject;
import com.jvpichowski.jme3.es.ObjectContainer;
import com.jvpichowski.jme3.es.bullet.components.BoxShape;
import com.jvpichowski.jme3.es.bullet.components.CustomShape;
import com.jvpichowski.jme3.es.bullet.components.GhostObject;
import com.jvpichowski.jme3.es.bullet.components.SphereShape;
import com.simsilica.es.*;

/**
 * A collection for all GhostObjects.
 */
final class GhostObjectContainer implements ObjectContainer<PhysicsGhostObject> {


    private SingleTypeContainer<CustomShape> customShapes;
    private SingleTypeContainer<SphereShape> sphereShapes;
    private SingleTypeContainer<BoxShape> boxShapes;


    public GhostObjectContainer(EntityData ed, PhysicsSpace physicsSpace, CustomShapeFactory customShapeFactory, PhysicsInstanceFilter instanceFilter) {
        customShapes = new SingleTypeContainer<>(ed, physicsSpace, CustomShape.class,
                CollisionShapeProvider.customShapeProvider(customShapeFactory), instanceFilter);
        sphereShapes = new SingleTypeContainer<>(ed, physicsSpace, SphereShape.class,
                CollisionShapeProvider.sphereShapeProvider(), instanceFilter);
        boxShapes = new SingleTypeContainer<>(ed, physicsSpace, BoxShape.class,
                CollisionShapeProvider.boxShapeProvider(), instanceFilter);
    }


    public GhostObjectContainer(EntityData ed, PhysicsSpace physicsSpace, CustomShapeFactory customShapeFactory) {
        customShapes = new SingleTypeContainer<>(ed, physicsSpace, CustomShape.class,
                CollisionShapeProvider.customShapeProvider(customShapeFactory));
        sphereShapes = new SingleTypeContainer<>(ed, physicsSpace, SphereShape.class,
                CollisionShapeProvider.sphereShapeProvider());
        boxShapes = new SingleTypeContainer<>(ed, physicsSpace, BoxShape.class,
                CollisionShapeProvider.boxShapeProvider());
    }

    @Override
    public PhysicsGhostObject getObject(EntityId entityId) {
        PhysicsGhostObject ghost = customShapes.getObject(entityId);
        if(ghost != null){
            return ghost;
        }
        ghost = sphereShapes.getObject(entityId);
        if(ghost != null){
            return ghost;
        }
        ghost = boxShapes.getObject(entityId);
        return ghost;
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
     * Contains a collection of all rigid bodies.
     */
    private static final class SingleTypeContainer<T extends EntityComponent> extends EntityContainer<PhysicsGhostObject> {

        private PhysicsSpace physicsSpace;
        private CollisionShapeProvider<T> provider;
        private Class<T> shapeType;

        SingleTypeContainer(EntityData ed, PhysicsSpace physicsSpace, Class<T> shapeType, CollisionShapeProvider<T> provider, PhysicsInstanceFilter filter) {
            super(ed, filter, filter.getComponentType(), GhostObject.class, shapeType);
            this.physicsSpace = physicsSpace;
            this.shapeType = shapeType;
            this.provider = provider;
        }

        SingleTypeContainer(EntityData ed, PhysicsSpace physicsSpace, Class<T> shapeType, CollisionShapeProvider<T> provider) {
            super(ed, GhostObject.class, shapeType);
            this.physicsSpace = physicsSpace;
            this.shapeType = shapeType;
            this.provider = provider;

        }

        @Override
        protected PhysicsGhostObject addObject(Entity e) {
            PhysicsGhostObject ghostObject = new PhysicsGhostObject(provider.getShape(e.get(shapeType)));
            ghostObject.setUserObject(e.getId());
            physicsSpace.add(ghostObject);
            return ghostObject;
        }

        @Override
        protected void updateObject(PhysicsGhostObject object, Entity e) {
            physicsSpace.remove(object);
            object.setCollisionShape(provider.getShape(e.get(shapeType)));
            physicsSpace.add(object);
        }

        @Override
        protected void removeObject(PhysicsGhostObject object, Entity e) {
            physicsSpace.remove(object);
            //object.destroy(); ?
            //TODO destroy collision shape?
        }
    }
}
