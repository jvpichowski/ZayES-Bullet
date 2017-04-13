package com.jvpichowski.jme3.es.bullet.extension.character;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.math.Vector3f;
import com.jvpichowski.jme3.es.bullet.components.*;
import com.simsilica.es.EntitySet;

/**
 * Slows a character down if the movement is zero
 */
final class SlowDownSystem extends BasePhysicsSystem {

    public SlowDownSystem() {
        super(PhysicsCharacter.class, PhysicsCharacterMovement.class, LinearVelocity.class, AngularVelocity.class);
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
                velocity = velocity.clone();
                velocity.y = 0;
                if(velocity.length()> 0.1f) { //the remaining slow down will be handled by friction, it's better than pushing the body back and forth
                    Vector3f force;
                    if(velocity.length() < 5) { //guessed values. they seem to work pretty well
                        force = velocity.mult(mass).mult(-10).mult(velocity.length()); //10 is nearly the gravity
                    }else{
                        force = velocity.mult(mass).mult(-1).mult(velocity.length());
                    }
                    force.y = 0;
                    entity.set(new CentralForce(force));
                    //TODO only slow down if on ground
                }
            }
        });
    }

    @Override
    public void physicsTick(PhysicsSpace space, float tpf) {
        //bad solution because other forces than movement can't affect character
        /*EntitySet entities = getEntities();
        entities.applyChanges();
        entities.forEach(entity -> {
            Vector3f movement = entity.get(PhysicsCharacterMovement.class).getMovement();
            if(movement.equals(Vector3f.ZERO)){
                Vector3f velocity = entity.get(LinearVelocity.class).getVelocity();
                velocity.clone();
                velocity.x = 0;
                velocity.z = 0;
                entity.set(new WarpVelocity(velocity, entity.get(AngularVelocity.class).getVelocity()));
            }
        });*/
    }
}
