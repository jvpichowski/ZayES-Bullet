package com.jvpichowski.jme3.es.bullet.components;

import com.jme3.math.Vector3f;
import com.simsilica.es.EntityComponent;

/**
 * Set a central force to a physics object. It will be applied in the next physics tick
 * and then removed from the entity.
 */
public final class CentralForce implements EntityComponent {

    private Vector3f force;

    public CentralForce(Vector3f force) {
        this.force = force;
    }

    public CentralForce() {
        this.force = new Vector3f();
    }

    public Vector3f getForce() {
        return force;
    }
}
