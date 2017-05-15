package com.jvpichowski.jme3.states.view;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Sphere;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityComponent;
import com.simsilica.es.EntityContainer;
import com.simsilica.es.EntityData;

/**
 *
 */
public final class CapsuleView implements EntityComponent {

    private final float radius;
    private final float height;

    public CapsuleView(float radius, float height) {
        this.radius = radius;
        this.height = height;
    }

    public CapsuleView() {
        this(1f, 1f);
    }

    public float getRadius() {
        return radius;
    }

    public float getHeight() {
        return height;
    }

    static final class Container extends EntityContainer<Node> {

        private final Node root;
        private final AssetManager assetManager;

        public Container(EntityData ed, AssetManager assetManager, Node root) {
            super(ed, ViewLocation.class, ViewRotation.class, UnshadedColor.class, CapsuleView.class);
            this.root = new Node(CapsuleView.class.getSimpleName()+" root");
            root.attachChild(this.root);
            this.assetManager = assetManager;
        }

        public void destroy(){
            root.removeFromParent();
        }

        @Override
        protected Node addObject(Entity e) {
            Node capsule = new Node("ID: "+e.getId());
            refreshMesh(capsule, e);
            root.attachChild(capsule);
            return capsule;
        }

        @Override
        protected void updateObject(Node object, Entity e) {
            object.detachAllChildren();
            refreshMesh(object, e);

        }

        private void refreshMesh(Node node, Entity e){
            float r = e.get(CapsuleView.class).getRadius();
            float h = e.get(CapsuleView.class).getHeight();
            Geometry cylinder = new Geometry("Cylinder", new Cylinder(16, 16, r, h));
            Geometry top = new Geometry("top sphere", new Sphere(16,16, r));
            Geometry bottom = new Geometry("bottom sphere", new Sphere(16, 16, r));
            Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mat.setColor("Color", e.get(UnshadedColor.class).getColor());
            cylinder.setMaterial(mat);
            cylinder.rotate(FastMath.HALF_PI, 0, 0);
            bottom.setMaterial(mat);
            top.setMaterial(mat);
            bottom.setLocalTranslation(0, -h/2, 0);
            top.setLocalTranslation(0, h/2,0);
            node.attachChild(cylinder);
            node.attachChild(bottom);
            node.attachChild(top);
            Vector3f pos = e.get(ViewLocation.class).getLocation();
            Quaternion rot = e.get(ViewRotation.class).getRotation();
            node.setLocalTranslation(pos);
            node.setLocalRotation(rot);
        }

        @Override
        protected void removeObject(Node object, Entity e) {
            object.removeFromParent();
        }

    }
}
