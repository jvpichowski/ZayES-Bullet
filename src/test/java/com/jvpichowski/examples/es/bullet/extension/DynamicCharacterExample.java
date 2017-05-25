package com.jvpichowski.examples.es.bullet.extension;

import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.collision.PhysicsRayTestResult;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.debug.BulletDebugAppState;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.math.*;
import com.jme3.scene.Node;
import com.jvpichowski.jme3.es.bullet.BulletSystem;
import com.jvpichowski.jme3.es.bullet.components.*;
import com.jvpichowski.jme3.es.bullet.extension.forces.SimpleDrag;
import com.jvpichowski.jme3.es.bullet.extension.forces.SimpleDragForceLogic;
import com.jvpichowski.jme3.es.bullet.extension.logic.PhysicsSimpleLogicManager;
import com.jvpichowski.jme3.es.bullet.extension.view.PhysicsToViewLocation;
import com.jvpichowski.jme3.es.bullet.extension.view.PhysicsToViewRotation;
import com.jvpichowski.jme3.es.logic.BaseSimpleEntityLogic;
import com.jvpichowski.jme3.states.ESDebugViewState;
import com.jvpichowski.jme3.states.ESBulletState;
import com.jvpichowski.jme3.states.SimpleLogicManagerState;
import com.jvpichowski.jme3.states.view.BoxView;
import com.jvpichowski.jme3.states.view.CapsuleView;
import com.jvpichowski.jme3.states.view.ShapeViewState;
import com.jvpichowski.jme3.states.view.UnshadedColor;
import com.simsilica.es.EntityComponent;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.base.DefaultEntityData;

import java.util.List;

/**
 * Let's bet I can write a character logic in 500 LOC.
 */
public class DynamicCharacterExample extends SimpleApplication {

    private static final int MAX_JUMP_NUMBER = 2; //Double JumpState, 3 = Triple JumpState, 1 = normal jump, 0 = no jumping
    private static final float MAX_MOVE_SPEED = 5f; //m/s
    private static final float MOVE_ACCELERATION = 2; //m/s^2
    private static final float ANTI_JUMP_HEIGHT = 0.0f;
    private static final float CHARACTER_STEP_HEIGHT = 0.2f;
    private static final float CHARACTER_RADIUS = 0.3f;
    private static final float CHARACTER_HEIGHT = 1.8f;
    private static final float CHARACTER_MASS = 80f;
    private static final float CHARACTER_JUMP_HEIGHT = 0.8f; //Epot = Ekin; m*g*h = 1/2mv^2 -> v = sqrt(2gh)
    private static final float CHARACTER_JUMP_SPEED = FastMath.sqrt(2*9.81f*CHARACTER_JUMP_HEIGHT);//m/s
    //vor jump keinen Positionset in luft

    private static final float CHARACTER_CAPSULE_HEIGHT = CHARACTER_HEIGHT-2*CHARACTER_RADIUS-CHARACTER_STEP_HEIGHT;
    private static final float CHARACTER_BODY_HEIGHT = CHARACTER_CAPSULE_HEIGHT+2*CHARACTER_RADIUS;

    //TODO Drag only for horizontal movement on ground
    private static final float DRAG_FACTOR = CHARACTER_MASS*MOVE_ACCELERATION/(MAX_MOVE_SPEED*MAX_MOVE_SPEED);  //ma = mfv^2 f = a/v^2

    public static void main(String[] args) {
        EntityData entityData = new DefaultEntityData();
        DynamicCharacterExample app = new DynamicCharacterExample(entityData, new Node());
        app.start();
    }

    private final EntityData entityData;
    private final Node root;

    public DynamicCharacterExample(EntityData entityData, Node node){
        super(new FlyCamAppState(),
                new ShapeViewState(entityData, node),
                new ESBulletState(entityData),
                new SimpleLogicManagerState(entityData));
        this.entityData = entityData;
        this.root = node;
    }

    //the character id
    private EntityId character;
    //the movement of the character
    private Vector2f movement = new Vector2f();

