package com.jvpichowski.jme3.states;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.renderer.RenderManager;
import com.jvpichowski.jme3.es.bullet.BulletSystem;
import com.simsilica.es.EntityData;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

/**
 *
 * This is a wrapper for the bullet system to easily attach
 * it to the application life cycle. It supports multiple threading
 * modes.
 */
public final class ESBulletState extends BaseAppState {


    private final EntityData entityData;
    private final ThreadingType threadingType;
    private BulletSystem bulletSystem;
    //used for parallel physics:
    private ScheduledThreadPoolExecutor executor;
    //used for tasks which should be executed after initialization
    private boolean finishedInit = false;
    private List<Runnable> initTasks = new LinkedList<>();
    //needed for parallel update
    private float tpf; //contains the last fps
    private Future physicsFuture; //contains the last update
    //needed fo decoupled physics
    private volatile boolean running = false; //true if the system is enabled
    private volatile boolean decoupledRunning = false; //true if the decoupled physic is running

    /**
     * Creates a new ESBulletState with sequential threading.
     *
     * @param entityData
     */
    public ESBulletState(EntityData entityData) {
        this(entityData, ThreadingType.SEQUENTIAL);
    }

    public ESBulletState(EntityData entityData, ThreadingType threadingType) {
        this.entityData = entityData;
        this.threadingType = threadingType;
    }

    @Override
    protected void initialize(Application app) {
        //TODO move this init code to executor?
        bulletSystem = new BulletSystem(entityData);
        finishedInit = true;
        initTasks.forEach(Runnable::run);
        initTasks = null;
        if(threadingType == ThreadingType.PARALLEL || threadingType == ThreadingType.DECOUPLED){
            if (executor != null) {
                //if you reattach an AppState. Maybe it's better to throw an Exception
                executor.shutdown();
            }
            executor = new ScheduledThreadPoolExecutor(1);
        }
    }

    @Override
    protected void cleanup(Application app) {
        if(executor != null) {
            executor.shutdown();
        }
        bulletSystem.destroy();
    }


    @Override
    public void update(float tpf) {
        super.update(tpf);
        if(!isEnabled()){
            return;
        }
        this.tpf = tpf; //copy the tpf so that it is known while rendering for parallel update
        if(threadingType == ThreadingType.MIXED) {
            //update the physics meanwhile with the use update
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
        running = true;
        if(threadingType == ThreadingType.DECOUPLED) {
            if (!decoupledRunning) {
                decoupledRunning = true;
                lastDecoupledPhysicsUpdate = System.currentTimeMillis();
                executor.submit(decoupledPhysicsUpdate);
            }
        }
    }

    @Override
    protected void onDisable() {
        running = false;
    }

    @Override
    public void render(RenderManager rm) {
        super.render(rm);
        if (!isEnabled()) {
            return;
        }
        if (threadingType == ThreadingType.PARALLEL) {
            //update the physics parallel while rendering
            physicsFuture = executor.submit(parallelPhysicsUpdate);
        } else if (threadingType == ThreadingType.SEQUENTIAL) {
            //update the physics before rendering
            bulletSystem.update(tpf);
        }
    }

    @Override
    public void postRender() {
        super.postRender();
        if (physicsFuture != null) {
            try {
                //wait until the physics update is done
                //if rendering was faster than physics update
                physicsFuture.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            physicsFuture = null;
        }
    }

    private Callable<Boolean> parallelPhysicsUpdate = new Callable<Boolean>() {
        public Boolean call() throws Exception {
            bulletSystem.update(tpf);
            return true;
        }
    };

    //when the last decoupled physics update happened (it belongs to the decoupled thread)
    private long lastDecoupledPhysicsUpdate = 0;
    private Callable<Void> decoupledPhysicsUpdate = new Callable<Void>() {
        @Override
        public Void call() throws Exception {
            bulletSystem.update(bulletSystem.getPhysicsSpace().getAccuracy());
            long update = System.currentTimeMillis() - lastDecoupledPhysicsUpdate;
            lastDecoupledPhysicsUpdate = System.currentTimeMillis();
            if(running) {
                executor.schedule(decoupledPhysicsUpdate, Math.round(getPhysicsSpace().getAccuracy() * 1000000.0f) - (update * 1000), TimeUnit.MICROSECONDS);
            }else{
                decoupledRunning = false;
            }
            return null;
        }
    };

    public enum ThreadingType {

        /**
         * Default mode; user update, physics update and rendering happen
         * sequentially (single threaded)
         */
        SEQUENTIAL,

        /**
         * Parallel threaded mode; physics update and rendering are executed in
         * parallel, update order is kept. Multiple BulletAppStates will
         * execute in parallel in this mode. (multi threaded)
         */
        PARALLEL,

        /**
         * Mixed mode; physics update happens while user update (single threaded)
         */
        MIXED,

        /**
         * Decoupled mode; physics update happens fully independent from the
         * game loop at the given accuracy of the physics space (usually 60Hz).
         * (multi threaded)
         */
        DECOUPLED
    }
}
