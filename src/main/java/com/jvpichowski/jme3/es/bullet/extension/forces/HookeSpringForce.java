package com.jvpichowski.jme3.es.bullet.extension.forces;

import com.jme3.math.Vector3f;
import com.jvpichowski.jme3.es.bullet.components.ForceComponent;

/**
 * The force pulling which is created by the spring.
 */
final class HookeSpringForce implements ForceComponent {

    private final Vector3f force;
    private final Vector3f location;

    public HookeSpringForce(Vector3f force, Vector3f location) {
        this.force = force;
        this.location = location;
    }


    public HookeSpringForce() {
        this(new Vector3f(), null);
    }

    @Override
    public Vector3f getForce() {
        return force;
    }

    @Override
    public Vector3f getLocation() {
        return location;
    }
}
