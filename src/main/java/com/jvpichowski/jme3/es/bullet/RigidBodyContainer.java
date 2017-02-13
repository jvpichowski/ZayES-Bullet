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


    RigidBodyContainer(EntityData ed, PhysicsSpace physicsSpace, PhysicsInstanceFilter filter) {
        super(ed, filter, filter.getComponentType(), RigidBody.class, CollisionShape.class);
        this.physicsSpace = physicsSpace;
    }

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
        rigidBody.setFriction(rigidBodyInfo.getFriction());
        rigidBody.setRestitution(rigidBodyInfo.getRestitution());
        rigidBody.setUserObject(e.getId());
        physicsSpace.add(rigidBody);
        return rigidBody;
    }

    @Override
    protected void updateObject(PhysicsRigidBody rigidBody, Entity e) {
        RigidBody rigidBodyInfo = e.get(RigidBody.class);
        CollisionShape collisionShapeInfo = e.get(CollisionShape.class);
        rigidBody.setKinematic(rigidBodyInfo.isKinematic());
        rigidBody.setMass(rigidBodyInfo.getMass());
        rigidBody.setFriction(rigidBodyInfo.getFriction());
        rigidBody.setRestitution(rigidBodyInfo.getRestitution());
        if(!rigidBody.getCollisionShape().equals(collisionShapeInfo.getCollisionShape())) {
            physicsSpace.remove(rigidBody);
            rigidBody.setCollisionShape(collisionShapeInfo.getCollisionShape());
            physicsSpace.add(rigidBody);
        }
//            rigidBody.setUserObject(e.getId()); already set?
    }

    @Override
    protected void removeObject(PhysicsRigidBody object, Entity e) {
        physicsSpace.remove(object);
    }
}
