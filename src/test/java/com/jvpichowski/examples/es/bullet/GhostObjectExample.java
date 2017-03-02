package com.jvpichowski.examples.es.bullet;

import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.PlaneCollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.debug.BulletDebugAppState;
import com.jme3.math.Plane;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jvpichowski.jme3.es.bullet.components.*;
import com.jvpichowski.jme3.states.ESBulletState;
import com.simsilica.es.*;
import com.simsilica.es.base.DefaultEntityData;


/**
 * Example No. 4
 */
public class GhostObjectExample extends SimpleApplication {

    public static void main(String[] args) {
        EntityData entityData = new DefaultEntityData();
        GhostObjectExample app = new GhostObjectExample(entityData);
        app.start();
    }

    private final EntityData entityData;

    private WatchedEntity ghostEntity;
    private EntityId box;

    public GhostObjectExample(EntityData entityData){
        super(new FlyCamAppState(), new ESBulletState(entityData));
        this.entityData = entityData;
    }

    @Override
    public void simpleInitApp() {
        getCamera().setLocation(new Vector3f(-5.6461415f, -0.026447738f, 5.5127993f));
        getCamera().setAxes(new Vector3f(-0.6044954f, 4.827976E-6f, -0.7966085f),
                new Vector3f(-0.19503751f, 0.9695639f, 0.14800741f),
                new Vector3f(0.7723636f, 0.24483837f, -0.5860959f));

        //Add some entities
        EntityId plane = entityData.createEntity();
        entityData.setComponents(plane,
                new RigidBody(false, 0),
                new CustomShape(new PlaneCollisionShape(new Plane(Vector3f.UNIT_Y.clone(), 0))));

        box = entityData.createEntity();
        entityData.setComponents(box,
                new WarpPosition(new Vector3f(0,10,0), Quaternion.DIRECTION_Z.clone()),
                new RigidBody(false, 10),
                new BoxShape());

        EntityId box2 = entityData.createEntity();
        entityData.setComponents(box2,
                new RigidBody(false, 0),
                new BoxShape());

        EntityId ghost = entityData.createEntity();
        entityData.setComponents(ghost,
                new SphereShape(1f),
                new GhostObject());

        //apply some force at the beginning
        entityData.setComponent(box, new Force(new Vector3f(100,100,100), new Vector3f()));

        ghostEntity = entityData.watchEntity(ghost, Collision.class);
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


        ghostEntity.applyChanges();
        if(ghostEntity.get(Collision.class).getCollisions().contains(box)){
            System.out.println("Box collides with ghost!");
        }
    }
}
