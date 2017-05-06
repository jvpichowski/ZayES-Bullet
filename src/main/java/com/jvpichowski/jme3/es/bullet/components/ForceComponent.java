package com.jvpichowski.jme3.es.bullet.components;

import com.jme3.math.Vector3f;
import com.simsilica.es.EntityComponent;

/**
 * Describes a linear force which acts on a physics object.
 * You have to implement this interface to register it to the
 * physics force system.
 * <br><br>
 * This force will only be cleared if it is set to zero or removed from the entity.
 */
public interface ForceComponent extends EntityComponent {

    /**
     * never return null!
     * @return the linear force which should be applied to the physics object
     */
    Vector3f getForce();

    /**
     *
     * @return the location where the force acts or null if it's an central force
     */
    Vector3f getLocation();
}
