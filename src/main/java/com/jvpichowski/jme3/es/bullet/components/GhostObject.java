package com.jvpichowski.jme3.es.bullet.components;

import com.simsilica.es.EntityComponent;

/**
 * Mark a physics entity as a ghost object. The bullet system
 * will create a new GhostObject for this entity which could
 * be accessed with from ghost object container.
 */
public final class GhostObject implements EntityComponent {

    public GhostObject() {
    }
}
