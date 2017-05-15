package com.jvpichowski.jme3.states.view;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityComponent;
import com.simsilica.es.EntityData;

/**
 * A simple box
 */
public final class BoxView implements EntityComponent {

    private final Vector3f extents;

    public BoxView(Vector3f extents) {
        this.extents = extents;
    }

    public BoxView() {
        this(new Vector3f(0.5f, 0.5f, 0.5f));
    }

    public Vector3f getExtents() {
        return extents;
    }

    static final class Container extends ShapeContainer {

        public Container(EntityData ed, AssetManager assetManager, Node root) {
            super(ed, assetManager, root, BoxView.class);
        }

        protected Mesh getMesh(Entity e) {
            Vector3f extents = e.get(BoxView.class).getExtents();
            return new Box(extents.x, extents.y, extents.z);
        }

    }
}
