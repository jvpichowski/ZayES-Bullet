package com.jvpichowski.jme3.es.bullet.components;

import com.simsilica.es.EntityComponent;
import com.simsilica.es.EntityId;

import java.util.List;
import java.util.Set;

/**
 * Obtain a list of collisions for this entity.
 * It is updated every physics tick.
 */
public final class Collision implements EntityComponent{

    private Set<EntityId> collisions;

    public Collision(Set<EntityId> collisions) {
        this.collisions = collisions;
    }

    /**
     * Don't change the content of this set. Read it only.
     *
     * @return
     */
    public Set<EntityId> getCollisions() {
        return collisions;
    }
}
