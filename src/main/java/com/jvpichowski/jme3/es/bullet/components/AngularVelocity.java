package com.jvpichowski.jme3.es.bullet.components;

import com.jme3.math.Vector3f;
import com.simsilica.es.EntityComponent;

/**
 * Read this component to obtain the angular velocity of the physics object.
 * It is updated every physics tick.
 */
public final class AngularVelocity implements EntityComponent {

    private Vector3f velocity;

    public AngularVelocity(Vector3f velocity) {
        this.velocity = velocity;
    }

    public AngularVelocity() {
        this.velocity = new Vector3f();
    }

    public Vector3f getVelocity() {
        return velocity;
    }
}
