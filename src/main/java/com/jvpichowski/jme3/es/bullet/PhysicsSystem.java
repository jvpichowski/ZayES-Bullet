package com.jvpichowski.jme3.es.bullet;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.simsilica.es.EntityData;

/**
 * Created by jan on 12.02.2017.
 */
public interface PhysicsSystem {


    void initialize(EntityData entityData, BulletSystem bulletSystem);


    void destroy(EntityData entityData, BulletSystem bulletSystem);

}
