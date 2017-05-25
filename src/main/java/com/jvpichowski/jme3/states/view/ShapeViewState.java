package com.jvpichowski.jme3.states.view;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntitySet;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 *
 */
public final class ShapeViewState extends BaseAppState {

    private final EntityData entityData;
    private final Node rootNode;
    private Node sceneRoot;
    private List<SpatialContainer> containers;
    private EntitySet unshadedEntities;
    private EntitySet shadedEntities;
    private EntitySet locatedEntities;
    private EntitySet rotatedEntities;
    private AssetManager assetManager;

    public ShapeViewState(EntityData entityData, Node rootNode){
        this.rootNode = rootNode;
        this.entityData = entityData;
    }

    @Override
    protected void initialize(Application app) {
        sceneRoot = new Node("Shapes");
        assetManager = app.getAssetManager();
        containers = new ArrayList<>(4);
        containers.add(new BoxView.Container(entityData, app.getAssetManager(), sceneRoot));
        containers.add(new CylinderView.Container(entityData, app.getAssetManager(), sceneRoot));
        containers.add(new SphereView.Container(entityData, app.getAssetManager(), sceneRoot));
        containers.add(new CapsuleView.Container(entityData, app.getAssetManager(), sceneRoot));
        containers.forEach(SpatialContainer::start);
        shadedEntities = entityData.getEntities(ShadedColor.class);
        unshadedEntities = entityData.getEntities(UnshadedColor.class);
        locatedEntities = entityData.getEntities(ViewLocation.class);
        rotatedEntities = entityData.getEntities(ViewRotation.class);
    }

    @Override
    protected void cleanup(Application app) {
        shadedEntities.release();
        unshadedEntities.release();
        locatedEntities.release();
        rotatedEntities.release();
        containers.forEach(SpatialContainer::stop);
        containers.forEach(SpatialContainer::destroy);
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
        containers.forEach(SpatialContainer::update);
/*
        shadedEntities.applyChanges();
        shadedEntities.getAddedEntities().forEach(entity -> find(entity).forEach(spatial -> {
            Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
            mat.setBoolean("UseMaterialColors",true);
            mat.setColor("Diffuse", entity.get(ShadedColor.class).getDiffuse());
            System.out.println(entity.get(ShadedColor.class).getDiffuse());
            spatial.setMaterial(mat);
        }));
        shadedEntities.getChangedEntities().forEach(entity -> find(entity).forEach(spatial -> {
            Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
            mat.setBoolean("UseMaterialColors",true);
            mat.setColor("Diffuse", entity.get(ShadedColor.class).getDiffuse());
            spatial.setMaterial(mat);
        }));

        unshadedEntities.applyChanges();
        unshadedEntities.getAddedEntities().forEach(entity -> find(entity).forEach(spatial -> {
            Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            spatial.setMaterial(mat);
        }));
        unshadedEntities.getChangedEntities().forEach(entity -> find(entity).forEach(spatial -> {
            Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mat.setColor("Color", entity.get(UnshadedColor.class).getColor());
            spatial.setMaterial(mat);
        }));

        locatedEntities.applyChanges();
        locatedEntities.getAddedEntities().forEach(entity -> find(entity).forEach(spatial ->
                spatial.setLocalTranslation(entity.get(ViewLocation.class).getLocation())));
        locatedEntities.getChangedEntities().forEach(entity -> find(entity).forEach(spatial ->
                spatial.setLocalTranslation(entity.get(ViewLocation.class).getLocation())));

        rotatedEntities.applyChanges();
        rotatedEntities.getAddedEntities().forEach(entity -> find(entity).forEach(spatial ->
                spatial.setLocalRotation(entity.get(ViewRotation.class).getRotation())));
        rotatedEntities.getChangedEntities().forEach(entity -> find(entity).forEach(spatial ->
                spatial.setLocalRotation(entity.get(ViewRotation.class).getRotation())));
*/
        shadedEntities.applyChanges();
        match(containers, shadedEntities.getAddedEntities(),
                ((entity, spatial) -> ShapeViewState.applyShadedMat(entity, spatial, assetManager)));
        match(containers, shadedEntities.getChangedEntities(),
                ((entity, spatial) -> ShapeViewState.applyShadedMat(entity, spatial, assetManager)));

        unshadedEntities.applyChanges();
        match(containers, unshadedEntities.getAddedEntities(),
                ((entity, spatial) -> ShapeViewState.applyUnshadedMat(entity, spatial, assetManager)));
        match(containers, unshadedEntities.getChangedEntities(),
                ((entity, spatial) -> ShapeViewState.applyUnshadedMat(entity, spatial, assetManager)));

        locatedEntities.applyChanges();
        match(containers, locatedEntities.getAddedEntities(), ShapeViewState::applyViewLocation);
        match(containers, locatedEntities.getChangedEntities(), ShapeViewState::applyViewLocation);

        rotatedEntities.applyChanges();
        match(containers, rotatedEntities.getAddedEntities(), ShapeViewState::applyViewRotation);
        match(containers, rotatedEntities.getChangedEntities(), ShapeViewState::applyViewRotation);


    }

    private static void applyShadedMat(Entity entity, Spatial spatial, AssetManager assetManager){
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat.setBoolean("UseMaterialColors",true);
        mat.setColor("Diffuse", entity.get(ShadedColor.class).getDiffuse());
        spatial.setMaterial(mat);
    }

    private static void applyUnshadedMat(Entity entity, Spatial spatial, AssetManager assetManager){
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", entity.get(UnshadedColor.class).getColor());
        spatial.setMaterial(mat);
    }

    private static void applyViewRotation(Entity entity, Spatial spatial){
        spatial.setLocalRotation(entity.get(ViewRotation.class).getRotation());
    }

    private static void applyViewLocation(Entity entity, Spatial spatial){
        spatial.setLocalTranslation(entity.get(ViewLocation.class).getLocation());
    }

    /**
     * Searches the pair of entity and spatial which are in both structures and applies the consumer to it.
     *
     * @param containers
     * @param entities
     * @param consumer
     */
    private static void match(List<SpatialContainer> containers, Set<Entity> entities, BiConsumer<Entity, Spatial> consumer){
        entities.forEach(entity -> containers.stream().map(c -> c.getObject(entity.getId())).filter(
                                c -> c != null).forEach(spatial -> consumer.accept(entity, spatial)));
    }

    /*private Stream<Spatial> find(Entity entity){
        return containers.stream().map(c -> c.getObject(entity.getId())).filter(c -> c != null);
    }*/
}
