package com.jvpichowski.jme3.es.bullet.components;

import com.simsilica.es.EntityComponent;

/**
 * Mark a physics entity as a rigid body. The bullet system
 * will create a new RigidBody for this entity which could
 * be accessed from the rigid body container.
 */
public final class RigidBody implements EntityComponent {

    private final boolean kinematic;
    private final float mass;

    private final float restitution;

    /**
     *
     * @param kinematic
     * @param mass
     * @param restitution not used yet
     */
    public RigidBody(boolean kinematic, float mass, float restitution) {
        this.kinematic = kinematic;
        this.mass = mass;
        this.restitution = restitution;
    }

    /**
     * Default: not kinematic, no mass, (zero restitution)
     */
    public RigidBody() {
        this(false, 0);
    }

    /**
     * Default: (zero restitution)
     *
     * @param kinematic
     * @param mass
     */
    public RigidBody(boolean kinematic, float mass) {
        this(kinematic, mass, 0);
    }

    public boolean isKinematic() {
        return kinematic;
    }

    public float getMass() {
        return mass;
    }

    public float getRestitution() {
        return restitution;
    }
}
