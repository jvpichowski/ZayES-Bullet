package com.jvpichowski.examples.es.bullet.extension;

import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.debug.BulletDebugAppState;
import com.jme3.font.BitmapText;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jvpichowski.examples.es.bullet.TestBrickWall;
import com.jvpichowski.jme3.es.bullet.components.*;
import com.jvpichowski.jme3.es.bullet.extension.character.PhysicsCharacterLogic;
import com.jvpichowski.jme3.es.bullet.extension.fields.ForceField;
import com.jvpichowski.jme3.es.bullet.extension.fields.ForceFieldLogic;
import com.jvpichowski.jme3.es.bullet.extension.logic.PhysicsSimpleLogicManager;
import com.jvpichowski.jme3.es.bullet.extension.view.PhysicsToViewLocation;
import com.jvpichowski.jme3.es.bullet.extension.view.PhysicsToViewRotation;
import com.jvpichowski.jme3.states.ESBulletState;
import com.jvpichowski.jme3.states.SimpleLogicManagerState;
import com.jvpichowski.jme3.states.view.BoxView;
import com.jvpichowski.jme3.states.view.ShadedColor;
import com.jvpichowski.jme3.states.view.ShapeViewState;
import com.jvpichowski.jme3.states.view.SphereView;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.base.DefaultEntityData;

/**
 *
 */
public class ForceFieldExample extends SimpleApplication {

    public static void main(String[] args) {
        ForceFieldExample example = new ForceFieldExample(new DefaultEntityData(), new Node("root"));
        example.start();
    }

    private final EntityData entityData;
    private final Node root;

    public ForceFieldExample(EntityData entityData, Node root) {
        super(
                new FlyCamAppState(),
                new ShapeViewState(entityData, root),
                new SimpleLogicManagerState(entityData),
                new ESBulletState(entityData)
        );
        this.entityData = entityData;
        this.root = root;
    }

    @Override
    public void simpleInitApp() {
        inputManager.addMapping("shoot", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(actionListener, "shoot");
        initCrossHairs();
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
            //Add Debug State to debug physics
            //As you see there are getters for physics space and so on.
            BulletDebugAppState debugAppState = new BulletDebugAppState(esBulletState.getPhysicsSpace());
            getStateManager().attach(debugAppState);

            //add a logic manager for scripts which should be executed on the physics thread
            PhysicsSimpleLogicManager physicsLogicManager = new PhysicsSimpleLogicManager();
            physicsLogicManager.initialize(entityData, esBulletState.getBulletSystem());
            //in a real application don't forget to destroy it after usage:
            //physicsLogicManager.destroy();

            // add the character logic
            PhysicsCharacterLogic characterLogic = new PhysicsCharacterLogic();
            characterLogic.initLogic(physicsLogicManager.getPreTickLogicManager(), esBulletState.getBulletSystem());
            physicsLogicManager.getPreTickLogicManager().attach(characterLogic);
            ForceFieldLogic forceFieldLogic = new ForceFieldLogic();
            forceFieldLogic.initLogic(entityData, esBulletState.getBulletSystem(),
                    physicsLogicManager.getPreTickLogicManager(),
                    physicsLogicManager.getTickLogicManager());
            physicsLogicManager.getPreTickLogicManager().attach(forceFieldLogic);

            initWorld();
            initForceField();
        });
    }


    private ActionListener actionListener = (name, keyPressed, tpf) -> {
        if (name.equals("shoot") && !keyPressed) {
            addBullet();
        }
    };

    protected void initCrossHairs() {
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        BitmapText ch = new BitmapText(guiFont, false);
        ch.setSize(guiFont.getCharSet().getRenderedSize() * 2);
        ch.setText("+"); // crosshairs
        ch.setLocalTranslation( // center
                settings.getWidth() / 2 - guiFont.getCharSet().getRenderedSize() / 3 * 2,
                settings.getHeight() / 2 + ch.getLineHeight() / 2, 0);
        guiNode.attachChild(ch);
    }


    public void addBullet(){
        EntityId bullet = entityData.createEntity();
        entityData.setComponents(bullet,
                new ShadedColor(ColorRGBA.Green),
                new PhysicsToViewLocation(),
                new PhysicsToViewRotation(),
                new SphereView(0.4f),
                new WarpPosition(cam.getLocation(), Quaternion.DIRECTION_Z.clone()),
                new WarpVelocity(cam.getDirection().mult(25), new Vector3f()),
                new SphereShape(0.4f),
                new RigidBody(false, 1));
    }

    private void initForceField() {
        EntityId forceField = entityData.createEntity();
        entityData.setComponents(forceField,
                new BoxShape(new Vector3f(5f,5f,5f)),
                new WarpPosition(new Vector3f(10, 5, 0), new Quaternion()),
                new ForceField(Vector3f.UNIT_X.mult(-50))
        );
    }

    private void initWorld(){
        EntityId centerFloor = entityData.createEntity();
        entityData.setComponents(centerFloor,
                new BoxShape(new Vector3f(5f, 0.2f, 5f)),
                new BoxView(new Vector3f(5f, 0.2f, 5f)),
                new ShadedColor(ColorRGBA.Green),
                new PhysicsToViewLocation(),
                new PhysicsToViewRotation(),
                new RigidBody(false, 0)
        );
        EntityId forceFieldFloor = entityData.createEntity();
        entityData.setComponents(forceFieldFloor,
                new BoxShape(new Vector3f(5f, 0.2f, 5f)),
                new BoxView(new Vector3f(5f, 0.2f, 5f)),
                new ShadedColor(ColorRGBA.Red),
                new PhysicsToViewLocation(),
                new PhysicsToViewRotation(),
                new RigidBody(false, 0),
                new WarpPosition(new Vector3f(10, 0,0), new Quaternion())
        );
    }
}
