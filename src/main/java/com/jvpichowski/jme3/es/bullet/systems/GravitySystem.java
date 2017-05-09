package com.jvpichowski.jme3.es.bullet.systems;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jvpichowski.jme3.es.ObjectContainer;
import com.jvpichowski.jme3.es.bullet.BulletSystem;
import com.jvpichowski.jme3.es.bullet.PhysicsInstanceFilter;
import com.jvpichowski.jme3.es.bullet.PhysicsSystem;
import com.jvpichowski.jme3.es.bullet.components.Gravity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;

/**
 * Applies the gravity components to the rigid bodies.
 */
public final class GravitySystem implements PhysicsSystem, PhysicsTickListener {

    private EntityData entityData;
    private ObjectContainer<PhysicsRigidBody> rigidBodies;
    private EntitySet gravities;

    @Override
    public void initialize(EntityData entityData, BulletSystem bulletSystem) {
        this.entityData = entityData;
        this.rigidBodies = bulletSystem.getRigidBodies();
        PhysicsInstanceFilter filter = bulletSystem.getInstanceFilter();
        if(filter == null) {
            this.gravities = entityData.getEntities(Gravity.class);
        }else{
            this.gravities = entityData.getEntities(filter, filter.getComponentType(), Gravity.class);
        }
        bulletSystem.addTickListener(this, true, true);
    }

    @Override
    public void destroy(EntityData entityData, BulletSystem bulletSystem) {
        bulletSystem.removeTickListener(this);
        gravities.release();
    }

    @Override
    public void prePhysicsTick(PhysicsSpace space, float tpf) {
        gravities.applyChanges();
        gravities.forEach(entity -> {
            PhysicsRigidBody rigidBody = rigidBodies.getObject(entity.getId());
            if(rigidBody != null){
                rigidBody.setGravity(entity.get(Gravity.class).getForce());
            }
        });
    }

    @Override
    public void physicsTick(PhysicsSpace space, float tpf) { }
}
