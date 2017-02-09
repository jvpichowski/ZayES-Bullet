package com.jvpichowski.jme3.es.bullet.components;

import com.simsilica.es.EntityComponent;
import com.simsilica.es.EntityId;

import java.util.List;
import java.util.Set;

/**
 * Created by Jan on 09.02.2017.
 */
public class Collision implements EntityComponent{

    private Set<EntityId> collisions;

    public Collision(Set<EntityId> collisions) {
        this.collisions = collisions;
    }

    public Set<EntityId> getCollisions() {
        return collisions;
    }
}
