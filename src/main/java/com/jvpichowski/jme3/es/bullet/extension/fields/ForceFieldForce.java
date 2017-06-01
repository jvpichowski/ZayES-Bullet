package com.jvpichowski.jme3.es.bullet.extension.fields;

import com.jme3.math.Vector3f;
import com.jvpichowski.jme3.es.bullet.components.ForceComponent;

/**
 *
 */
final class ForceFieldForce implements ForceComponent {

    private final Vector3f force;

    public ForceFieldForce(Vector3f factor, float mass) {
        force = factor.mult(mass);
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
