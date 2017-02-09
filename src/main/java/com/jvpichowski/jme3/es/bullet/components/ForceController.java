package com.jvpichowski.jme3.es.bullet.components;

import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jvpichowski.jme3.es.bullet.PhysicsController;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityComponent;
import com.simsilica.es.EntityId;

/**
 * Created by Jan on 08.02.2017.
 */
public final class ForceController extends PhysicsController {

    @Override
    public Class<? extends EntityComponent> getType() {
        return Force.class;
    }

    @Override
    public void updatePhysics(PhysicsRigidBody rigidBody, Entity entity) {
        Force forceInfo = entity.get(Force.class);
        rigidBody.applyForce(forceInfo.getForce(), forceInfo.getLocation());
        getEntityData().removeComponent(entity.getId(), Force.class);
    }

    @Override
    public void updateData(EntityId entityId, PhysicsRigidBody rigidBody) {

    }
}
