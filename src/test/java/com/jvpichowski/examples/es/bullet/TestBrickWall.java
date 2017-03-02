package com.jvpichowski.examples.es.bullet;

import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.font.BitmapText;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.shadow.BasicShadowRenderer;
import com.jvpichowski.jme3.es.bullet.components.*;
import com.jvpichowski.jme3.states.ESBulletState;
import com.simsilica.es.*;
import com.simsilica.es.base.DefaultEntityData;

/**
 * Implementation of this example with an entity system.
 *
 * https://github.com/jMonkeyEngine/jmonkeyengine/blob/master/jme3-examples/src/main/java/jme3test/bullet/TestBrickWall.java
 */
public class TestBrickWall  extends SimpleApplication {

    public static void main(String[] args) {
        EntityData entityData = new DefaultEntityData();
        TestBrickWall app = new TestBrickWall(entityData);
        app.start();
    }

    private final EntityData entityData;

    public TestBrickWall(EntityData entityData){
        super(new FlyCamAppState(), new ESBulletState(entityData));
        this.entityData = entityData;
    }

    private float bLength = 0.48f;
    private float bWidth = 0.24f;
    private float bHeight = 0.12f;

    private Material brickMat;
    private Material bulletMat;

    private BoxView boxes;
    private BulletView bullets;

    private BasicShadowRenderer bsr;

