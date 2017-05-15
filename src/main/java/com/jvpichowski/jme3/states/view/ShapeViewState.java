package com.jvpichowski.jme3.states.view;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.scene.Node;
import com.simsilica.es.EntityData;

/**
 *
 */
public final class ShapeViewState extends BaseAppState {

    private final EntityData entityData;
    private final Node rootNode;
    private Node sceneRoot;
    private BoxView.Container boxContainer;
    private CylinderView.Container cylinderContainer;
    private SphereView.Container sphereContainer;
    private CapsuleView.Container capsuleContainer;

    public ShapeViewState(EntityData entityData, Node rootNode){
        this.rootNode = rootNode;
        this.entityData = entityData;
    }

    @Override
    protected void initialize(Application app) {
        sceneRoot = new Node("Shapes");
        boxContainer = new BoxView.Container(entityData, app.getAssetManager(), sceneRoot);
        cylinderContainer = new CylinderView.Container(entityData, app.getAssetManager(), sceneRoot);
        sphereContainer = new SphereView.Container(entityData, app.getAssetManager(), sceneRoot);
        capsuleContainer = new CapsuleView.Container(entityData, app.getAssetManager(), sceneRoot);
        boxContainer.start();
        cylinderContainer.start();
        sphereContainer.start();
        capsuleContainer.start();
    }

    @Override
    protected void cleanup(Application app) {
        boxContainer.stop();
        cylinderContainer.stop();
        sphereContainer.stop();
        capsuleContainer.stop();
        boxContainer.destroy();
        cylinderContainer.destroy();
        sphereContainer.destroy();
        capsuleContainer.destroy();
        sceneRoot = null;
    }

    @Override
    protected void onEnable() {
        rootNode.attachChild(sceneRoot);
    }

    @Override
    protected void onDisable() {
        sceneRoot.removeFromParent();
    }

    @Override
    public void update(float tpf) {
        if(!isEnabled()) return;
        boxContainer.update();
        cylinderContainer.update();
        sphereContainer.update();
        capsuleContainer.update();
    }
}
