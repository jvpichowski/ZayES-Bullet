package com.jvpichowski.jme3.es.bullet.components;

import com.simsilica.es.EntityComponent;

/**
 * This component isn't fully integrated. Later it will be
 * possible to have multiple physics spaces and bullet systems.
 * If no instance if this component is added to an entity it
 * will be added to the default physics system (possible now).
 * If a instance of this component is added to an entity it
 * will be added to the bullet system with the instance number
 * (not fully possible now).
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
