package com.jvpichowski.jme3.es.bullet.extension.forces;

import com.jme3.math.Vector3f;
import com.simsilica.es.EntityComponent;

/**
 * A simple spring. If the physics location of the entity is not
 * the relaxed location it will be pulled towards the relaxed location.
 */
public final class HookeSpring implements EntityComponent {

    private final Vector3f connectionLocation;
    private final Vector3f equilibriumLocation;
    private final float constant;

    public HookeSpring(Vector3f connectionLocation, Vector3f equilibriumLocation, float constant) {
        this.connectionLocation = connectionLocation;
        this.equilibriumLocation = equilibriumLocation;
        this.constant = constant;
    }

    public HookeSpring() {
        this(null, new Vector3f(), 0);
    }

    public Vector3f getEquilibriumLocation() {
        return equilibriumLocation;
    }

    public float getConstant() {
        return constant;
    }

    public Vector3f getConnectionLocation() {
        return connectionLocation;
    }
}
