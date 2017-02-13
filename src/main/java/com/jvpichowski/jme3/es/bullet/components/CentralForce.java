package com.jvpichowski.jme3.es.bullet.components;

import com.jme3.math.Vector3f;
import com.simsilica.es.EntityComponent;

/**
 * Created by Jan on 08.02.2017.
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
