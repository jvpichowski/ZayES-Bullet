package com.jvpichowski.jme3.es.bullet.components;


import com.jme3.math.Vector3f;
import com.simsilica.es.EntityComponent;

/**
 * Apply the force to a RigidBody.
 * The force will be applied in the next physics tick
 * and then removed from the entity.
 * Be aware that a force consists of mass and acceleration.
 * To lift 10kg against the gravity you need at least 10*10N
 */
public final class Force implements EntityComponent {

    private final Vector3f force;
    private final Vector3f location;

    public Force(Vector3f force, Vector3f location) {
        this.force = force;
        this.location = location;
    }

    public Force() {
        this(new Vector3f(), new Vector3f());
    }

    public Vector3f getForce() {
        return force;
    }

    public Vector3f getLocation() {
        return location;
    }
}
