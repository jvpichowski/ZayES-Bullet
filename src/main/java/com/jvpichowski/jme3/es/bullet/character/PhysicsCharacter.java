package com.jvpichowski.jme3.es.bullet.character;

import com.simsilica.es.EntityComponent;

/**
 * Created by jan on 20.02.2017.
 */
public final class PhysicsCharacter implements EntityComponent {

    private final float mass;
    private final float radius;
    private final float height;

    public PhysicsCharacter(float mass, float radius, float height) {
        this.mass = mass;
        this.radius = radius;
        this.height = height;
    }

    public PhysicsCharacter() {
        this(0,0,0);
    }

    public float getMass() {
        return mass;
    }

    public float getHeight() {
        return height;
    }

    public float getRadius() {
        return radius;
    }
}
