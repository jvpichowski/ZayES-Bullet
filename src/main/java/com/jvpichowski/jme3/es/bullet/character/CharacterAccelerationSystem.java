package com.jvpichowski.jme3.es.bullet.character;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.math.FastMath;
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
            if(!Float.isNaN(diffSquared)){ //NAN if movement = (0,0,0)
                if(diffSquared > 0) { //only add a force if the body is too slow. Slow down will be handled in a different system
                    Vector3f force;
                    if(diffSquared < 1){
                        force = movement.mult(mass);//.mult(FastMath.sqr(diffSquared)*10);
                    }else{
                        force = movement.mult(mass).mult(diffSquared);
                    }
                    //force.y = 0; commented out because the user should be able to add jumps

                    //let the force point towards the ground will reduce the jumping if you start running from
                    //a hill but it will make the effect of the friction bigger
                    //Vector3f forceDir = force.normalize();
                    //forceDir.y = -0.5f;
                    //force = force.project(forceDir);
                    entity.set(new CentralForce(force));
                }
            }
        });
    }

    @Override
    public void physicsTick(PhysicsSpace space, float tpf) {

    }
}
