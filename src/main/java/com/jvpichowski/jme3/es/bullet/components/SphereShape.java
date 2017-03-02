package com.jvpichowski.jme3.es.bullet.components;

import com.simsilica.es.EntityComponent;

/**
 * A sphere collision shape
 */
public final class SphereShape implements EntityComponent {

    private final float radius;

    public SphereShape(float radius) {
        this.radius = radius;
    }

    /**
     * Default radius: 0.5
     */
    public SphereShape() {
        this(0.5f);
    }

    public float getRadius() {
        return radius;
    }
}
