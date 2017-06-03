package com.jvpichowski.jme3.es.logic;

import com.simsilica.es.Entity;
import com.simsilica.es.EntityComponent;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;

/**
 * A logic base class which should be use together with the SimpleLogicManager.
 */
public abstract class BaseSimpleEntityLogic {

    /**
     * Register all needed components in one call with dependsOn();
     */
    public abstract void registerComponents();

    /**
     * This method is called if a new entity is created which fits to the needed components
     */
    public void init() {}

    /**
     * This method is called if a value of the needed components has changed
     */
    public void update() {}

    /**
     * This method is called if an entity with the needed components
     * is destroyed or if a needed component is removed from it.
     */
    public void destroy() {}

    /**
     * This method is called every time the logic should be executed.
     */
    public void run() {}

    private EntityData entityData;
    private Entity entity;
    private Class<? extends EntityComponent>[] components;

    final void initialize(EntityData entityData){
        this.entityData = entityData;
        registerComponents();
    }

    /**
     * Is called before the per entity logic is updated
     */
    public void updateLogic() {

    }

    /**
     * Only call once in registerComponents(). Only the last call will be used.
     * @param components the components this logic depends on
     */
    protected final void dependsOn(Class<? extends EntityComponent>... components){
        this.components = components;
    }

    protected final void set(EntityComponent component) {
        entity.set(component);
    }

    protected final void clear(Class<? extends EntityComponent> componentType){
        entityData.removeComponent(entity.getId(), componentType);
    }

    protected final EntityId getId(){
        return entity.getId();
    }

    protected final <T extends EntityComponent> T get(Class<T> componentType) {
        return entity.get(componentType);
    }

    final void onAdd(Entity entity) {
        this.entity = entity;
        init();
        this.entity = null;
    }

    final void onChanged(Entity entity) {
        this.entity = entity;
        update();
        this.entity = null;
    }

    final void onRemove(Entity entity) {
        this.entity = entity;
        destroy();
        this.entity = null;
    }

    final void onRun(Entity entity) {
        this.entity = entity;
        run();
        this.entity = null;
    }

    final Class<? extends EntityComponent>[] getComponents() {
        return components;
    }
}
