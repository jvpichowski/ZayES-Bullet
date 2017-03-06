package com.jvpichowski.jme3.es.bullet.character;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.math.Vector3f;
import com.jvpichowski.jme3.es.bullet.components.CentralForce;
import com.jvpichowski.jme3.es.bullet.components.LinearVelocity;
import com.simsilica.es.EntitySet;

/**
 * Accelerates a character until the movement speed is reached
 */
final class CharacterAccelerationSystem extends BasePhysicsSystem {

    public CharacterAccelerationSystem() {
        super(PhysicsCharacter.class, PhysicsCharacterMovement.class, LinearVelocity.class);
    }

    @Override
    public void prePhysicsTick(PhysicsSpace space, float tpf) {
        EntitySet entities = getEntities();
        entities.applyChanges();
        entities.forEach(entity -> {
            float mass = entity.get(PhysicsCharacter.class).getMass();
            Vector3f movement = entity.get(PhysicsCharacterMovement.class).getMovement();
            Vector3f move2d = new Vector3f(movement.x, 0f, movement.z);
            Vector3f velocity = entity.get(LinearVelocity.class).getVelocity();
            Vector3f velo2d = new Vector3f(velocity.x, 0, velocity.z);
            //TODO remove 2d calculations they are unnecessary detailed here
            //this is a simple approach. It doesn't use the direction of the movement and the velocity
            float diffSquared = move2d.lengthSquared() - velo2d.project(move2d).lengthSquared();
            if(!Float.isNaN(diffSquared)){
                if(diffSquared > 0) { //only add a force if the body is too slow. Slow down will be handled in a different system
                    Vector3f force = movement.mult(mass).mult(diffSquared); //TODO 2d?
                    entity.set(new CentralForce(force));
                }
            }
        });
    }

    @Override
    public void physicsTick(PhysicsSpace space, float tpf) {

    }
}
