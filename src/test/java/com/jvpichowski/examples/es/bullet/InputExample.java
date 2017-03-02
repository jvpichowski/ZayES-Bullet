package com.jvpichowski.examples.es.bullet;

import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.PlaneCollisionShape;
import com.jme3.bullet.debug.BulletDebugAppState;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Plane;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jvpichowski.jme3.es.bullet.components.*;
import com.jvpichowski.jme3.states.ESBulletState;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.base.DefaultEntityData;

/**
 * Example No. 2
 */
public class InputExample extends SimpleApplication {

    public static void main(String[] args) {
        EntityData entityData = new DefaultEntityData();
        InputExample app = new InputExample(entityData);
        app.start();
    }

    private final EntityData entityData;

    public InputExample(EntityData entityData){
        super(new FlyCamAppState(), new ESBulletState(entityData));
        this.entityData = entityData;
    }

    @Override
    public void simpleInitApp() {
        System.out.println("Press and hold SPACE to move box up!");
        //Add some entities
        EntityId plane = entityData.createEntity();
        entityData.setComponents(plane,
                new RigidBody(false, 0),
                new CustomShape(new PlaneCollisionShape(new Plane(Vector3f.UNIT_Y.clone(), 0))));

        EntityId box = entityData.createEntity();
        entityData.setComponents(box,
                new WarpPosition(new Vector3f(0,10,0), Quaternion.DIRECTION_Z.clone()),
                new RigidBody(false, 10),
                new BoxShape());

        EntityId box2 = entityData.createEntity();
        entityData.setComponents(box2,
                new RigidBody(false, 0),
                new BoxShape());

        getInputManager().addMapping("Push", new KeyTrigger(KeyInput.KEY_SPACE));
        getInputManager().addListener((AnalogListener) (name, time, tpf) -> {
            //100 Newton  are needed to lift the box up against the gravity (10kg * 9.81m/s^2 < 100N)
            entityData.setComponent(box, new Force(new Vector3f(0,100,0), new Vector3f()));
        }, "Push");

    }

    @Override
    public void update() {
        super.update();

        //attach DebugState after initialization of esBulletState
        if(getStateManager().getState(BulletDebugAppState.class) == null){
            if(getStateManager().getState(ESBulletState.class).isInitialized()) {
                ESBulletState esBulletState = getStateManager().getState(ESBulletState.class);

                //Add Debug State to debug physics
                BulletDebugAppState debugAppState = new BulletDebugAppState(esBulletState.getPhysicsSpace());
                getStateManager().attach(debugAppState);
                debugAppState.setEnabled(true);
            }
        }
    }
}