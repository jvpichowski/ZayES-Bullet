package com.jvpichowski.jme3.es.bullet;

import com.simsilica.es.EntityData;

/**
 * Created by jan on 12.02.2017.
 */
public interface PhysicsSystem {

    void initialize(EntityData entityData, BulletSystem bulletSystem);
    void destroy(EntityData entityData, BulletSystem bulletSystem);

}
