package com.jvpichowski.jme3.es.bullet.components;

import com.jme3.math.Vector3f;
import com.simsilica.es.EntityComponent;

/**
 * Apply the torque impulse once to a RigidBody. Every torque impulse
 * will be added in the next physics tick and removed afterwards.
 */
public final class TorqueImpulse implements EntityComponent {

    private Vector3f impulse;

    public TorqueImpulse(Vector3f impulse) {
        this.impulse = impulse;
    }

    public TorqueImpulse() {
        this.impulse = new Vector3f();
    }

    public Vector3f getImpulse() {
        return impulse;
    }
}
