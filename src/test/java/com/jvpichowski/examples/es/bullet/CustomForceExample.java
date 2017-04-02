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
 * THis example shows how you could define multiple forces which act on a single entity.
 */
public class CustomForceExample extends SimpleApplication {

    public static void main(String[] args) {
        EntityData entityData = new DefaultEntityData();
        CustomForceExample app = new CustomForceExample(entityData);
        app.start();
    }

    private final EntityData entityData;

    public CustomForceExample(EntityData entityData){
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
                new WarpPosition(new Vector3f(0,10,0), Quaternion.DIRECTION_Z.clone()),
                new RigidBody(false, 10),
                new BoxShape());

        //Add two custom forces. You could play with the values..
        entityData.setComponents(box, new PushForce(10), new PullForce(10));

        ESBulletState esBulletState = stateManager.getState(ESBulletState.class);
        esBulletState.onInitialize(() -> {
            BulletDebugAppState debugAppState = new BulletDebugAppState(esBulletState.getPhysicsSpace());
            getStateManager().attach(debugAppState);

            //register the component definitions to make the physics system aware of them
            esBulletState.getBulletSystem().getForceSystem().registerForce(PullForce.class);
            esBulletState.getBulletSystem().getForceSystem().registerForce(PushForce.class);
        });
    }

    public static final class PullForce implements ForceComponent {

        private final Vector3f force;

        public PullForce(float force) {
            this.force = new Vector3f(-force, 0, 0);
        }

        @Override
        public Vector3f getForce() {
            return force;
        }

        @Override
        public Vector3f getLocation() {
            //return null because it's a central force
            return null;
        }
    }

    public static final class PushForce implements ForceComponent {

        private final Vector3f force;

        public PushForce(float force) {
            this.force = new Vector3f(force, 0, 0);
        }

        @Override
        public Vector3f getForce() {
            return force;
        }

        @Override
        public Vector3f getLocation() {
            //return null because it's a central force
            return null;
        }
    }
}
