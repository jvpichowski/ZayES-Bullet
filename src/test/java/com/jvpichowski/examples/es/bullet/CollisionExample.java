package com.jvpichowski.examples.es.bullet;

import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.PlaneCollisionShape;
import com.jme3.bullet.debug.BulletDebugAppState;
import com.jme3.math.Plane;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jvpichowski.jme3.es.bullet.components.*;
import com.jvpichowski.jme3.states.ESBulletState;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import com.simsilica.es.Name;
import com.simsilica.es.base.DefaultEntityData;


/**
 * Example No. 3
 */
public class CollisionExample extends SimpleApplication {

    public static void main(String[] args) {
        EntityData entityData = new DefaultEntityData();
        CollisionExample app = new CollisionExample(entityData);
        app.start();
    }

    private final EntityData entityData;

    private EntitySet colliding;

    public CollisionExample(EntityData entityData){
        super(new FlyCamAppState(), new ESBulletState(entityData));
        this.entityData = entityData;
    }

    @Override
    public void simpleInitApp() {
        //Add some entities
        EntityId plane = entityData.createEntity();
        entityData.setComponents(plane,
                new PhysicsPosition(new Vector3f(), Quaternion.DIRECTION_Z.clone()),
                new RigidBody(false, 0),
                new CollisionShape(new PlaneCollisionShape(new Plane(Vector3f.UNIT_Y.clone(), 0))),
                new Name("floor"));

        EntityId box = entityData.createEntity();
        entityData.setComponents(box,
                new PhysicsPosition(new Vector3f(0,10,0), Quaternion.DIRECTION_Z.clone()),
                new RigidBody(false, 10),
                new CollisionShape(new BoxCollisionShape(new Vector3f(0.5f,0.5f,0.5f))),
                new Name("box1"));

        EntityId box2 = entityData.createEntity();
        entityData.setComponents(box2,
                new PhysicsPosition(new Vector3f(0,0,0), Quaternion.DIRECTION_Z.clone()),
                new RigidBody(false, 0),
                new CollisionShape(new BoxCollisionShape(new Vector3f(0.5f,0.5f,0.5f))),
                new Name("box2"));

        entityData.setComponent(box, new Force(new Vector3f(100,100,100), new Vector3f()));

        colliding = entityData.getEntities(Collision.class, Name.class);
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

        colliding.applyChanges();
        colliding.forEach(entity -> {
            System.out.print(entity.get(Name.class).getName()+" collides with ");
            entity.get(Collision.class).getCollisions().forEach(entityId -> {
                System.out.print(colliding.getEntity(entityId).get(Name.class).getName()+" ");
            });
            System.out.println();
        });

    }
}
