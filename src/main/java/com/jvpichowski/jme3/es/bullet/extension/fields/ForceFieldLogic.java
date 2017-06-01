package com.jvpichowski.jme3.es.bullet.extension.fields;

import com.jvpichowski.jme3.es.bullet.BulletSystem;
import com.jvpichowski.jme3.es.bullet.components.Collision;
import com.jvpichowski.jme3.es.bullet.components.GhostObject;
import com.jvpichowski.jme3.es.bullet.components.RigidBody;
import com.jvpichowski.jme3.es.logic.BaseSimpleEntityLogic;
import com.jvpichowski.jme3.es.logic.SimpleLogicManager;
import com.simsilica.es.EntityData;

/**
 * Depends on: ForceField, CollisionShape
 */
public final class ForceFieldLogic extends BaseSimpleEntityLogic {

    private SimpleLogicManager preLogicManager;
    private SimpleLogicManager postLogicManager;
    private EntityData entityData;
    private CollisionLogic collisionLogic;
    private ForceRemoverLogic forceRemoverLogic;
    private BulletSystem bulletSystem;

    @Override
    public void registerComponents() {
        dependsOn(ForceField.class);
    }

    public void initLogic(EntityData entityData, BulletSystem bulletSystem, SimpleLogicManager preLogicManager, SimpleLogicManager postlogicManager){
        this.entityData = entityData;
        this.preLogicManager = preLogicManager;
        this.postLogicManager = postlogicManager;
        this.bulletSystem = bulletSystem;
        collisionLogic = new CollisionLogic();
        forceRemoverLogic = new ForceRemoverLogic();
        preLogicManager.attach(collisionLogic);
        postlogicManager.attach(forceRemoverLogic);
        bulletSystem.getForceSystem().registerForce(ForceFieldForce.class);
    }

    public void destroyLogic(){
        preLogicManager.detach(collisionLogic);
        postLogicManager.detach(forceRemoverLogic);
        bulletSystem.getForceSystem().unregisterForce(ForceFieldForce.class);
    }

    @Override
    public void init() {
        set(new GhostObject());
    }


    @Override
    public void update() {
    }

    @Override
    public void destroy() {
        clear(GhostObject.class);
    }

    private final class ForceRemoverLogic extends BaseSimpleEntityLogic {

        @Override
        public void registerComponents() {
            dependsOn(ForceFieldForce.class);
        }

        @Override
        public void init() {
            clear(ForceFieldForce.class);
        }

        @Override
        public void update() {
            clear(ForceFieldForce.class);
        }
    }

    private final class CollisionLogic extends BaseSimpleEntityLogic {

        @Override
        public void registerComponents() {
            dependsOn(ForceField.class, Collision.class);
        }

        @Override
        public void init() {
            applyForce();
        }

        @Override
        public void update() {
            applyForce();
        }

        private void applyForce(){
            Collision collision = get(Collision.class);
            collision.getCollisions().forEach(opponent -> {
                RigidBody body = entityData.getEntity(opponent, RigidBody.class).get(RigidBody.class);
                if(body != null){
                    float mass = body.getMass();
                    if(mass != 0) {
                        entityData.setComponent(opponent, new ForceFieldForce(get(ForceField.class).getFactor(), mass));
                    }
                }
            });

        }
    }
}
