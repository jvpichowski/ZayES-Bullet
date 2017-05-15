package com.jvpichowski.jme3.states.view;

import com.jme3.math.Vector3f;
import com.simsilica.es.EntityComponent;

/**
 * The location of the object in the view
 */
public final class ViewLocation implements EntityComponent {

    private final Vector3f location;

    public ViewLocation(Vector3f location) {
        this.location = location;
    }

    public ViewLocation() {
        this(Vector3f.ZERO);
    }

    public Vector3f getLocation() {
        return location;
    }
}