    @Override
    public void simpleInitApp() {
        getRootNode().attachChild(root);
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(1,-0.5f,-2).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        getRootNode().addLight(sun);

        SimpleLogicManagerState logicManager = getStateManager().getState(SimpleLogicManagerState.class);
        logicManager.attach(new PhysicsToViewRotation.Logic());
        logicManager.attach(new PhysicsToViewLocation.Logic());

        ESBulletState esBulletState = stateManager.getState(ESBulletState.class);
        esBulletState.onInitialize(() -> {
            BulletDebugAppState debugAppState = new BulletDebugAppState(esBulletState.getPhysicsSpace());
            //getStateManager().attach(debugAppState);
            ESDebugViewState debugView = new ESDebugViewState(entityData, getRootNode());
            //getStateManager().attach(debugView);

            esBulletState.getBulletSystem().getForceSystem().registerForce(MoveForce.class);

            //the spring logic is encapsulated in the logic manager and not in a system.
            //therefor we have to create a new logic manager
            PhysicsSimpleLogicManager physicsLogicManager = new PhysicsSimpleLogicManager();
            physicsLogicManager.initialize(entityData, esBulletState.getBulletSystem());
            //in a real application don't forget to destroy it after usage:
            //physicsLogicManager.destroy();

            //add the spring logic
            //DynamicCharacterLogic characterLogic = new DynamicCharacterLogic();
            //call custom init method for the logic
            //characterLogic.initLogic(entityData, esBulletState.getBulletSystem(), physicsLogicManager.getPreTickLogicManager(), getStateManager().getState(DebugArrowDrawer.class));
            //physicsLogicManager.getPreTickLogicManager().attach(characterLogic);
            physicsLogicManager.getPreTickLogicManager().attach(new CharacterLogic());
            SimpleDragForceLogic sdfl = new SimpleDragForceLogic();
            sdfl.initLogic(esBulletState.getBulletSystem());
            physicsLogicManager.getPreTickLogicManager().attach(sdfl);

            EntityId plane = entityData.createEntity();
            entityData.setComponents(plane,
                    new PhysicsToViewLocation(),
                    new PhysicsToViewRotation(),
                    new UnshadedColor(ColorRGBA.DarkGray),
                    new BoxView(new Vector3f(50f, 0.1f, 50f)),
                    new RigidBody(false, 0),
                    new BoxShape(new Vector3f(50f, 0.1f, 50f)),
                    new WarpPosition(new Vector3f(0f, -0.1f, 0f), new Quaternion()));

            EntityId box = entityData.createEntity();
            entityData.setComponents(box,
                    new PhysicsToViewLocation(),
                    new PhysicsToViewRotation(),
                    new UnshadedColor(ColorRGBA.Green),
                    new BoxView(new Vector3f(2f, 0.2f, 2f)),
                    new RigidBody(false, 0),
                    new WarpPosition(new Vector3f(0f, -0.1f, 0f), new Quaternion()),
                    new BoxShape(new Vector3f(2, 0.2f, 2)));

            EntityId wall = entityData.createEntity();
            entityData.setComponents(wall,
                    new PhysicsToViewLocation(),
                    new PhysicsToViewRotation(),
                    new UnshadedColor(ColorRGBA.Green),
                    new BoxView(new Vector3f(0.2f, 10f, 10f)),
                    new RigidBody(false, 0),
                    new WarpPosition(new Vector3f(5,0,0), new Quaternion()),
                    new BoxShape(new Vector3f(0.2f, 10f, 10f)));

            EntityId ramp = entityData.createEntity();
            entityData.setComponents(ramp,
                    new PhysicsToViewLocation(),
                    new PhysicsToViewRotation(),
                    new UnshadedColor(ColorRGBA.Blue),
                    new BoxView(new Vector3f(2f, 0.2f, 2f)),
                    new WarpPosition(new Vector3f(-10,0,0), new Quaternion().fromAngleAxis(FastMath.DEG_TO_RAD * 10, Vector3f.UNIT_Z)),
                    new RigidBody(false, 0),
                    new BoxShape(new Vector3f(2f, 0.2f, 2f)));

            EntityId ramp2 = entityData.createEntity();
            entityData.setComponents(ramp2,
                    new PhysicsToViewLocation(),
                    new PhysicsToViewRotation(),
                    new UnshadedColor(ColorRGBA.Blue),
                    new BoxView(new Vector3f(2f, 0.2f, 2f)),
                    new WarpPosition(new Vector3f(-6f,0,0), new Quaternion().fromAngleAxis(FastMath.DEG_TO_RAD * -10, Vector3f.UNIT_Z)),
                    new RigidBody(false, 0),
                    new BoxShape(new Vector3f(2f, 0.2f, 2f)));

            character = entityData.createEntity();
            entityData.setComponents(character,
                    new PhysicsToViewLocation(new Vector3f(0, -CHARACTER_STEP_HEIGHT/2, 0)),
                    new PhysicsToViewRotation(),
                    new UnshadedColor(ColorRGBA.Red),
                    new CapsuleView(CHARACTER_RADIUS, CHARACTER_HEIGHT-2*CHARACTER_RADIUS),
                    new JumpState(false),
                    new JumpCount(0),
                    new Friction(0),
                    new Factor(new Vector3f(1,1,1), new Vector3f(0,0,0)),
                    new SimpleDrag(DRAG_FACTOR),
                    new PhysicsCharacter(),
                    new RigidBody(false, CHARACTER_MASS),
                    new CustomShape(new CapsuleCollisionShape(CHARACTER_RADIUS, CHARACTER_CAPSULE_HEIGHT)),
                    new WarpPosition(new Vector3f(0, 5, 0), new Quaternion()),
                    new MoveForce(new Vector2f()));

            getInputManager().addMapping("LEFT", new KeyTrigger(KeyInput.KEY_J));
            getInputManager().addMapping("RIGHT", new KeyTrigger(KeyInput.KEY_L));
            getInputManager().addMapping("UP", new KeyTrigger(KeyInput.KEY_I));
            getInputManager().addMapping("DOWN", new KeyTrigger(KeyInput.KEY_K));
            getInputManager().addMapping("JUMP", new KeyTrigger(KeyInput.KEY_SPACE));
            getInputManager().addListener((ActionListener) (name, isPressed, tpf) -> onKeyInput(name, isPressed),
                    "LEFT", "RIGHT", "UP", "DOWN");
            getInputManager().addListener((ActionListener) (name, isPressed, tpf) -> {
                if(isPressed) {
                    //TODO move if to logic
                    //if (entityData.getComponent(character, OnGround.class) != null || entityData.getComponent(character, JumpCount.class).getJumpNumber() < MAX_JUMP_NUMBER) {
                        entityData.setComponent(character, new JumpState(true));
                        //entityData.setComponent(character, new Impulse(new Vector3f(0, CHARACTER_MASS * CHARACTER_JUMP_SPEED, 0), new Vector3f()));
                        //entityData.setComponent(character, new JumpCount(entityData.getComponent(character, JumpCount.class).jumpNumber + 1));
                        //make drag lower and add jump drag
                    //}
                }
            }, "JUMP");

        });
    }

