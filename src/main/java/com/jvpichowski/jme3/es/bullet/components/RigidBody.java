package com.jvpichowski.jme3.es.bullet.components;

import com.simsilica.es.EntityComponent;

/**
 * Created by Jan on 30.01.2017.
 */
public final class RigidBody implements EntityComponent {

    private final boolean kinematic;
    private final float mass;

    private final float friction;
    private final float restitution;

    public RigidBody(boolean kinematic, float mass, float friction, float restitution) {
        this.kinematic = kinematic;
        this.mass = mass;
        this.friction = friction;
        this.restitution = restitution;
    }

    public RigidBody() {
        this(false, 0);
    }

    public RigidBody(boolean kinematic, float mass) {
        this(kinematic, mass, 0, 0);
    }

    public boolean isKinematic() {
        return kinematic;
    }

    public float getMass() {
        return mass;
    }

    public float getFriction() {
        return friction;
    }

    public float getRestitution() {
        return restitution;
    }
}
