package com.jvpichowski.jme3.es.bullet.character;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jvpichowski.jme3.es.bullet.BulletSystem;
import com.jvpichowski.jme3.es.bullet.PhysicsSystem;
import com.simsilica.es.EntityComponent;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntitySet;

/**
 * Created by jan on 06.03.2017.
 */
abstract class BasePhysicsSystem implements PhysicsTickListener, PhysicsSystem {

    private EntitySet entities;

    private final Class<? extends EntityComponent>[] components;

    private BasePhysicsSystem() {
        this(new Class[0]);
    }

    public BasePhysicsSystem(Class<? extends EntityComponent>... components){
        this.components = components;
    }

    @Override
    public void initialize(EntityData entityData, BulletSystem bulletSystem) {
        entities = entityData.getEntities(components);
        bulletSystem.getPhysicsSpace().addTickListener(this);
    }

    @Override
    public void destroy(EntityData entityData, BulletSystem bulletSystem) {
        bulletSystem.getPhysicsSpace().removeTickListener(this);
        entities.release();
    }

    public EntitySet getEntities() {
        return entities;
    }
}
