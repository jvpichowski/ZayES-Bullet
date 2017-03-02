package com.jvpichowski.jme3.es.bullet;

import com.simsilica.es.EntityData;

/**
 * Every physics subsystem can implement this interface to getShape hooked
 * into the life cycle of the physics space. Be aware that you have to use the
 * PhysicsInstanceFilter if you want this system to support multiple
 * physics instances.
 */
public interface PhysicsSystem {

    /**
     * Called right after this system is added to the bullet system
     * or right after the initialization of the bullet system if it
     * is attached beforehand.
     *
     * @param entityData
     * @param bulletSystem
     */
    void initialize(EntityData entityData, BulletSystem bulletSystem);

    /**
     * Called right after it is removed from the bullet system or
     * before the bullet system will be destroyed.
     *
     * @param entityData
     * @param bulletSystem
     */
    void destroy(EntityData entityData, BulletSystem bulletSystem);

}
