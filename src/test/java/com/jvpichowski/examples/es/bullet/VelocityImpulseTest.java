package com.jvpichowski.examples.es.bullet;

import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.collision.shapes.PlaneCollisionShape;
import com.jme3.bullet.debug.BulletDebugAppState;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
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
 * Test if you can use velocity and impulses simultaneous
 */
public class VelocityImpulseTest extends SimpleApplication {

    public static void main(String[] args) {
        EntityData entityData = new DefaultEntityData();
        VelocityImpulseTest app = new VelocityImpulseTest(entityData);
        app.start();
    }

    private final EntityData entityData;

    public VelocityImpulseTest(EntityData entityData){
        super(new FlyCamAppState(), new ESBulletState(entityData));
        this.entityData = entityData;
    }

    @Override
    public void simpleInitApp() {
        EntityId plane = entityData.createEntity();
        entityData.setComponents(plane,
                new Friction(0),
                new RigidBody(false, 0),
                new CustomShape(new PlaneCollisionShape(new Plane(Vector3f.UNIT_Y.clone(), 0))));

        EntityId box = entityData.createEntity();
        entityData.setComponents(box,
                new Friction(0),
                new WarpPosition(new Vector3f(0,1,0), Quaternion.DIRECTION_Z.clone()),
                new RigidBody(false, 10),
                new BoxShape(),
                new WarpVelocity(new Vector3f(1,0,0), new Vector3f()));

        ESBulletState esBulletState = stateManager.getState(ESBulletState.class);
        esBulletState.onInitialize(() -> {
            BulletDebugAppState debugAppState = new BulletDebugAppState(esBulletState.getPhysicsSpace());
            getStateManager().attach(debugAppState);
        });

        getInputManager().addMapping("JUMP", new KeyTrigger(KeyInput.KEY_SPACE));
        getInputManager().addListener((ActionListener) (name, isPressed, tpf) -> {
            if(isPressed){
                entityData.setComponent(box, new Impulse(new Vector3f(0, 20,0), new Vector3f()));
            }
        }, "JUMP");
    }

}
