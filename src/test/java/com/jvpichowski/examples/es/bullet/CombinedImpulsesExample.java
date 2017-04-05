package com.jvpichowski.examples.es.bullet;

import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.collision.shapes.PlaneCollisionShape;
import com.jme3.bullet.debug.BulletDebugAppState;
import com.jme3.math.Plane;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jvpichowski.jme3.es.bullet.components.*;
import com.jvpichowski.jme3.states.ESBulletState;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.base.DefaultEntityData;

/**
 * This example shows how to use combined impulses.
 */
public class CombinedImpulsesExample  extends SimpleApplication {

    public static void main(String[] args) {
        EntityData entityData = new DefaultEntityData();
        CombinedImpulsesExample app = new CombinedImpulsesExample(entityData);
        app.start();
    }

    private final EntityData entityData;

    public CombinedImpulsesExample(EntityData entityData){
        super(new FlyCamAppState(), new ESBulletState(entityData));
        this.entityData = entityData;
    }

    @Override
    public void simpleInitApp() {
        //Same setup as in BasicExample
        EntityId plane = entityData.createEntity();
        entityData.setComponents(plane,
                new RigidBody(false, 0),
                new CustomShape(new PlaneCollisionShape(new Plane(Vector3f.UNIT_Y.clone(), 0))));

        EntityId box = entityData.createEntity();
        entityData.setComponents(box,
                new WarpPosition(new Vector3f(0,0,0), Quaternion.DIRECTION_Z.clone()),
                new RigidBody(false, 10),
                new BoxShape());


        //Add two custom impulses. They are only applied once. You could play with the values..
        entityData.setComponent(box, new CombinedImpulses(new Vector3f(100, 0,0), new Vector3f()));
        //for the second one we have to update the CombinedImpulses component.
        entityData.setComponent(box, entityData.getComponent(box, CombinedImpulses.class).addImpulse(new Vector3f(-150, 0, 0), new Vector3f()));

        ESBulletState esBulletState = stateManager.getState(ESBulletState.class);
        esBulletState.onInitialize(() -> {
            BulletDebugAppState debugAppState = new BulletDebugAppState(esBulletState.getPhysicsSpace());
            getStateManager().attach(debugAppState);
        });
    }
}