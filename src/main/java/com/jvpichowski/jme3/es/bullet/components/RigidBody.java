package com.jvpichowski.jme3.es.bullet.components;

import com.simsilica.es.EntityComponent;

/**
 * Created by Jan on 30.01.2017.
 */
public final class RigidBody implements EntityComponent {

    private final boolean kinematic;
    private final float mass;

    public RigidBody() {
        this(false, 0);
    }

    public RigidBody(boolean kinematic, float mass) {
        this.kinematic = kinematic;
        this.mass = mass;
    }

    public boolean isKinematic() {
        return kinematic;
    }

    public float getMass() {
        return mass;
    }
}
