package com.jvpichowski.jme3.es.bullet.character;

import com.jme3.math.Vector3f;
import com.simsilica.es.EntityComponent;

/**
 * Created by jan on 20.02.2017.
 */
public final class Movement implements EntityComponent {

    private final Vector3f movement;

    public Movement(Vector3f movement) {
        this.movement = movement;
    }

    public Movement() {
        this(new Vector3f());
    }

    public Vector3f getMovement() {
        return movement;
    }
}
