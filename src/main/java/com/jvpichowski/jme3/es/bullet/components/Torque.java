package com.jvpichowski.jme3.es.bullet.components;

import com.jme3.math.Vector3f;
import com.simsilica.es.EntityComponent;

/**
 * Created by Jan on 08.02.2017.
 */
public final class Torque implements EntityComponent{

    private Vector3f torque;

    public Torque(Vector3f torque) {
        this.torque = torque;
    }

    public Torque() {
        this.torque = new Vector3f();
    }

    public Vector3f getTorque() {
        return torque;
    }
}
