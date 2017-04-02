package com.jvpichowski.jme3.es.bullet.components;

import com.jme3.math.Vector3f;
import com.simsilica.es.EntityComponent;

/**
 * Apply the torque to a RigidBody.
 * The torque will be applied in the next physics tick
 * and then removed from the entity.
 * Be aware that a torque consists of a moment of inertia
 * and angular acceleration.
 */
public final class Torque implements TorqueComponent{

    private Vector3f torque;

    public Torque(Vector3f torque) {
        this.torque = torque;
    }

    public Torque() {
        this.torque = new Vector3f();
    }

    @Override
    public Vector3f getTorque() {
        return torque;
    }
}
