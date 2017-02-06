package com.jvpichowski.jme3.es.bullet;

import com.simsilica.es.EntityComponent;

/**
 * Created by Jan on 30.01.2017.
 */
public final class PhysicsRigidBodyComponent implements EntityComponent {

    private final boolean kinematic;
    private final float mass;

    public PhysicsRigidBodyComponent() {
        this(false, 0);
    }

    public PhysicsRigidBodyComponent(boolean kinematic, float mass) {
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
