package com.jvpichowski.jme3.es.bullet.character;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jvpichowski.jme3.es.bullet.BulletSystem;
import com.jvpichowski.jme3.es.bullet.PhysicsSystem;
import com.jvpichowski.jme3.es.bullet.components.*;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntitySet;

/**
 * Created by jan on 20.02.2017.
 */
public final class CharacterSystem implements PhysicsSystem, PhysicsTickListener {

    private EntityData entityData;
    private EntitySet characters;
    private EntitySet movements;

    @Override
    public void initialize(EntityData entityData, BulletSystem bulletSystem) {
        this.entityData = entityData;
        characters = entityData.getEntities(Character.class);
        movements = entityData.getEntities(Character.class, Movement.class);
        bulletSystem.getPhysicsSpace().addTickListener(this);
    }

    @Override
    public void destroy(EntityData entityData, BulletSystem bulletSystem) {
        bulletSystem.getPhysicsSpace().removeTickListener(this);
        characters.release();
    }

    @Override
    public void prePhysicsTick(PhysicsSpace space, float tpf) {
        characters.applyChanges();
        characters.forEach(e -> {
            Character character = e.get(Character.class);
            e.set(new Friction(0.65f));
            e.set(new Factor(new Vector3f(1,1,1), new Vector3f(0,0,0)));
            RigidBody rigidBody = entityData.getComponent(e.getId(), RigidBody.class);
            if(rigidBody == null || rigidBody.getMass() != character.getMass()) {
                e.set(new RigidBody(false, character.getMass()));
            }
            CustomShape collisionShape = entityData.getComponent(e.getId(), CustomShape.class);
            if(collisionShape == null){
                e.set(new CustomShape(new CapsuleCollisionShape(character.getRadius(), character.getHeight())));
            }else {
                CapsuleCollisionShape oldShape = (CapsuleCollisionShape) collisionShape.getDefinition();
                if (oldShape.getRadius() != character.getRadius() || oldShape.getHeight() != character.getHeight()) {
                    e.set(new CustomShape(new CapsuleCollisionShape(character.getRadius(), character.getHeight())));
                }
            }
        });
        movements.applyChanges();
        movements.forEach(e -> {
            //apply every tick new imupls becaus it will be used every tick
            Vector3f movement = e.get(Movement.class).getMovement();
            e.set(new Impulse(movement, new Vector3f()));
        });
    }

    @Override
    public void physicsTick(PhysicsSpace space, float tpf) {

    }
}
