package com.jvpichowski.jme3.es;

import com.simsilica.es.EntityId;

/**
 * Contains a collection of objects with type T which are bound to a specific EntityId.
 */
public interface ObjectContainer<T> {

    /**
     *
     * @param entityId
     * @return the object which belongs to this EntityId or null if it doesn't exist.
     */
    T getObject(EntityId entityId);
}
