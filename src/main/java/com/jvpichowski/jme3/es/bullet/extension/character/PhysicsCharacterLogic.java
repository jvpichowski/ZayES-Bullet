package com.jvpichowski.jme3.es.bullet.extension.character;

import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.math.Vector3f;
import com.jvpichowski.jme3.es.bullet.BulletSystem;
import com.jvpichowski.jme3.es.bullet.components.CustomShape;
import com.jvpichowski.jme3.es.bullet.components.Factor;
import com.jvpichowski.jme3.es.bullet.components.Friction;
import com.jvpichowski.jme3.es.bullet.components.RigidBody;
import com.jvpichowski.jme3.es.bullet.extension.forces.SimpleDrag;
import com.jvpichowski.jme3.es.bullet.extension.forces.SimpleDragForceLogic;
import com.jvpichowski.jme3.es.logic.BaseSimpleEntityLogic;
import com.jvpichowski.jme3.es.logic.SimpleLogicManager;

/**
 * call initLogic before start up
 * depends on PhysicsCharacter
 */
public class PhysicsCharacterLogic extends BaseSimpleEntityLogic {

    private BulletSystem bulletSystem;
    private SimpleLogicManager logicManager;
    private PhysicsCharacterMovementLogic movementLogic;
    private SimpleDragForceLogic dragForceLogic;

    public void initLogic(SimpleLogicManager logicManager, BulletSystem bulletSystem){
        this.logicManager = logicManager;
        this.bulletSystem = bulletSystem;
        bulletSystem.getForceSystem().registerForce(PhysicsCharacterMovementForce.class);
        movementLogic = new PhysicsCharacterMovementLogic();
        movementLogic.initLogic(bulletSystem);
        dragForceLogic = new SimpleDragForceLogic();
        logicManager.attach(dragForceLogic);
        logicManager.attach(movementLogic);
    }

    public void destroyLogic(){
        bulletSystem.getForceSystem().unregisterForce(PhysicsCharacterMovementForce.class);
        logicManager.detach(movementLogic);
        logicManager.detach(dragForceLogic);
        movementLogic.destroyLogic();
        movementLogic = null;
        dragForceLogic = null;
        logicManager = null;
        bulletSystem = null;
    }

    @Override
    public void registerComponents() {
        dependsOn(PhysicsCharacter.class);
    }

    @Override
    public void init() {
        PhysicsCharacter character = get(PhysicsCharacter.class);
        float characterCapsuleHeight = character.getHeight()-2*character.getRadius()-character.getStepHeight();
        float dragFactor = character.getMass()*character.getAcceleration()/(character.getMaxSpeed()*character.getMaxSpeed());
        set(new Friction(0));
        set(new Factor(new Vector3f(1,1,1), new Vector3f(0,0,0)));
        set(new RigidBody(false, character.getMass()));
        set(new CustomShape(new CapsuleCollisionShape(character.getRadius(), characterCapsuleHeight)));
        set(new SimpleDrag(dragFactor));
        set(new PhysicsCharacterState(false, false));
        set(new PhysicsCharacterJumpCount(0));
        set(new PhysicsCharacterMovement());
    }

    @Override
    public void update() {
        PhysicsCharacter character = get(PhysicsCharacter.class);
        float characterCapsuleHeight = character.getHeight()-2*character.getRadius()-character.getStepHeight();
        float dragFactor = character.getMass()*character.getAcceleration()/(character.getMaxSpeed()*character.getMaxSpeed());
        set(new RigidBody(false, character.getMass()));
        set(new CustomShape(new CapsuleCollisionShape(character.getRadius(), characterCapsuleHeight)));
        set(new SimpleDrag(dragFactor));
    }

    @Override
    public void destroy() {
        clear(Friction.class);
        clear(Factor.class);
        clear(RigidBody.class);
        clear(CustomShape.class);
        clear(SimpleDrag.class);
        clear(PhysicsCharacterState.class);
        clear(PhysicsCharacterJumpCount.class);
        clear(PhysicsCharacterMovement.class);
    }
}
