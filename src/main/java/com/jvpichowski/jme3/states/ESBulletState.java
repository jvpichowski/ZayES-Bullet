package com.jvpichowski.jme3.states;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jvpichowski.jme3.es.bullet.BulletSystem;
import com.simsilica.es.EntityData;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * This is a very simple wrapper for the bullet system to easily attach
 * it to the application life cycle. It will call update every frame.
 * As a result the physics ticks won't be at a fixed rate.
 * Also it is not possible to use multithreaded physics yet.
 */
public final class ESBulletState extends BaseAppState {

    private EntityData entityData;
    private BulletSystem bulletSystem;

    private boolean finishedInit = false;
    private List<Runnable> initTasks = new LinkedList<>();

    public ESBulletState(EntityData entityData) {
        this.entityData = entityData;
    }

    @Override
    protected void initialize(Application app) {
        bulletSystem = new BulletSystem(entityData);
        finishedInit = true;
        initTasks.forEach(Runnable::run);
        initTasks = null;
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

    public BulletSystem getBulletSystem() {
        return bulletSystem;
    }

    /**
     *
     * @param task
     * @return true if state was already initialized.
     */
    public boolean onInitialize(Runnable task){
        if(finishedInit){
            return false;
        }
        initTasks.add(task);
        return false;
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }

    public enum ThreadingType {

        /**
         * Default mode; user update, physics update and rendering happen
         * sequentially (single threaded)
         */
        SEQUENTIAL,

        /**
         * Parallel threaded mode; physics update and rendering are executed in
         * parallel, update order is kept.<br/> Multiple BulletAppStates will
         * execute in parallel in this mode.
         */
        PARALLEL_MULTITHREADED,

        /**
         * Mixed mode; physics update happens while user update (single threaded)
         */
        MIXED
    }
}
