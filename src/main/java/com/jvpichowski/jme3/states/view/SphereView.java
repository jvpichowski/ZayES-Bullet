package com.jvpichowski.jme3.states.view;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityComponent;
import com.simsilica.es.EntityData;

/**
 *
 */
public final class SphereView implements EntityComponent {

    private final float radius;

    public SphereView(float radius) {
        this.radius = radius;
    }

    public SphereView() {
        this(1f);
    }

    public float getRadius() {
        return radius;
    }

    static final class Container extends ShapeContainer {

        public Container(EntityData ed, AssetManager assetManager, Node root) {
            super(ed, assetManager, root, SphereView.class);
        }

        @Override
        protected Mesh getMesh(Entity e) {
            return new Sphere(16, 16, e.get(SphereView.class).getRadius());
        }
    }
}
