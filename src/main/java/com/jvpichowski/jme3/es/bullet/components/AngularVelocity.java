package com.jvpichowski.jme3.es.bullet.components;

import com.jme3.math.Vector3f;
import com.simsilica.es.EntityComponent;

/**
 * Created by Jan on 08.02.2017.
 */
public final class AngularVelocity implements EntityComponent {

    private Vector3f velocity;

    public AngularVelocity(Vector3f velocity) {
        this.velocity = velocity;
    }

    public AngularVelocity() {
        this.velocity = new Vector3f();
    }

    public Vector3f getVelocity() {
        return velocity;
    }
}
