package com.jvpichowski.jme3.es.bullet;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.simsilica.es.*;

/**
 * Created by Jan on 08.02.2017.
 */
public abstract class PhysicsController {

    private EntityContainer<PhysicsRigidBody> rigidBodies;
    private EntitySet entities;
    private EntityData entityData;

    final void initialize(EntityData entityData, EntityContainer<PhysicsRigidBody> rigidBodies){
        this.entityData = entityData;
        this.rigidBodies = rigidBodies;
        entities = entityData.getEntities(getType());
    }

    protected final EntityData getEntityData() {
        return entityData;
    }

    public abstract Class<? extends EntityComponent> getType();

    /**
     * Updates the state of the rigid body
     *
     * @param rigidBody
     * @param entity
     */
    public abstract void updatePhysics(PhysicsRigidBody rigidBody, Entity entity);

    /**
     * Updates the state of the entity system
     *
     * @param entityId
     * @param rigidBody
     */
    public abstract void updateData(EntityId entityId, PhysicsRigidBody rigidBody);

    final void prePhysicsTick(PhysicsSpace space, float tpf) {
        entities.applyChanges();
        entities.forEach(entity -> {
            PhysicsRigidBody rigidBody = rigidBodies.getObject(entity.getId());
            if (rigidBody != null) {
                updatePhysics(rigidBody, entity);
            }
        });
    }

    final void physicsTick(PhysicsSpace space, float tpf) {
        //entities.applyChanges();
        space.getRigidBodyList().forEach(rigidBody ->
                updateData((EntityId) rigidBody.getUserObject(), rigidBody));
    }

    final void destroy(){
        entities.release();
    }
}
