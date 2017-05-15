package com.jvpichowski.jme3.states.view;

import com.jme3.math.Quaternion;
import com.simsilica.es.EntityComponent;

/**
 *
 */
public final class ViewRotation implements EntityComponent {

    private final Quaternion rotation;

    public ViewRotation(Quaternion rotation) {
        this.rotation = rotation;
    }

    public ViewRotation() {
        this(Quaternion.DIRECTION_Z);
    }

    public Quaternion getRotation() {
        return rotation;
    }
}
