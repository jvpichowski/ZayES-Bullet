package com.jvpichowski.jme3.states.view;

import com.jme3.math.ColorRGBA;
import com.simsilica.es.EntityComponent;

/**
 * Set an unshaded color on an object
 */
public final class UnshadedColor implements EntityComponent {

    private final ColorRGBA color;

    public UnshadedColor(ColorRGBA color) {
        this.color = color;
    }

    public UnshadedColor() {
        this(ColorRGBA.White);
    }

    public ColorRGBA getColor() {
        return color;
    }
}
