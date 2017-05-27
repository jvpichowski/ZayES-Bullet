package com.jvpichowski.jme3.es.bullet.extension.character;

import com.jme3.math.Vector3f;
import com.jvpichowski.jme3.es.bullet.components.ForceComponent;

/**
 *
 */
final class PhysicsCharacterMovementForce implements ForceComponent {

    private final Vector3f force;

    public PhysicsCharacterMovementForce(Vector3f force) {
        this.force = force;
    }

    @Override
    public Vector3f getForce() {
        return force;
    }

    @Override
    public Vector3f getLocation() {
        return null;
    }
}
