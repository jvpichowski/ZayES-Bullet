package com.jvpichowski.jme3.es.bullet.extension.logic;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jvpichowski.jme3.es.bullet.BulletSystem;
import com.jvpichowski.jme3.es.logic.SimpleLogicManager;
import com.simsilica.es.EntityData;

/**
 * This logic manager contains two getters to SimpleLogicManagers
 * which are updated before and after a physics tick.
 */
public final class PhysicsSimpleLogicManager {

    private BulletSystem bulletSystem;
    private SimpleLogicManager preTickLogicManager;
    private SimpleLogicManager tickLogicManager;
    private PhysicsTickListener physicsTickListener = new PhysicsTickListener(){

        @Override
        public void prePhysicsTick(PhysicsSpace space, float tpf) {
            preTickLogicManager.update();
        }

        @Override
        public void physicsTick(PhysicsSpace space, float tpf) {
            tickLogicManager.update();
        }
    };

    public void initialize(EntityData entityData, BulletSystem bulletSystem){
        if(preTickLogicManager != null || tickLogicManager != null){
            throw new IllegalStateException("initialize was already called");
        }
        this.bulletSystem = bulletSystem;
        preTickLogicManager = new SimpleLogicManager();
        tickLogicManager = new SimpleLogicManager();
        preTickLogicManager.initialize(entityData);
        tickLogicManager.initialize(entityData);
        bulletSystem.getPhysicsSpace().addTickListener(physicsTickListener);
    }

    public void destroy(){
        bulletSystem.getPhysicsSpace().removeTickListener(physicsTickListener);
        tickLogicManager.destroy();
        preTickLogicManager.destroy();
        tickLogicManager = null;
        preTickLogicManager = null;
    }

    public SimpleLogicManager getPreTickLogicManager() {
        return preTickLogicManager;
    }

    public SimpleLogicManager getTickLogicManager() {
        return tickLogicManager;
    }
}
