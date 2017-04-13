package com.jvpichowski.examples.es.bullet.extension;

import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.debug.BulletDebugAppState;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jvpichowski.jme3.es.bullet.components.*;
import com.jvpichowski.jme3.es.bullet.extension.forces.DragMedium;
import com.jvpichowski.jme3.es.bullet.extension.forces.DragMediumLogic;
import com.jvpichowski.jme3.es.bullet.extension.forces.HookeSpring;
import com.jvpichowski.jme3.es.bullet.extension.forces.HookeSpringLogic;
import com.jvpichowski.jme3.es.bullet.extension.logic.PhysicsSimpleLogicManager;
import com.jvpichowski.jme3.states.ESBulletState;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.base.DefaultEntityData;

/**
 * A simple example showing how to use a hooke spring and a drag medium
 */
public class SpringDragExample extends SimpleApplication {

    public static void main(String[] args) {
        EntityData entityData = new DefaultEntityData();
        SpringDragExample app = new SpringDragExample(entityData);
        app.start();
    }

    private final EntityData entityData;

    public SpringDragExample(EntityData entityData){
        super(new FlyCamAppState(), new ESBulletState(entityData));
        this.entityData = entityData;
    }

    @Override
    public void simpleInitApp() {

        //create a box with a mass of 1kg
        EntityId box = entityData.createEntity();
        entityData.setComponents(box,
                new WarpPosition(new Vector3f(0,4,0), Quaternion.DIRECTION_Z.clone()),
                new RigidBody(false, 1),
                new BoxShape());

        //this ghost shows the equilibrium point
        EntityId springGhost = entityData.createEntity();
        entityData.setComponents(springGhost,
                new WarpPosition(new Vector3f(0, 3, 0), Quaternion.DIRECTION_Z.clone()),
                new GhostObject(),
                new SphereShape(0.2f));

        //add a (hooke) spring to the box with its relaxed location at (0,3,0).
        //the spring force will act at the center and the spring constant will be 5.
        //also we add a medium to make it more realistic.
        entityData.setComponents(box,
                new HookeSpring(null, new Vector3f(0, 3, 0), 5),
                new DragMedium(1, DragMedium.DENSITY_AIR, 1.28f));

        ESBulletState esBulletState = stateManager.getState(ESBulletState.class);
        esBulletState.onInitialize(() -> {
            BulletDebugAppState debugAppState = new BulletDebugAppState(esBulletState.getPhysicsSpace());
            getStateManager().attach(debugAppState);

            //the spring logic is encapsulated in the logic manager and not in a system.
            //therefor we have to create a new logic manager
            PhysicsSimpleLogicManager physicsLogicManager = new PhysicsSimpleLogicManager();
            physicsLogicManager.initialize(entityData, esBulletState.getBulletSystem());
            //in a real application don't forget to destroy it after usage:
            //physicsLogicManager.destroy();

            //add the spring logic
            HookeSpringLogic hookeSpringLogic = new HookeSpringLogic();
            //call custom init method for the logic
            hookeSpringLogic.initLogic(esBulletState.getBulletSystem());
            physicsLogicManager.getPreTickLogicManager().attach(hookeSpringLogic);

            //same her with drag medium logic
            DragMediumLogic dragMediumLogic = new DragMediumLogic();
            dragMediumLogic.initLogic(esBulletState.getBulletSystem());
            physicsLogicManager.getPreTickLogicManager().attach(dragMediumLogic);
        });
    }
}
