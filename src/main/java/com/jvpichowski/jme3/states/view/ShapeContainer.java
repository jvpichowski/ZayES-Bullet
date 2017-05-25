package com.jvpichowski.jme3.states.view;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityComponent;
import com.simsilica.es.EntityContainer;
import com.simsilica.es.EntityData;

/**
 * A base class for shape containers
 */
abstract class ShapeContainer extends EntityContainer<Geometry> implements SpatialContainer {

    private final Node root;
    private final AssetManager assetManager;

    public ShapeContainer(EntityData ed, AssetManager assetManager, Node root, Class<? extends EntityComponent> viewComponent) {
        super(ed, viewComponent);
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
        //geom.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        Material dummy = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        dummy.setColor("Color", ColorRGBA.White);
        geom.setMaterial(dummy);
        root.attachChild(geom);
        return geom;
    }

    @Override
    protected void updateObject(Geometry object, Entity e) {
        object.setMesh(getMesh(e));
        object.updateModelBound();
    }

    @Override
    protected void removeObject(Geometry object, Entity e) {
        object.removeFromParent();
    }

    protected abstract Mesh getMesh(Entity e);

}
