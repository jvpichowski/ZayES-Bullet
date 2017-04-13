package com.jvpichowski.jme3.es.bullet.extension.character;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.math.Vector3f;
import com.jvpichowski.jme3.es.bullet.BulletSystem;
import com.jvpichowski.jme3.es.bullet.PhysicsSystem;
import com.jvpichowski.jme3.es.bullet.components.AngularVelocity;
import com.jvpichowski.jme3.es.bullet.components.LinearVelocity;
import com.jvpichowski.jme3.es.bullet.components.WarpVelocity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntitySet;

/**
 * Clamping the velocity is not the best solution because when you are accelerated by external
 * forces like an explosion you should be faster than moving speed.
 */
final class ClampVelocitySystem implements PhysicsSystem, PhysicsTickListener {

    private EntitySet characters;

    @Override
    public void initialize(EntityData entityData, BulletSystem bulletSystem) {
        characters = entityData.getEntities(PhysicsCharacter.class, LinearVelocity.class, AngularVelocity.class, PhysicsCharacterMovement.class);
        bulletSystem.getPhysicsSpace().addTickListener(this);
    }

    @Override
    public void destroy(EntityData entityData, BulletSystem bulletSystem) {
        bulletSystem.getPhysicsSpace().removeTickListener(this);
        characters.release();
    }

    @Override
    public void prePhysicsTick(PhysicsSpace space, float tpf) {

    }

    @Override
    public void physicsTick(PhysicsSpace space, float tpf) {
        characters.applyChanges();
        characters.forEach(entity -> {
            Vector3f wantedMovement = entity.get(PhysicsCharacterMovement.class).getMovement();
            Vector3f realMovement = entity.get(LinearVelocity.class).getVelocity();
            //if wanted length == 0 then the projection will result in NAN and be smaller than wanted
            if(realMovement.project(wantedMovement).lengthSquared() > wantedMovement.lengthSquared()){
                realMovement = wantedMovement.project(realMovement);
                entity.set(new WarpVelocity(realMovement, entity.get(AngularVelocity.class).getVelocity()));
            }
        });
    }
}
