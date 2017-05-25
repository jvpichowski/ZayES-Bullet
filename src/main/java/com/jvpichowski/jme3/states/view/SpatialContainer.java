package com.jvpichowski.jme3.states.view;

import com.jme3.scene.Spatial;
import com.simsilica.es.EntityId;

/**
 * A base interface for spatial containers
 */
interface SpatialContainer {

    /**
     *
     */
    void start();

    /**
     *
     * @return
     */
    boolean update();

    /**
     *
     */
    void stop();

    /**
     *
     */
    void destroy();

    /**
     *
     * @param id
     * @return
     */
    Spatial getObject(EntityId id);
}
