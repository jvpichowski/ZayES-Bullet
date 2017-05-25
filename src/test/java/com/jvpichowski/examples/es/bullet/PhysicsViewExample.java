package com.jvpichowski.examples.es.bullet;

import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.debug.BulletDebugAppState;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import com.jvpichowski.jme3.es.bullet.components.BoxShape;
import com.jvpichowski.jme3.es.bullet.components.RigidBody;
import com.jvpichowski.jme3.es.bullet.extension.view.PhysicsToViewLocation;
import com.jvpichowski.jme3.es.bullet.extension.view.PhysicsToViewRotation;
import com.jvpichowski.jme3.states.ESBulletState;
import com.jvpichowski.jme3.states.SimpleLogicManagerState;
import com.jvpichowski.jme3.states.view.*;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.base.DefaultEntityData;

/**
 * Shows how a simple shape could be connected to a physics object.
 */
public class PhysicsViewExample extends SimpleApplication {

    public static void main(String[] args) {
        EntityData entityData = new DefaultEntityData();
        PhysicsViewExample app = new PhysicsViewExample(entityData, new Node("Scene Root"));
        app.start();
    }

    private final EntityData entityData;
    private final Node sceneRoot;

    public PhysicsViewExample(EntityData entityData, Node sceneRoot){
        super(
                new FlyCamAppState(),
                new ESBulletState(entityData),
                new SimpleLogicManagerState(entityData),
                new ShapeViewState(entityData, sceneRoot)
        );
        this.sceneRoot = sceneRoot;
        this.entityData = entityData;
    }

    @Override
    public void simpleInitApp() {
        getRootNode().attachChild(sceneRoot);

        EntityId box = entityData.createEntity();
        entityData.setComponents(box,
                new PhysicsToViewLocation(),        //the translation offset between the physics object and the view object
                new PhysicsToViewRotation(),        //the rotational offset between the physics object and the view object
                new UnshadedColor(ColorRGBA.Cyan),  //the color of the view object
                new BoxView(),                      //the view object
                new BoxShape(),                     //the physics object
                new RigidBody(true,0));

        //We have to add logic to translate the physics position to the view position.
        //The transition is defined by the components.
        SimpleLogicManagerState logicManager = getStateManager().getState(SimpleLogicManagerState.class);
        logicManager.attach(new PhysicsToViewRotation.Logic());
        logicManager.attach(new PhysicsToViewLocation.Logic());

        //Just add a debug view to compare physics and view.
        ESBulletState esBulletState = stateManager.getState(ESBulletState.class);
        esBulletState.onInitialize(() -> {
            BulletDebugAppState debugAppState = new BulletDebugAppState(esBulletState.getPhysicsSpace());
            getStateManager().attach(debugAppState);
        });
    }

}
