package com.jvpichowski.jme3.es.bullet.components;

import com.simsilica.es.EntityComponent;

/**
 * Created by jan on 14.02.2017.
 */
public final class PhysicsInstance implements EntityComponent {

    private int instance;

    public PhysicsInstance(int instance) {
        this.instance = instance;
    }

    public PhysicsInstance() {
        this(0);
    }

    public int getInstance() {
        return instance;
    }
}
