package com.jvpichowski.jme3.es.bullet.extension.producer;

import com.jme3.math.Vector3f;
import com.jvpichowski.jme3.es.bullet.BulletSystem;
import com.jvpichowski.jme3.es.bullet.components.ForceComponent;
import com.jvpichowski.jme3.es.bullet.components.PhysicsPosition;
import com.jvpichowski.jme3.es.bullet.components.RigidBody;
import com.jvpichowski.jme3.es.logic.BaseSimpleEntityLogic;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntitySet;

/**
 *
 */
public final class ForceProducerLogic extends BaseSimpleEntityLogic {

    private BulletSystem bulletSystem;
    private EntitySet producers;

    @Override
    public void registerComponents() {
        dependsOn(RigidBody.class, PhysicsPosition.class);
    }

    public void initLogic(EntityData entityData, BulletSystem bulletSystem) {
        producers = entityData.getEntities(PhysicsPosition.class, ForceProducer.class);
        this.bulletSystem = bulletSystem;
        bulletSystem.getForceSystem().registerForce(Force.class);
    }

    public void destroyLogic() {
        producers.release();
        bulletSystem.getForceSystem().unregisterForce(Force.class);
    }

    @Override
    public void updateLogic() {
        producers.applyChanges();
    }

    @Override
    public void run() {
        float mass = get(RigidBody.class).getMass();
        if (mass == 0) {
            return;
        }
        Vector3f pos = get(PhysicsPosition.class).getLocation();
        Vector3f force = new Vector3f();
        //sum up all force producers to create the final force
        producers.forEach(entity -> {
            Vector3f prodPos = entity.get(PhysicsPosition.class).getLocation();
            force.addLocal(entity.get(ForceProducer.class).getForceDefinition().apply(pos.subtract(prodPos)));
        });
        force.multLocal(mass);
        //TODO remove if = (0,0,0)
        set(new Force(force));
    }

    @Override
    public void destroy() {
        clear(Force.class);
    }

    private final class Force implements ForceComponent {

        private final Vector3f force;

        public Force(Vector3f force) {
            this.force = force;
        }

        @Override
        public Vector3f getForce() {
            return force;
        }

        @Override
        public Vector3f getLocation() {
            return null;
        }
    }
}
