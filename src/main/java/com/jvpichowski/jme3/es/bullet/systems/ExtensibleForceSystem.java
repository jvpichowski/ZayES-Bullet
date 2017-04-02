package com.jvpichowski.jme3.es.bullet.systems;

import com.jvpichowski.jme3.es.bullet.components.TorqueComponent;
import com.jvpichowski.jme3.es.bullet.components.ForceComponent;

/**
 * By default you can specify one Force component for each entity.
 * Usually there are more forces than one. You either can combine them
 * yourself and use the default Force component to inform the bullet engine
 * or you create custom force components which could be registered to
 * the force system. Then the force system combines the forces and you do
 * not need to worry about it. The force systems which implement this
 * interface provides the functionality to register custom forces.
 */
public interface ExtensibleForceSystem {

    void registerForce(Class<? extends ForceComponent> forceComponent);

    void registerTorque(Class<? extends TorqueComponent> torqueComponent);

    void unregisterForce(Class<? extends ForceComponent> forceComponent);

    void unregisterTorque(Class<? extends TorqueComponent> torqueComponent);
}
