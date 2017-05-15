package com.jvpichowski.jme3.states;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jvpichowski.jme3.es.logic.BaseSimpleEntityLogic;
import com.jvpichowski.jme3.es.logic.SimpleLogicManager;
import com.simsilica.es.EntityData;

import java.util.LinkedList;
import java.util.List;

/**
 * An AppState which could be used as SimpleLogicManager.
 * The logic update happens in the render thread update.
 */
public final class SimpleLogicManagerState extends BaseAppState {

    private final SimpleLogicManager logicManager = new SimpleLogicManager();
    private final EntityData entityData;
    private final List<BaseSimpleEntityLogic> toAttach = new LinkedList<>();

    public SimpleLogicManagerState(EntityData entityData) {
        this.entityData = entityData;
    }

    @Override
    protected void initialize(Application app) {
        logicManager.initialize(entityData);
        toAttach.forEach(logicManager::attach);
        toAttach.clear();
    }

    @Override
    protected void cleanup(Application app) {
        logicManager.destroy();
    }

    public void attach(BaseSimpleEntityLogic logic){
        if(!isInitialized()){
            toAttach.add(logic);
            return;
        }
        logicManager.attach(logic);
    }

    public void detach(BaseSimpleEntityLogic logic){
        if(!isInitialized()){
            throw new IllegalStateException("Logic manager is ot initialized yet!");
        }
        logicManager.detach(logic);
    }

    @Override
    public void update(float tpf) {
        if(!isEnabled()) return;
        logicManager.update();
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }
}
