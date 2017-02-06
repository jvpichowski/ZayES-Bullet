package com.jvpichowski.jme3.es.bullet;

import com.simsilica.es.EntityComponent;
import com.simsilica.es.EntityId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Jan on 30.01.2017.
 */
public final class PhysicsGhostObject implements EntityComponent {

    private final List<EntityId> overlappingObjects;
    private final int overlappingCount;

    public PhysicsGhostObject() {
        this(new ArrayList<EntityId>(), 0);
    }

    public PhysicsGhostObject(List<EntityId> overlappingObjects, int overlappingCount) {
        //copy list for real immutability?
        this.overlappingObjects = overlappingObjects;
        this.overlappingCount = overlappingCount;
    }

    public List<EntityId> getOverlappingObjects() {
        return Collections.unmodifiableList(overlappingObjects);
    }

    public int getOverlappingCount() {
        return overlappingCount;
    }
}
