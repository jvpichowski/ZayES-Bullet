package com.jvpichowski.jme3.es.bullet.extension.forces;

import com.simsilica.es.EntityComponent;

/**
 * Represents a force which acts on an entity if it moves through a medium.
 *
 * https://en.wikipedia.org/wiki/Drag_coefficient
 */
public final class DragMedium implements EntityComponent {

    public static final float DENSITY_AIR = 1.184f;

    private final float area;
    private final float density;
    private final float coefficient;

    public DragMedium(float area, float density, float coefficient) {
        this.area = area;
        this.density = density;
        this.coefficient = coefficient;
    }

    public DragMedium() {
        this(0, 0, 0);
    }

    public float getArea() {
        return area;
    }

    public float getDensity() {
        return density;
    }

    public float getCoefficient() {
        return coefficient;
    }
}
