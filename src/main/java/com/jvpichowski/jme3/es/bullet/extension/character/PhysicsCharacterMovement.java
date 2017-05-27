package com.jvpichowski.jme3.es.bullet.extension.character;

import com.jme3.math.Vector2f;
import com.simsilica.es.EntityComponent;

/**
 * Set the movement of the character
 */
public final class PhysicsCharacterMovement implements EntityComponent {

    private final Vector2f direction;

    /**
     *
     * @param direction between 0 and 1
     */
    public PhysicsCharacterMovement(Vector2f direction) {
        this.direction = direction;
    }

    public PhysicsCharacterMovement() {
        this(Vector2f.ZERO);
    }

    public Vector2f getDirection() {
        return direction;
    }

}