    private void onKeyInput(String name, boolean isPressed){
        movement = new Vector2f();
        if(isPressed){
            Vector3f impulse = new Vector3f();
            switch (name) {
                case "LEFT": impulse = Vector3f.UNIT_X.negate(); break;
                case "RIGHT": impulse = Vector3f.UNIT_X.clone(); break;
                case "UP": impulse = Vector3f.UNIT_Z.clone(); break;
                case "DOWN": impulse = Vector3f.UNIT_Z.negate(); break;
            }
            impulse.multLocal(CHARACTER_MASS*MOVE_ACCELERATION);
            movement = new Vector2f(impulse.x, impulse.z);
        }
        entityData.setComponent(character,  new MoveForce(movement));
    }

    private final class JumpState implements EntityComponent {

        private final boolean shouldJump;

        public JumpState(boolean shouldJump) {
            this.shouldJump = shouldJump;
        }

        public JumpState() {
            this(false);
        }

        public boolean shouldJump() {
            return shouldJump;
        }
    }

    //a simple flag component to mark the character
    private final class PhysicsCharacter implements EntityComponent {}

    private final class OnGround implements EntityComponent {}

    private final class JumpCount implements EntityComponent {

        private final int jumpNumber;

        public JumpCount(int jumpNumber) {
            this.jumpNumber = jumpNumber;
        }

        public int getJumpNumber() {
            return jumpNumber;
        }
    }

    private final class MoveForce implements ForceComponent {

