package com.jvpichowski.jme3.es.bullet.components;

import com.simsilica.es.EntityComponent;

/**
 * Set the friction of a physics object. It will be applied in the next
 * physics tick.
 *
 * For standard values look at:
 *
 * https://www.thoughtspike.com/friction-coefficients-for-bullet-physics/
 *
 */
public final class Friction implements EntityComponent {

    private final float friction;

    public Friction(float friction) {
        this.friction = friction;
    }

    /**
     * Default friction(could different from bullet): 1
     */
    public Friction() {
        this(1);
    }

    public float getFriction() {
        return friction;
    }
}
