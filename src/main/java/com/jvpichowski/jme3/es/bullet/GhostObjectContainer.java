package com.jvpichowski.jme3.es.bullet;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.objects.PhysicsGhostObject;
import com.jvpichowski.jme3.es.bullet.components.CollisionShape;
import com.jvpichowski.jme3.es.bullet.components.GhostObject;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityContainer;
import com.simsilica.es.EntityData;

/**
 * Created by jan on 10.02.2017.
 */
public final class GhostObjectContainer extends EntityContainer<PhysicsGhostObject> {

    private PhysicsSpace physicsSpace;

    GhostObjectContainer(EntityData ed, PhysicsSpace physicsSpace) {
        super(ed, GhostObject.class, CollisionShape.class);
        this.physicsSpace = physicsSpace;
    }

    @Override
    protected PhysicsGhostObject addObject(Entity e) {
        PhysicsGhostObject ghostObject = new PhysicsGhostObject(e.get(CollisionShape.class).getCollisionShape());
        ghostObject.setUserObject(e.getId());
        physicsSpace.add(ghostObject);
        return ghostObject;
    }

    @Override
    protected void updateObject(PhysicsGhostObject object, Entity e) {
        physicsSpace.remove(object);
        object.setCollisionShape(e.get(CollisionShape.class).getCollisionShape());
        physicsSpace.add(object);
    }

    @Override
    protected void removeObject(PhysicsGhostObject object, Entity e) {
        physicsSpace.remove(object);
        //object.destroy(); ?
        //TODO destroy collision shape?
    }
}
