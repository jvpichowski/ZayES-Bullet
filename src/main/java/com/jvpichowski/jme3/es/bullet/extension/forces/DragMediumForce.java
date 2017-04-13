package com.jvpichowski.jme3.es.bullet.extension.forces;

import com.jme3.math.Vector3f;
import com.jvpichowski.jme3.es.bullet.components.ForceComponent;

/**
 * The force which will act on the entity duo to the drag medium
 */
final class DragMediumForce implements ForceComponent {

    private Vector3f force;

    public DragMediumForce(Vector3f force) {
        this.force = force;
    }

    public DragMediumForce() {
        this(new Vector3f());
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
