package com.jvpichowski.jme3.es.bullet.extension.forces;

import com.simsilica.es.EntityComponent;

/**
 * Results in a drag force which is described by the constant factor.
 */
public final class SimpleDrag implements EntityComponent {

    private final float factor;

    public SimpleDrag(float factor) {
        this.factor = factor;
    }

    public SimpleDrag() {
        this(0);
    }

    public float getFactor() {
        return factor;
    }
}
