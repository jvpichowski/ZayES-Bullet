package com.jvpichowski.jme3.es.bullet.extension.character;

import com.jme3.bullet.collision.PhysicsRayTestResult;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jvpichowski.jme3.es.bullet.BulletSystem;
import com.jvpichowski.jme3.es.bullet.components.*;
import com.jvpichowski.jme3.es.logic.BaseSimpleEntityLogic;

import java.util.List;

/**
 *
 */
final class PhysicsCharacterMovementLogic extends BaseSimpleEntityLogic {

    private BulletSystem bulletSystem;

    public void initLogic(BulletSystem bulletSystem){
        this.bulletSystem = bulletSystem;
    }

    public void destroyLogic(){
        bulletSystem = null;
    }

    @Override
    public void registerComponents() {
        //register the needed components for a character
        dependsOn(
                PhysicsCharacter.class, PhysicsPosition.class,
                LinearVelocity.class, AngularVelocity.class,
                PhysicsCharacterJumpCount.class, PhysicsCharacterState.class,
                PhysicsCharacterMovement.class
        );
    }

    @Override
    public void update() {
        //update the character state based on the needed components
        //first collect some necessary information
        PhysicsCharacter character = get(PhysicsCharacter.class);
        Vector2f move = get(PhysicsCharacterMovement.class).getDirection();
        set(new PhysicsCharacterMovementForce(new Vector3f(
                move.x*character.getMass()*character.getAcceleration(), 0,
                move.y*character.getMass()*character.getAcceleration())));
        float capsuleHeight = character.getHeight()-2*character.getRadius()-character.getStepHeight();
        float bodyHeight = capsuleHeight+2*character.getRadius();
        float jumpSpeed = FastMath.sqrt(2*9.81f*character.getJumpHeight());
        boolean jumping = get(PhysicsCharacterState.class).isJumping();
        boolean ducking = get(PhysicsCharacterState.class).isDucking();
        set(new PhysicsCharacterState(false, false)); //TODO remove
        int jumpCount = get(PhysicsCharacterJumpCount.class).getCount();
        boolean onGround = false;
        Vector3f velocity = get(LinearVelocity.class).getVelocity();
        PhysicsPosition pos = get(PhysicsPosition.class);

        //the most important part is the ray system which keeps the capsule floating
        //in the air over the ground
        List<PhysicsRayTestResult> rayTestResultList = bulletSystem.getPhysicsSpace()
                .rayTest(pos.getLocation(), pos.getLocation().add(0,-50,0));
        if(rayTestResultList.size() > 0){
            float len = 60;
            for (PhysicsRayTestResult physicsRayTestResult : rayTestResultList) {
                if(physicsRayTestResult.getHitFraction()*50 < len){
                    len = physicsRayTestResult.getHitFraction()*50;
                }
            }
            //if the character is near the ground or below it push it to the min step height.
            if(len <= character.getStepHeight() + bodyHeight/2){
                float diff = character.getStepHeight() + bodyHeight/2-len;
                set(new WarpPosition(pos.getLocation().add(0, diff,  0), pos.getRotation()));
                if(velocity.y < 0) {
                    set(new WarpVelocity(velocity.clone().setY(0), Vector3f.ZERO));
                }
                onGround = true;
                set(new PhysicsCharacterJumpCount(0));
                jumpCount = 0;
            }
        }

        Vector3f impulse = new Vector3f();
        //apply jump
        if(jumping && (onGround || jumpCount < character.getMaxJumpNumber())){
            set(new PhysicsCharacterJumpCount(jumpCount+1));
            impulse.addLocal(new Vector3f(0, character.getMass()*jumpSpeed, 0));
        }
        //slow the character down if no movement is applied
        if(onGround){
            Vector3f slowDownImpulse = new Vector3f();
            if(FastMath.approximateEquals(move.x, 0)){
                slowDownImpulse.setX(-velocity.x*character.getMass()*0.05f);
            }
            if(FastMath.approximateEquals(move.y, 0)){
                slowDownImpulse.setZ(-velocity.z*character.getMass()*0.05f);
            }
            impulse.addLocal(slowDownImpulse);
        }
        //TODO use combined impulse
        set(new Impulse(impulse, new Vector3f()));
    }
}
