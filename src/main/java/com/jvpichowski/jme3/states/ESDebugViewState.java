package com.jvpichowski.jme3.states;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;
import com.jvpichowski.jme3.es.bullet.CollisionShapeFactory;
import com.jvpichowski.jme3.es.bullet.CustomShapeFactory;
import com.jvpichowski.jme3.es.bullet.components.*;
import com.simsilica.es.*;

import java.util.logging.Logger;

/**
 * A new debug state.
 * Supports at the moment only BoxCollisionShapes and SphereCollisionShapes
 */
public final class ESDebugViewState extends BaseAppState {

    private final EntityData entityData;
    //private final ViewPort viewPort;
    private Node rootNode;
    private Node parentNode;
    private BodyView<CustomShape> customView;
    private BodyView<BoxShape> boxView;
    private BodyView<SphereShape> sphereView;
    private CustomShapeFactory customShapeFactory;
    private EntitySet bodyPositions;
    private PositionMarkers positionMarkers;


    public ESDebugViewState(EntityData entityData, /*ViewPort viewPort*/Node node, CustomShapeFactory customShapeFactory) {
        this.entityData = entityData;
        //this.viewPort = viewPort;
        this.parentNode = node;
        this.customShapeFactory = customShapeFactory;
    }

    public ESDebugViewState(EntityData entityData, Node node) {
        this(entityData, node, new CollisionShapeFactory());
    }

    @Override
    protected void initialize(Application app) {
        rootNode = new Node("ZayES-Bullet Debug Root");
        customView = new BodyView<>(entityData, app.getAssetManager(), rootNode, CustomShape.class, shape -> MeshFactory.create(shape, customShapeFactory));
        boxView = new BodyView<>(entityData, app.getAssetManager(), rootNode, BoxShape.class, MeshFactory::create);
        sphereView = new BodyView<>(entityData, app.getAssetManager(), rootNode, SphereShape.class, MeshFactory::create);
        bodyPositions = entityData.getEntities(RigidBody.class, PhysicsPosition.class);
        positionMarkers = new PositionMarkers(entityData, app.getAssetManager(), rootNode);
        customView.start();
        boxView.start();
        sphereView.start();
        positionMarkers.start();
    }

    @Override
    protected void cleanup(Application app) {
        bodyPositions.release();
        customView.stop();
        boxView.stop();
        sphereView.stop();
        positionMarkers.stop();
        rootNode = null;
    }

    @Override
    protected void onEnable() {
        parentNode.attachChild(rootNode);
        //viewPort.attachScene(rootNode);
    }

    @Override
    protected void onDisable() {
        rootNode.removeFromParent();
        //viewPort.detachScene(rootNode);
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        if(!isEnabled()){
            return;
        }
        positionMarkers.update();
        customView.update();
        boxView.update();
        sphereView.update();
        bodyPositions.applyChanges();
        bodyPositions.forEach(entity -> {
            Spatial spatial = customView.getObject(entity.getId());
            if(spatial == null){
                spatial = boxView.getObject(entity.getId());
            }
            if(spatial != null){
                PhysicsPosition position = entity.get(PhysicsPosition.class);
                spatial.setLocalRotation(position.getRotation());
                spatial.setLocalTranslation(position.getLocation());
            }
        });
    }

    private static final class PositionMarkers extends EntityContainer<Spatial> {

        private final AssetManager assetManager;
        private final Node rootNode;

        public PositionMarkers(EntityData ed, AssetManager assetManager, Node rootNode) {
            super(ed, PhysicsPosition.class);
            this.rootNode = rootNode;
            this.assetManager = assetManager;
        }

        @Override
        protected Spatial addObject(Entity e) {
            Vector3f loc = e.get(PhysicsPosition.class).getLocation();
            Mesh mesh = MeshFactory.createSphere(0.25f);
            Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            material.setColor("Color", ColorRGBA.Orange);
            TextureKey key = new TextureKey("Interface/grid-shaded.png");
            key.setGenerateMips(true);
            Texture texture = assetManager.loadTexture(key);
            texture.setWrap(Texture.WrapMode.Repeat);
            material.setTexture("ColorMap", texture);
            Geometry geometry = new Geometry("PhysicsPosition: "+e.getId(), mesh);
            geometry.setMaterial(material);
            geometry.setLocalTranslation(loc);
            rootNode.attachChild(geometry);
            return geometry;
        }

