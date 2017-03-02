package com.jvpichowski.jme3.es.bullet.components;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.simsilica.es.EntityComponent;

/**
 * Moves a physics object to a certain location and applies a certain rotation to it.
 * It is like a warp and the physics object will instantly switch into the new state
 */
public final class WarpPosition implements EntityComponent {

    private final Vector3f location;
    private final Quaternion rotation;

    public WarpPosition(Vector3f location, Quaternion rotation) {
        this.location = location;
        this.rotation = rotation;
    }

    public WarpPosition() {
        this(new Vector3f(), new Quaternion());
    }

    public Vector3f getLocation() {
        return location;
    }

    public Quaternion getRotation() {
        return rotation;
    }
}
