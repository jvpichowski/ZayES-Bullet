package com.jvpichowski.jme3.es.bullet.components;

import com.jme3.math.Vector3f;
import com.simsilica.es.EntityComponent;

/**
 * Describes an angular force which acts on a physics object.
 * YOu have to implement this interface to register a new force to the
 * physics force system.
 * <br><br>
 * This torque will only be cleared if it is set to zero or removed from the entity.
 */
public interface TorqueComponent extends EntityComponent {

    /**
     *
     * @return the angular force which acts on a physics object
     */
    Vector3f getTorque();
}