        @Override
        protected void updateObject(Spatial object, Entity e) {
            Vector3f loc = e.get(PhysicsPosition.class).getLocation();
            object.setLocalTranslation(loc);
        }

        @Override
        protected void removeObject(Spatial object, Entity e) {
            object.removeFromParent();
        }
    }

    /**
     *
     * @param <T>
     */
    private static final class BodyView <T extends EntityComponent> extends EntityContainer<Spatial> {

        private static final Logger LOGGER = Logger.getLogger(BodyView.class.getName());

        public interface Factory<T extends  EntityComponent> {
            Mesh getMesh(T component);
        }

        private AssetManager assetManager;
        private Factory<T> factory;
        private Class<T> componentType;
        private Node rootNode;

        public BodyView(EntityData ed, AssetManager assetManager, Node rootNode, Class<T> componentType, Factory<T> factory) {
            super(ed, componentType, RigidBody.class);
            this.componentType = componentType;
            this.factory = factory;
            this.assetManager = assetManager;
            this.rootNode = rootNode;
        }

        @Override
        protected Spatial addObject(Entity e) {
            Mesh mesh = factory.getMesh(e.get(componentType));
            if(mesh == null){
                LOGGER.info("Factory doesn't provide mesh for "+componentType+" "+e.getId());
                return new Node("Empty mesh ("+componentType+") "+e.getId());
            }
            Geometry geom = new Geometry(componentType.getName()+" "+ e.getId(), mesh);
            RigidBody body = e.get(RigidBody.class);
            if(body.isKinematic()){
                geom.setMaterial(createMaterial(ColorRGBA.Green));
            }else {
                if(body.getMass() == 0) {
                    geom.setMaterial(createMaterial(ColorRGBA.Cyan.mult(ColorRGBA.DarkGray)));
                }else {
                    geom.setMaterial(createMaterial(ColorRGBA.Red));
                }
            }
            rootNode.attachChild(geom);
            return geom;
        }

        @Override
        protected void updateObject(Spatial geom, Entity e) {
            if(geom instanceof Node){
                LOGGER.info("Can't update mesh for "+e.getId()+" recreate it!");
                return;
            }
            Mesh mesh = factory.getMesh(e.get(componentType));
            ((Geometry)geom).setMesh(mesh);
            geom.updateModelBound();
            RigidBody body = e.get(RigidBody.class);
            if(body.isKinematic()){
                geom.setMaterial(createMaterial(ColorRGBA.Green));
            }else {
                if(body.getMass() == 0) {
                    geom.setMaterial(createMaterial(ColorRGBA.Cyan.mult(ColorRGBA.DarkGray)));
                }else {
                    geom.setMaterial(createMaterial(ColorRGBA.Red));
                }
            }
        }

        @Override
        protected void removeObject(Spatial object, Entity e) {
            object.removeFromParent();
        }

        private Material createMaterial(ColorRGBA color){
            Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            material.setColor("Color", color);
            TextureKey key = new TextureKey("Interface/grid-shaded.png");
            key.setGenerateMips(true);
            Texture texture = assetManager.loadTexture(key);
            texture.setWrap(Texture.WrapMode.Repeat);
            material.setTexture("ColorMap", texture);
            return material;
        }
    }

    /**
     *
     */
    private static final class MeshFactory {

        private MeshFactory(){}

        public static Mesh create(BoxShape shape){
            return createBox(shape.getHalfExtents());
        }

        public static Mesh create(SphereShape shape){
            return createSphere(shape.getRadius());
        }

        public static Mesh create(CustomShape shape, CustomShapeFactory customShapeFactory){
            Object object = customShapeFactory.getShape(shape.getDefinition());
            if(object instanceof BoxCollisionShape){
                return createBox(((BoxCollisionShape)shape.getDefinition()).getHalfExtents());
            }
            if(object instanceof SphereCollisionShape){
                return createSphere(((SphereCollisionShape)shape.getDefinition()).getRadius());
            }
            Logger.getLogger(BodyView.Factory.class.getName()).info("Can't create mesh for definition: "+object);
            return null;
        }

        public static Mesh createBox(Vector3f halfExtents){
            return new Box(halfExtents.x, halfExtents.y, halfExtents.z);
        }

        public static Mesh createSphere(float radius){
            Sphere mesh = new Sphere(24, 24, radius);
            mesh.setTextureMode(Sphere.TextureMode.Projected);
            return mesh;
        }

    }
}
