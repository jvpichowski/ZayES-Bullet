package com.jvpichowski.jme3.es.bullet.components;

import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jvpichowski.jme3.es.bullet.PhysicsController;
import com.jvpichowski.jme3.es.bullet.components.PhysicsPosition;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityContainer;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;

/**
 * Created by Jan on 08.02.2017.
 */
public final class PhysicsPositionController extends PhysicsController {

    @Override
    public Class<PhysicsPosition> getType() {
        return PhysicsPosition.class;
    }

    @Override
    public void updatePhysics(PhysicsRigidBody rigidBody, Entity entity) {
        PhysicsPosition positionInfo = entity.get(PhysicsPosition.class);
        rigidBody.setPhysicsLocation(positionInfo.getLocation());
        rigidBody.setPhysicsRotation(positionInfo.getRotation());
    }

    @Override
    public void updateData(EntityId entityId, PhysicsRigidBody rigidBody) {
        getEntityData().setComponent((EntityId) rigidBody.getUserObject(),
                new PhysicsPosition(rigidBody.getPhysicsLocation(), rigidBody.getPhysicsRotation()));
    }
}
