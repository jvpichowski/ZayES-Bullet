package com.jvpichowski.jme3.es.bullet.components;

import com.jme3.math.Vector3f;
import com.simsilica.es.EntityComponent;

/**
 * Changes instantly the velocity of a physics object.
 */
public final class WarpVelocity implements EntityComponent {

    private final Vector3f linearVelocity;
    private final Vector3f angularVelocity;

    public WarpVelocity(Vector3f linearVelocity, Vector3f angularVelocity) {
        this.linearVelocity = linearVelocity;
        this.angularVelocity = angularVelocity;
    }

    public WarpVelocity() {
        this(new Vector3f(), new Vector3f());
    }

    public Vector3f getLinearVelocity() {
        return linearVelocity;
    }

    public Vector3f getAngularVelocity() {
        return angularVelocity;
    }
}
