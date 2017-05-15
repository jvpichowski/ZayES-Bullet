package com.jvpichowski.jme3.states.view;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Cylinder;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityComponent;
import com.simsilica.es.EntityData;

/**
 *
 */
public final class CylinderView implements EntityComponent {

    private final float height;
    private final float radius;

    public CylinderView(float radius, float height) {
        this.height = height;
        this.radius = radius;
    }

    public CylinderView() {
        this(1,1);
    }

    public float getHeight() {
        return height;
    }

    public float getRadius() {
        return radius;
    }

    static final class Container extends ShapeContainer {

        public Container(EntityData ed, AssetManager assetManager, Node root) {
            super(ed, assetManager, root, CylinderView.class);
        }

        @Override
        protected Mesh getMesh(Entity e) {
            CylinderView cylinder = e.get(CylinderView.class);
            return new Cylinder(16, 16, cylinder.getRadius(), cylinder.getHeight(), true);
        }
    }
}
