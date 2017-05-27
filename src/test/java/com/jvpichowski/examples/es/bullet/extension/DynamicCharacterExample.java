package com.jvpichowski.examples.es.bullet.extension;

import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.math.*;
import com.jme3.scene.Node;
import com.jvpichowski.jme3.es.bullet.components.*;
import com.jvpichowski.jme3.es.bullet.extension.character.PhysicsCharacter;
import com.jvpichowski.jme3.es.bullet.extension.character.PhysicsCharacterLogic;
import com.jvpichowski.jme3.es.bullet.extension.character.PhysicsCharacterMovement;
import com.jvpichowski.jme3.es.bullet.extension.character.PhysicsCharacterState;
import com.jvpichowski.jme3.es.bullet.extension.logic.PhysicsSimpleLogicManager;
import com.jvpichowski.jme3.es.bullet.extension.view.PhysicsToViewLocation;
import com.jvpichowski.jme3.es.bullet.extension.view.PhysicsToViewRotation;
import com.jvpichowski.jme3.states.ESBulletState;
import com.jvpichowski.jme3.states.SimpleLogicManagerState;
import com.jvpichowski.jme3.states.view.BoxView;
import com.jvpichowski.jme3.states.view.CapsuleView;
import com.jvpichowski.jme3.states.view.ShapeViewState;
import com.jvpichowski.jme3.states.view.UnshadedColor;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.base.DefaultEntityData;

/**
 *
 */
public class DynamicCharacterExample extends SimpleApplication {

    public static void main(String[] args) {
        EntityData entityData = new DefaultEntityData();
        DynamicCharacterExample app = new DynamicCharacterExample(entityData, new Node());
        app.start();
    }

    private final EntityData entityData;
    private final Node root;
    private EntityId character;

    public DynamicCharacterExample(EntityData entityData, Node root){
        super(new FlyCamAppState(),
                //this is needed for the view
                new ShapeViewState(entityData, root),
                new ESBulletState(entityData),
                //this is needed to convert the physics position to the view position
                new SimpleLogicManagerState(entityData));
        this.entityData = entityData;
        this.root = root;
    }

    @Override
    public void simpleInitApp() {
        getRootNode().attachChild(root);
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(1,-0.5f,-2).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        getRootNode().addLight(sun);
        //add scripts which convert the physics positions to view positions
        //the scripts are executed on the render thread
        SimpleLogicManagerState logicManager = getStateManager().getState(SimpleLogicManagerState.class);
        logicManager.attach(new PhysicsToViewRotation.Logic());
        logicManager.attach(new PhysicsToViewLocation.Logic());

        ESBulletState esBulletState = stateManager.getState(ESBulletState.class);
        esBulletState.onInitialize(() -> {
            //add a logic manager for scripts which should be executed on the physics thread
            PhysicsSimpleLogicManager physicsLogicManager = new PhysicsSimpleLogicManager();
            physicsLogicManager.initialize(entityData, esBulletState.getBulletSystem());
            //in a real application don't forget to destroy it after usage:
            //physicsLogicManager.destroy();

            // add the character logic
            PhysicsCharacterLogic characterLogic = new PhysicsCharacterLogic();
            characterLogic.initLogic(physicsLogicManager.getPreTickLogicManager(), esBulletState.getBulletSystem());
            physicsLogicManager.getPreTickLogicManager().attach(characterLogic);

            createLevel();
            createCharacter();
            initInput();
        });
    }

    private void initInput(){
        getInputManager().addMapping("LEFT", new KeyTrigger(KeyInput.KEY_J));
        getInputManager().addMapping("RIGHT", new KeyTrigger(KeyInput.KEY_L));
        getInputManager().addMapping("UP", new KeyTrigger(KeyInput.KEY_I));
        getInputManager().addMapping("DOWN", new KeyTrigger(KeyInput.KEY_K));
        getInputManager().addMapping("JUMP", new KeyTrigger(KeyInput.KEY_SPACE));
        getInputManager().addListener((ActionListener) (name, isPressed, tpf) -> {
            Vector2f movement = new Vector2f();
            if(isPressed){
                switch (name) {
                    case "LEFT": movement.setX(-1); break;
                    case "RIGHT": movement.setX(+1); break;
                    case "UP": movement.setY(-1); break;
                    case "DOWN": movement.setY(+1); break;
                }
            }
            entityData.setComponent(character,  new PhysicsCharacterMovement(movement));
        }, "LEFT", "RIGHT", "UP", "DOWN");
        getInputManager().addListener((ActionListener) (name, isPressed, tpf) ->
                entityData.setComponent(character, new PhysicsCharacterState(isPressed, false)
        ), "JUMP");
    }

    private void createCharacter(){
        float CHARACTER_RADIUS = 0.3f;
        float CHARACTER_HEIGHT = 1.8f;
        float CHARACTER_STEP_HEIGHT = 0.5f;
        character = entityData.createEntity();
        entityData.setComponents(character,
                //some view stuff
                new PhysicsToViewLocation(new Vector3f(0, -CHARACTER_STEP_HEIGHT/2, 0)),
                new PhysicsToViewRotation(),
                new UnshadedColor(ColorRGBA.Red),
                new CapsuleView(CHARACTER_RADIUS, CHARACTER_HEIGHT-2*CHARACTER_RADIUS),
                //basic properties of the capsule
                new PhysicsCharacter(CHARACTER_RADIUS, CHARACTER_HEIGHT, 80,
                        5,2, 0.2f, 0.5f, 2),
                new WarpPosition(new Vector3f(0, 5, 0), new Quaternion()));
    }

    private void createLevel(){
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
                new WarpPosition(new Vector3f(-10,0,0),
                        new Quaternion().fromAngleAxis(FastMath.DEG_TO_RAD * 10, Vector3f.UNIT_Z)),
                new RigidBody(false, 0),
                new BoxShape(new Vector3f(2f, 0.2f, 2f)));

        EntityId ramp2 = entityData.createEntity();
        entityData.setComponents(ramp2,
                new PhysicsToViewLocation(),
                new PhysicsToViewRotation(),
                new UnshadedColor(ColorRGBA.Blue),
                new BoxView(new Vector3f(2f, 0.2f, 2f)),
                new WarpPosition(new Vector3f(-6f,0,0),
                        new Quaternion().fromAngleAxis(FastMath.DEG_TO_RAD * -10, Vector3f.UNIT_Z)),
                new RigidBody(false, 0),
                new BoxShape(new Vector3f(2f, 0.2f, 2f)));
    }
}
