package com.jvpichowski.jme3.states;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jvpichowski.jme3.es.bullet.BulletSystem;
import com.simsilica.es.EntityData;

/**
 *
 *
 * Created by Jan on 06.02.2017.
 */
public final class ESBulletState extends BaseAppState {

    //TODO add threading like in BulletAppState

    private EntityData entityData;
    private BulletSystem bulletSystem;

    public ESBulletState(EntityData entityData) {
        this.entityData = entityData;
    }

    @Override
    protected void initialize(Application app) {
        bulletSystem = new BulletSystem(entityData);
    }

    @Override
    protected void cleanup(Application app) {
        bulletSystem.destroy();
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        if(isEnabled()){
            bulletSystem.update(tpf);
        }
    }

    public PhysicsSpace getPhysicsSpace(){
        return bulletSystem.getPhysicsSpace();
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }
}
