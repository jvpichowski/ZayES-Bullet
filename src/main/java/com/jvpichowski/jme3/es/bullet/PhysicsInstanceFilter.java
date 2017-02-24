package com.jvpichowski.jme3.es.bullet;

import com.jvpichowski.jme3.es.bullet.components.PhysicsInstance;
import com.simsilica.es.ComponentFilter;
import com.simsilica.es.EntityComponent;

/**
 * This system isn't fully integrated. Later it will be possible to have multiple
 * bullet systems which use this filter to determine if the entity belongs to them.
 * As a consequence every subsystem has to use this entity filter.
 */
public final class PhysicsInstanceFilter implements ComponentFilter<PhysicsInstance> {

    private int instance;

    public PhysicsInstanceFilter(int instance) {
        this.instance = instance;
    }

    @Override
    public Class<PhysicsInstance> getComponentType() {
        return PhysicsInstance.class;
    }

    @Override
    public boolean evaluate(EntityComponent c) {
        return ((PhysicsInstance)c).getInstance() == instance;
    }
}
