package com.jvpichowski.jme3.es.bullet.components;

import com.jme3.math.Vector3f;
import com.simsilica.es.EntityComponent;

/**
 * Add a constant force to a physics object. This force won't
 * pull other physics objects to this object. It is just a force which
 * is applied every tick. Unlike all other force components this one
 * won't be removed in the next physics tick.
 */
public final class Gravity implements EntityComponent {

    private Vector3f force;

    public Gravity(Vector3f force) {
        this.force = force;
    }

    public Gravity() {
        this(new Vector3f());
    }

    public Vector3f getForce() {
        return force;
    }
}
