package com.jvpichowski.jme3.states.view;

import com.jme3.math.ColorRGBA;
import com.simsilica.es.EntityComponent;

/**
 * A simple shaded material definition
 */
public final class ShadedColor implements EntityComponent {

    private final ColorRGBA diffuse;

    public ShadedColor(ColorRGBA diffuse) {
        this.diffuse = diffuse;
    }

    public ShadedColor() {
        this(ColorRGBA.White);
    }

    public ColorRGBA getDiffuse() {
        return diffuse;
    }

}
