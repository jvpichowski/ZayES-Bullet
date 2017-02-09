package com.jvpichowski.jme3.es.bullet.components;

import com.jme3.math.Vector3f;
import com.simsilica.es.EntityComponent;

/**
 * Created by Jan on 08.02.2017.
 */
public final class LinearVelocity implements EntityComponent{

    private Vector3f linearVelocity;

    public LinearVelocity(Vector3f linearVelocity) {
        this.linearVelocity = linearVelocity;
    }

    public LinearVelocity() {
    }

    public Vector3f getLinearVelocity() {
        return linearVelocity;
    }
}
