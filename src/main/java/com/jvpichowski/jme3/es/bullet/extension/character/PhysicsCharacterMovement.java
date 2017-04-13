package com.jvpichowski.jme3.es.bullet.extension.character;

import com.jme3.math.Vector3f;
import com.simsilica.es.EntityComponent;

/**
 * Created by jan on 20.02.2017.
 */
public final class PhysicsCharacterMovement implements EntityComponent {

    private final Vector3f movement;

    public PhysicsCharacterMovement(Vector3f movement) {
        this.movement = movement;
    }

    public PhysicsCharacterMovement() {
        this(new Vector3f());
    }

    public Vector3f getMovement() {
        return movement;
    }
}
