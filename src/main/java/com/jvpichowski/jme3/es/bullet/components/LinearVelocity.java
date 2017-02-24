package com.jvpichowski.jme3.es.bullet.components;

import com.jme3.math.Vector3f;
import com.simsilica.es.EntityComponent;

/**
 * Get the linear velocity of a physics object. This
 * is only a getter and it has no impact on the
 * physics system if you change it. Be aware that
 * other systems might depend on this component
 * if you consider to overwrite this component.
 */
public final class LinearVelocity implements EntityComponent{

    private Vector3f velocity;

    public LinearVelocity(Vector3f velocity) {
        this.velocity = velocity;
    }

    public LinearVelocity() {
        this.velocity = new Vector3f();
    }

    public Vector3f getVelocity() {
        return velocity;
    }
}
