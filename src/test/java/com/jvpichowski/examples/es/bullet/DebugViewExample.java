package com.jvpichowski.examples.es.bullet;

import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jvpichowski.jme3.es.bullet.components.*;
import com.jvpichowski.jme3.states.ESBulletState;
import com.jvpichowski.jme3.states.DebugViewState;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.base.DefaultEntityData;

/**
 * Example No. 5
 */
public class DebugViewExample extends SimpleApplication {

    public static void main(String[] args) {
        EntityData entityData = new DefaultEntityData();
        DebugViewExample app = new DebugViewExample(entityData);
        app.start();
    }

    private final EntityData entityData;

    public DebugViewExample(EntityData entityData){
        super(new FlyCamAppState(), new ESBulletState(entityData));
        this.entityData = entityData;
    }

    @Override
    public void simpleInitApp() {
        //create a new debug view state
        //you have to add the same CustomShapeFactory which you have added to the BulletSystem
        //if no factory is given to the constructor the default factory will be used (CollisionShapeFactory)
        DebugViewState debugView = new DebugViewState(entityData, getRootNode());
        getStateManager().attach(debugView);

        EntityId sphere = entityData.createEntity();
        entityData.setComponents(sphere,
                new CustomShape(new SphereCollisionShape(0.5f)),
                new RigidBody(false, 10),
                new WarpPosition(new Vector3f(0, 15, 0), Quaternion.DIRECTION_Z.clone()));

        EntityId box = entityData.createEntity();
        entityData.setComponents(box,
                new BoxShape(new Vector3f(5f, 0.1f, 5f)),
                new RigidBody(false, 0));
    }

}
