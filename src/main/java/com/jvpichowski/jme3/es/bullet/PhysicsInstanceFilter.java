package com.jvpichowski.jme3.es.bullet;

import com.jvpichowski.jme3.es.bullet.components.PhysicsInstance;
import com.simsilica.es.ComponentFilter;
import com.simsilica.es.EntityComponent;

/**
 * Created by jan on 14.02.2017.
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