        private final Vector3f force;

        public MoveForce(Vector2f move) {
            this.force = new Vector3f(move.x, 0, move.y);
        }

        /**
         * @return the linear force which should be applied to the physics object
         */
        @Override
        public Vector3f getForce() {
            return force;
        }

        /**
         * @return null because it's a central force
         */
        @Override
        public Vector3f getLocation() {
            return null;
        }
    }

    //the main character logic
    private class CharacterLogic extends BaseSimpleEntityLogic {

        /**
         * Register all needed components in one call with dependsOn();
         */
        @Override
        public void registerComponents() {
            dependsOn(
                    PhysicsCharacter.class, PhysicsPosition.class,
                    LinearVelocity.class, AngularVelocity.class,
                    JumpCount.class, JumpState.class, MoveForce.class
            );
        }

        /**
         * This method is called if a value of the needed components has changed
         */
        @Override
        public void update() {
            boolean shouldJump = get(JumpState.class).shouldJump();
            set(new JumpState(false));
            int jumpCount = get(JumpCount.class).getJumpNumber();
            boolean onGround = false;

            BulletSystem bulletSystem = getStateManager().getState(ESBulletState.class).getBulletSystem();
            PhysicsPosition pos = get(PhysicsPosition.class);
            List<PhysicsRayTestResult> rayTestResultList = bulletSystem.getPhysicsSpace()
                    .rayTest(pos.getLocation(), pos.getLocation().add(0,-50,0));

            Vector3f velocity = get(LinearVelocity.class).getVelocity();
            Vector3f warpVelo = velocity.clone();
            boolean doWarpVelo = false;
            if(velocity.x < 0.01 && velocity.x > -0.01){
                warpVelo.x = 0;
                doWarpVelo = true;
            }
            if(velocity.z < 0.01 && velocity.z > -0.01){
                warpVelo.z = 0;
                doWarpVelo = true;
            }
            if(rayTestResultList.size() > 0){
                float len = 60;
                for (PhysicsRayTestResult physicsRayTestResult : rayTestResultList) {
                    if(physicsRayTestResult.getHitFraction()*50 < len){
                        len = physicsRayTestResult.getHitFraction()*50;
                    }
                }
                //if the character is near the ground or below it push it to the ground.
                if(len < CHARACTER_STEP_HEIGHT + CHARACTER_BODY_HEIGHT/2 + ANTI_JUMP_HEIGHT){
                    float diff = CHARACTER_STEP_HEIGHT + CHARACTER_BODY_HEIGHT/2-len;
                    set(new WarpPosition(pos.getLocation().add(0, diff,  0), pos.getRotation()));
                    if(velocity.y < 0) {
                        warpVelo.y = 0;
                        doWarpVelo = true;
                    }
                    set(new OnGround());
                    onGround = true;
                    set(new JumpCount(0));
                }else{
                    //set(new MoveForce(Vector2f.ZERO));
                    clear(OnGround.class);
                }
            }else{
                //set(new MoveForce(Vector2f.ZERO));
                clear(OnGround.class);
            }
            if(doWarpVelo){
                set(new WarpVelocity(warpVelo, Vector3f.ZERO)); //set to ZERO because we don't have angular velocity
            }
            Vector3f impulse = new Vector3f();
            if(shouldJump && (onGround || jumpCount < MAX_JUMP_NUMBER)){
                set(new JumpCount(jumpCount+1));
                impulse.addLocal(new Vector3f(0, CHARACTER_MASS*CHARACTER_JUMP_SPEED, 0));
            }
            if(onGround){
                Vector3f slowDownImpulse = new Vector3f();
                Vector3f move = get(MoveForce.class).getForce();
                if(FastMath.approximateEquals(move.x, 0)){
                    slowDownImpulse.setX(-velocity.x*CHARACTER_MASS*0.05f);
                }
                if(FastMath.approximateEquals(move.z, 0)){
                    slowDownImpulse.setZ(-velocity.z*CHARACTER_MASS*0.05f);
                }
                impulse.addLocal(slowDownImpulse);
            }

            if(!impulse.equals(Vector3f.ZERO)) {
                //TODO use combined impulse
                set(new Impulse(impulse, new Vector3f()));
            }
        }
    }
}