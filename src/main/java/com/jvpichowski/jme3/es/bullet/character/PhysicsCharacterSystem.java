package com.jvpichowski.jme3.es.bullet.character;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.math.Vector3f;
import com.jvpichowski.jme3.es.bullet.BulletSystem;
import com.jvpichowski.jme3.es.bullet.PhysicsSystem;
import com.jvpichowski.jme3.es.bullet.components.*;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntitySet;

/**
 * Created by jan on 20.02.2017.
 */
public final class PhysicsCharacterSystem implements PhysicsSystem, PhysicsTickListener {

    private InAirSystem inAirSystem;

    private EntityData entityData;
    private EntitySet characters;
    private EntitySet movements;

    private EntitySet stickySet;

    @Override
    public void initialize(EntityData entityData, BulletSystem bulletSystem) {
        this.entityData = entityData;
        characters = entityData.getEntities(PhysicsCharacter.class);
        movements = entityData.getEntities(PhysicsCharacter.class, PhysicsCharacterMovement.class);
        stickySet = entityData.getEntities(PhysicsCharacter.class, LinearVelocity.class, AngularVelocity.class, InAir.class);
        bulletSystem.getPhysicsSpace().addTickListener(this);

        inAirSystem = new InAirSystem();
        bulletSystem.addPhysicsSystem(inAirSystem);
    }

    @Override
    public void destroy(EntityData entityData, BulletSystem bulletSystem) {
        bulletSystem.removePhysicsSystem(inAirSystem);
        bulletSystem.getPhysicsSpace().removeTickListener(this);
        characters.release();
        movements.release();
        stickySet.release();
    }

    @Override
    public void prePhysicsTick(PhysicsSpace space, float tpf) {
        characters.applyChanges();
        characters.forEach(e -> {
            PhysicsCharacter character = e.get(PhysicsCharacter.class);
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
            Vector3f movement = e.get(PhysicsCharacterMovement.class).getMovement();
            e.set(new Impulse(movement, new Vector3f()));
        });
        stickySet.applyChanges();
        stickySet.forEach(e -> {
            Vector3f velocity = e.get(LinearVelocity.class).getVelocity();
            if(velocity.y > 0){
                Vector3f newVelocity = velocity.clone();
                newVelocity.y = 0;
                e.set(new WarpVelocity(newVelocity, e.get(AngularVelocity.class).getVelocity()));
            }
        });
    }

    @Override
    public void physicsTick(PhysicsSpace space, float tpf) {

    }
}
