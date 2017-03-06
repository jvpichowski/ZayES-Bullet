package com.jvpichowski.jme3.es.bullet.character;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.collision.PhysicsRayTestResult;
import com.jme3.math.Vector3f;
import com.jvpichowski.jme3.es.bullet.BulletSystem;
import com.jvpichowski.jme3.es.bullet.PhysicsSystem;
import com.jvpichowski.jme3.es.bullet.components.PhysicsPosition;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntitySet;

import java.util.LinkedList;

/**
 * Checks by casting a ray. Maybe it's better to get the colliding objects and get the normal of the hitpoint.
 */
final class InAirSystem implements PhysicsSystem, PhysicsTickListener {

    private EntitySet characters;
    private EntityData entityData;

    @Override
    public void initialize(EntityData entityData, BulletSystem bulletSystem) {
        this.entityData = entityData;
        characters = entityData.getEntities(PhysicsCharacter.class, PhysicsPosition.class);
        bulletSystem.getPhysicsSpace().addTickListener(this);
    }

    @Override
    public void destroy(EntityData entityData, BulletSystem bulletSystem) {
        bulletSystem.getPhysicsSpace().removeTickListener(this);
        characters.release();
    }

    @Override
    public void prePhysicsTick(PhysicsSpace space, float tpf) {  }

    @Override
    public void physicsTick(PhysicsSpace space, float tpf) {
        characters.applyChanges();
        characters.forEach(entity -> {
            float height = entity.get(PhysicsCharacter.class).getHeight();
            PhysicsPosition pos = entity.get(PhysicsPosition.class);
            Vector3f start = pos.getLocation();
            LinkedList<PhysicsRayTestResult> result = new LinkedList<>();
            space.rayTest(start.add(0, -height/2, 0), start.add(0f, -height/2f-0.65f, 0f), result);
            if(result.isEmpty()){
                entity.set(new InAir());
            }else{
                entityData.removeComponent(entity.getId(), InAir.class);
            }
        });
    }
}
