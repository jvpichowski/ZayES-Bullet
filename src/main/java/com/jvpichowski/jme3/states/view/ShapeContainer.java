package com.jvpichowski.jme3.states.view;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityComponent;
import com.simsilica.es.EntityContainer;
import com.simsilica.es.EntityData;

/**
 *
 */
abstract class ShapeContainer extends EntityContainer<Geometry> {

    private final Node root;
    private final AssetManager assetManager;

    public ShapeContainer(EntityData ed, AssetManager assetManager, Node root, Class<? extends EntityComponent> viewComponent) {
        super(ed, ViewLocation.class, ViewRotation.class, UnshadedColor.class, viewComponent);
        this.root = new Node(viewComponent.getSimpleName()+" root");
        root.attachChild(this.root);
        this.assetManager = assetManager;
    }

    public void destroy(){
        root.removeFromParent();
    }

    @Override
    protected Geometry addObject(Entity e) {
        Geometry geom = new Geometry("ID: "+e.getId(), getMesh(e));
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", e.get(UnshadedColor.class).getColor());
        geom.setMaterial(mat);
        Vector3f pos = e.get(ViewLocation.class).getLocation();
        Quaternion rot = e.get(ViewRotation.class).getRotation();
        geom.setLocalTranslation(pos);
        geom.setLocalRotation(rot);
        //geom.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        root.attachChild(geom);
        return geom;
    }

    @Override
    protected void updateObject(Geometry object, Entity e) {
        object.setMesh(getMesh(e));
        object.updateModelBound(); //TODO update geometry state?
        object.getMaterial().setColor("Color", e.get(UnshadedColor.class).getColor());
        Vector3f pos = e.get(ViewLocation.class).getLocation();
        Quaternion rot = e.get(ViewRotation.class).getRotation();
        object.setLocalTranslation(pos);
        object.setLocalRotation(rot);
    }

    @Override
    protected void removeObject(Geometry object, Entity e) {
        object.removeFromParent();
    }

    protected abstract Mesh getMesh(Entity e);
}
