package com.jvpichowski.jme3.es.bullet.extension.forces;

import com.jme3.math.Vector3f;
import com.jvpichowski.jme3.es.bullet.components.ForceComponent;

/**
 * The resulting force of SimpleDrag
 */
final class SimpleDragForce implements ForceComponent {

    private final Vector3f force;

    public SimpleDragForce(Vector3f force) {
        this.force = force;
    }

    public SimpleDragForce() {
        this(new Vector3f());
    }

    @Override
    public Vector3f getForce() {
        return force;
    }

    /**
     *
     * @return null because it always acts on the center
     */
    @Override
    public Vector3f getLocation() {
        return null;
    }
}
