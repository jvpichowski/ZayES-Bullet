package com.jvpichowski.jme3.es.logic;

import com.simsilica.es.EntityData;
import com.simsilica.es.EntitySet;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages a set of EntityLogic's. It executes their lifecycle methods when update() is called.
 */
public final class SimpleLogicManager {

    private EntityData entityData;
    private Map<BaseSimpleEntityLogic, EntitySet> logics = new HashMap<>();

    public void initialize(EntityData entityData){
        this.entityData = entityData;
    }

    public void update() {
        logics.forEach((logic, entities) -> {
            entities.applyChanges();
            entities.getAddedEntities().forEach(logic::onAdd);
            entities.getChangedEntities().forEach(logic::onChanged);
            entities.getRemovedEntities().forEach(logic::onRemove);
            entities.forEach(logic::onRun); //removed entities are not in the set anymore
        });
    }

    public void destroy() {
        logics.forEach((logic, entities) -> entities.release());
        logics.clear();
        entityData = null;
    }

    public void attach(BaseSimpleEntityLogic logic) {
        if(logics.containsKey(logic)){
            //remove to create a new set, maybe dependsOn has changed..
            logics.get(logic).release();
            logics.remove(logic);
        }
        logic.initialize(entityData);
        logics.put(logic, entityData.getEntities(logic.getComponents()));
    }

    public void detach(BaseSimpleEntityLogic logic) {
        if(logics.containsKey(logic)){
            logics.get(logic).release();
            logics.remove(logic);
        }
    }
}
