package com.jvpichowski.jme3.es.bullet.components;

import com.jme3.math.Vector3f;
import com.simsilica.es.EntityComponent;

/**
 * A box collision shape
 */
public final class BoxShape implements EntityComponent {

    private final Vector3f halfExtends;

    public BoxShape(Vector3f halfExtends) {
        this.halfExtends = halfExtends;
    }

    /**
     * Default halfExtends: (0.5, 0.5, 0.5)
     */
    public BoxShape() {
        this(new Vector3f(0.5f,0.5f,0.5f));
    }

    public Vector3f getHalfExtents() {
        return halfExtends;
    }
}
