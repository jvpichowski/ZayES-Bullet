package com.jvpichowski.jme3.es.bullet.components;

import com.jme3.math.Vector3f;
import com.simsilica.es.EntityComponent;

/**
 * Apply the impulse once to a RigidBody. Every Impulse
 * will be added in the next physics tick and removed afterwards.
 */
public final class Impulse implements EntityComponent {

    private Vector3f impulse;
    private Vector3f relativeLocation;

    public Impulse(Vector3f impulse, Vector3f relativeLocation) {
        this.impulse = impulse;
        this.relativeLocation = relativeLocation;
    }

    public Impulse() {
        this.impulse = new Vector3f();
        this.relativeLocation = new Vector3f();
    }

    public Vector3f getImpulse() {
        return impulse;
    }

    public Vector3f getRelativeLocation() {
        return relativeLocation;
    }
}
