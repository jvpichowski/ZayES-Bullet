package com.jvpichowski.jme3.es.bullet;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jvpichowski.jme3.es.bullet.components.CollisionShape;
import com.jvpichowski.jme3.es.bullet.components.RigidBody;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityContainer;
import com.simsilica.es.EntityData;

/**
 * Created by Jan on 08.02.2017.
 */
public final class RigidBodyContainer extends EntityContainer<PhysicsRigidBody> {

    private PhysicsSpace physicsSpace;

    RigidBodyContainer(EntityData ed, PhysicsSpace physicsSpace) {
        super(ed, RigidBody.class, CollisionShape.class);
        this.physicsSpace = physicsSpace;
    }

    @Override
    protected PhysicsRigidBody addObject(Entity e) {
        RigidBody rigidBodyInfo = e.get(RigidBody.class);
        CollisionShape collisionShapeInfo = e.get(CollisionShape.class);
        PhysicsRigidBody rigidBody = new PhysicsRigidBody(collisionShapeInfo.getCollisionShape(), rigidBodyInfo.getMass());
        rigidBody.setMass(rigidBodyInfo.getMass());
        rigidBody.setKinematic(rigidBodyInfo.isKinematic());
        rigidBody.setUserObject(e.getId());
        physicsSpace.add(rigidBody);
        return rigidBody;
    }

    @Override
    protected void updateObject(PhysicsRigidBody object, Entity e) {
        physicsSpace.remove(object);
        RigidBody rigidBodyInfo = e.get(RigidBody.class);
        CollisionShape collisionShapeInfo = e.get(CollisionShape.class);
        object.setKinematic(rigidBodyInfo.isKinematic());
        object.setMass(rigidBodyInfo.getMass());
        object.setCollisionShape(collisionShapeInfo.getCollisionShape());
        physicsSpace.add(object);
//            object.setUserObject(e.getId()); already set?
    }

    @Override
    protected void removeObject(PhysicsRigidBody object, Entity e) {
        physicsSpace.remove(object);
    }
}
