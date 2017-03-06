package com.jvpichowski.jme3.es.bullet.character;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.math.Vector3f;
import com.jvpichowski.jme3.es.bullet.components.CentralForce;
import com.jvpichowski.jme3.es.bullet.components.LinearVelocity;
import com.simsilica.es.EntitySet;

/**
 * Slows a character down if the movement is zero
 */
final class SlowDownSystem extends BasePhysicsSystem {

    public SlowDownSystem() {
        super(PhysicsCharacter.class, PhysicsCharacterMovement.class, LinearVelocity.class);
    }

    @Override
    public void prePhysicsTick(PhysicsSpace space, float tpf) {
        EntitySet entities = getEntities();
        entities.applyChanges();
        entities.forEach(entity -> {
            float mass = entity.get(PhysicsCharacter.class).getMass();
            Vector3f movement = entity.get(PhysicsCharacterMovement.class).getMovement();
            if(movement.lengthSquared() == 0) {
                Vector3f velocity = entity.get(LinearVelocity.class).getVelocity();
                Vector3f force = velocity.mult(mass).mult(-1).mult(velocity.length());
                force.y = 0;
                entity.set(new CentralForce(force));
            }
        });
    }

    @Override
    public void physicsTick(PhysicsSpace space, float tpf) {

    }
}
