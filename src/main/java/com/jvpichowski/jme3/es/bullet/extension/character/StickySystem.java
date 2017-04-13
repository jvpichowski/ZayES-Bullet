package com.jvpichowski.jme3.es.bullet.extension.character;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.math.Vector3f;
import com.jvpichowski.jme3.es.bullet.components.AngularVelocity;
import com.jvpichowski.jme3.es.bullet.components.LinearVelocity;
import com.jvpichowski.jme3.es.bullet.components.WarpVelocity;
import com.simsilica.es.EntitySet;

/**
 * If a character is moving to fast over a hill peak it'll
 * jump because the velocity points in the air. This systems removes
 * the velocity in y-direction to lower the effect.
 *
 * The system depends on the InAirSystem.
 */
final class StickySystem extends BasePhysicsSystem {

    public StickySystem() {
        super(PhysicsCharacter.class, LinearVelocity.class, AngularVelocity.class, InAir.class);
    }

    @Override
    public void prePhysicsTick(PhysicsSpace space, float tpf) {
        EntitySet entities = getEntities();
        entities.applyChanges();
        entities.forEach(e -> {
            Vector3f velocity = e.get(LinearVelocity.class).getVelocity();
            if(velocity.y > 0){
                Vector3f newVelocity = velocity.clone();
                newVelocity.y = 0;
                e.set(new WarpVelocity(newVelocity, e.get(AngularVelocity.class).getVelocity()));
            }
        });
    }

    @Override
    public void physicsTick(PhysicsSpace space, float tpf) {

    }
}