    @Override
    public void simpleInitApp() {
        //init input
        initCrossHairs();
        this.cam.setLocation(new Vector3f(0, 6f, 6f));
        cam.lookAt(Vector3f.ZERO, new Vector3f(0, 1, 0));
        cam.setFrustumFar(15);
        inputManager.addMapping("shoot", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(actionListener, "shoot");

        //init shadow
        rootNode.setShadowMode(RenderQueue.ShadowMode.Off);
        bsr = new BasicShadowRenderer(assetManager, 256);
        bsr.setDirection(new Vector3f(-1, -1, -1).normalizeLocal());
        viewPort.addProcessor(bsr);

        //init data
        brickMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        brickMat.setColor("Color", ColorRGBA.Orange);
        bulletMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        bulletMat.setColor("Color", ColorRGBA.Blue);
        initFloor();
        initWall();

        //start view
        boxes = new BoxView(entityData);
        bullets = new BulletView(entityData);
        boxes.start();
        bullets.start();
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


    @Override
    public void update() {
        super.update();

        //attach DebugState after initialization of esBulletState
        /*if(getStateManager().getState(BulletDebugAppState.class) == null){
            if(getStateManager().getState(ESBulletState.class).isInitialized()) {
                ESBulletState esBulletState = getStateManager().getState(ESBulletState.class);

                //Add Debug State to debug physics
                BulletDebugAppState debugAppState = new BulletDebugAppState(esBulletState.getPhysicsSpace());
                getStateManager().attach(debugAppState);
                debugAppState.setEnabled(true);
            }
        }*/

        //update view
        boxes.update();
        bullets.update();
    }

    ///////////////////////////////////////////////////////////////////
    /// DATA                                                        ///
    ///////////////////////////////////////////////////////////////////

    public void initFloor() {
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Brown);
        EntityId floorEntity = entityData.createEntity();
        entityData.setComponents(floorEntity,
                new WarpPosition(new Vector3f(0, -0.1f, 0), Quaternion.DIRECTION_Z.clone()),
                new RigidBody(false, 0),
                new Friction(0.6f),
                new BoxShape(new Vector3f(10f, 0.1f, 5f)),
                new BoxComponent(new Vector3f(10f, 0.1f, 5f)),
                new MaterialComponent(mat));
    }

    public void initWall() {
        float startpt = bLength / 4;
        float height = 0;
        for (int j = 0; j < 15; j++) {
            for (int i = 0; i < 4; i++) {
                Vector3f vt = new Vector3f(i * bLength * 2 + startpt, bHeight + height, 0);
                addBrick(vt);
            }
            startpt = -startpt;
            height += 2 * bHeight;
        }
    }

    public void addBrick(Vector3f ori) {
        EntityId boxEntity = entityData.createEntity();
        entityData.setComponents(boxEntity,
                new WarpPosition(ori.clone(), Quaternion.DIRECTION_Z.clone()),
                new BoxShape(new Vector3f(bLength, bHeight, bWidth)),
                new RigidBody(false, 1.5f, 0),
                new Friction(0.6f),
                new MaterialComponent(brickMat),
                new BoxComponent(new Vector3f(bLength, bHeight, bWidth)));
    }

    public void addBullet(){
        EntityId bullet = entityData.createEntity();
        entityData.setComponents(bullet, new MaterialComponent(bulletMat),
                new SphereComponent(0.4f),
                new WarpPosition(cam.getLocation(), Quaternion.DIRECTION_Z.clone()),
                new WarpVelocity(cam.getDirection().mult(25), new Vector3f()),
                new SphereShape(0.4f),
                new RigidBody(false, 1));
    }

    /////////////////////////////////////////////////////////////////////////
    /// VIEW                                                              ///
    /////////////////////////////////////////////////////////////////////////

    private class BoxView extends EntityContainer<Spatial>   {

        public BoxView(EntityData entityData){
            super(entityData, BoxComponent.class, MaterialComponent.class, PhysicsPosition.class);
        }

        @Override
        protected Spatial addObject(Entity e) {
            PhysicsPosition pos = e.get(PhysicsPosition.class);
            Vector3f size = e.get(BoxComponent.class).getSize();
            Geometry reBoxg = new Geometry("brick", new Box(size.x, size.y, size.z));
            reBoxg.setMaterial(e.get(MaterialComponent.class).getMat());
            reBoxg.setLocalTranslation(pos.getLocation());
            reBoxg.setLocalRotation(pos.getRotation());
            reBoxg.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
            TestBrickWall.this.rootNode.attachChild(reBoxg);
            return reBoxg;
        }

        @Override
        protected void updateObject(Spatial object, Entity e) {
            //only position will be updated during test
            PhysicsPosition pos = e.get(PhysicsPosition.class);
            object.setLocalTranslation(pos.getLocation());
            object.setLocalRotation(pos.getRotation());
        }

        @Override
        protected void removeObject(Spatial object, Entity e) {
            object.removeFromParent();
        }
    }

    private class BulletView extends EntityContainer<Spatial>  {

        public BulletView(EntityData entityData) {
            super(entityData, SphereComponent.class, MaterialComponent.class, PhysicsPosition.class);
        }

        @Override
        protected Spatial addObject(Entity e) {
            PhysicsPosition pos = e.get(PhysicsPosition.class);
            Geometry bullet = new Geometry("bullet", new Sphere(16,16,e.get(SphereComponent.class).getRadius()));
            bullet.setMaterial(e.get(MaterialComponent.class).getMat());
            bullet.setLocalTranslation(pos.getLocation());
            bullet.setLocalRotation(pos.getRotation());
            bullet.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
            TestBrickWall.this.rootNode.attachChild(bullet);
            return bullet;
        }

        @Override
        protected void updateObject(Spatial object, Entity e) {
            //only position will be updated during test
            PhysicsPosition pos = e.get(PhysicsPosition.class);
            object.setLocalTranslation(pos.getLocation());
            object.setLocalRotation(pos.getRotation());
        }

        @Override
        protected void removeObject(Spatial object, Entity e) {
            object.removeFromParent();
        }
    }

    /////////////////////////////////////////////////////////////////////////////////
    /// DATA DEFINITION                                                           ///
    /////////////////////////////////////////////////////////////////////////////////

    private class SphereComponent implements EntityComponent {

        private float radius;

        public SphereComponent(float radius) {
            this.radius = radius;
        }

        public float getRadius() {
            return radius;
        }
    }

    private class BoxComponent implements EntityComponent {
        private Vector3f size;

        public BoxComponent(Vector3f size) {
            this.size = size;
        }

        public Vector3f getSize() {
            return size;
        }
    }

    private class MaterialComponent implements EntityComponent {

        private Material mat;

        public MaterialComponent(Material mat) {
            this.mat = mat;
        }

        public Material getMat() {
            return mat;
        }
    }
}
