package com.jvpichowski.jme3.es.bullet.components;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.simsilica.es.EntityComponent;

/**
 * Created by Jan on 30.01.2017.
 */
public final class PhysicsPosition implements EntityComponent {

    private Quaternion rotation;
    private Vector3f location;

    public PhysicsPosition(Vector3f location, Quaternion rotation) {
        this.rotation = rotation;
        this.location = location;
    }

    public PhysicsPosition() {
        this.rotation = Quaternion.DIRECTION_Z.clone();
        this.location = new Vector3f();
    }

    public Quaternion getRotation() {
        return rotation;
    }

    public Vector3f getLocation() {
        return location;
    }
}
