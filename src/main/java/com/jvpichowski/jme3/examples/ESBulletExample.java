package com.jvpichowski.jme3.examples;

import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.PlaneCollisionShape;
import com.jme3.bullet.debug.BulletDebugAppState;
import com.jme3.math.Plane;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jvpichowski.jme3.es.bullet.CollisionShapeComponent;
import com.jvpichowski.jme3.es.bullet.PhysicsForceComponent;
import com.jvpichowski.jme3.es.bullet.PhysicsPositionComponent;
import com.jvpichowski.jme3.es.bullet.PhysicsRigidBodyComponent;
import com.jvpichowski.jme3.states.ESBulletState;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.base.DefaultEntityData;


/**
 * Created by Jan on 06.02.2017.
 */
public class ESBulletExample extends SimpleApplication {

    public static void main(String[] args) {
        EntityData entityData = new DefaultEntityData();
        ESBulletExample app = new ESBulletExample(entityData);
        app.start();
    }

    private final EntityData entityData;

    public ESBulletExample(EntityData entityData){
        super(new FlyCamAppState(), new ESBulletState(entityData));
        this.entityData = entityData;
    }

    @Override
    public void simpleInitApp() {
        //Add some entities
        EntityId plane = entityData.createEntity();
        entityData.setComponents(plane,
                new PhysicsPositionComponent(new Vector3f(), Quaternion.DIRECTION_Z.clone()),
                new PhysicsRigidBodyComponent(false, 0),
                new CollisionShapeComponent(new PlaneCollisionShape(new Plane(Vector3f.UNIT_Y.clone(), 0))));

        EntityId box = entityData.createEntity();
        entityData.setComponents(box,
                new PhysicsPositionComponent(new Vector3f(0,10,0), Quaternion.DIRECTION_Z.clone()),
                new PhysicsRigidBodyComponent(false, 10),
                new CollisionShapeComponent(new BoxCollisionShape(new Vector3f(0.5f,0.5f,0.5f))));

        EntityId box2 = entityData.createEntity();
        entityData.setComponents(box2,
                new PhysicsPositionComponent(new Vector3f(0,0,0), Quaternion.DIRECTION_Z.clone()),
                new PhysicsRigidBodyComponent(false, 0),
                new CollisionShapeComponent(new BoxCollisionShape(new Vector3f(0.5f,0.5f,0.5f))));

        //uncomment this line and notice the difference
        //entityData.setComponent(box, new PhysicsForceComponent(new Vector3f(100,100,100), new Vector3f()));
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
