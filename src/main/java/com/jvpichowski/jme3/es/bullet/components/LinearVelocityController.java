package com.jvpichowski.jme3.es.bullet.components;

import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jvpichowski.jme3.es.bullet.PhysicsController;
import com.simsilica.es.*;

/**
 * Created by Jan on 08.02.2017.
 */
public final class LinearVelocityController extends PhysicsController {

    @Override
    public Class<? extends EntityComponent> getType() {
        return LinearVelocity.class;
    }

    @Override
    public void updatePhysics(PhysicsRigidBody rigidBody, Entity entity) {
        rigidBody.setLinearVelocity(entity.get(LinearVelocity.class).getLinearVelocity());
    }

    @Override
    public void updateData(EntityId entityId, PhysicsRigidBody rigidBody) {
        getEntityData().setComponent(entityId, new LinearVelocity(rigidBody.getLinearVelocity()));
    }
}
