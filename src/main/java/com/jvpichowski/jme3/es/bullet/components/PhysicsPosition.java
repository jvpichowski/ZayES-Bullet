package com.jvpichowski.jme3.es.bullet.components;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.simsilica.es.EntityComponent;

/**
 * Get the position in the physics space of a physics object. This
 * is only a getter and it has no impact on the
 * physics system if you change it. Be aware that
 * other systems might depend on this component
 * if you consider to overwrite this component.
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
