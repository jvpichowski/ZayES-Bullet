package com.jvpichowski.jme3.es.bullet.extension.view;

import com.jme3.math.Vector3f;
import com.jvpichowski.jme3.es.bullet.components.PhysicsPosition;
import com.jvpichowski.jme3.es.logic.BaseSimpleEntityLogic;
import com.jvpichowski.jme3.states.view.ViewLocation;
import com.simsilica.es.EntityComponent;

/**
 *
 */
public final class PhysicsToViewLocation implements EntityComponent {

    private final Vector3f offset;

    public PhysicsToViewLocation(Vector3f offset) {
        this.offset = offset;
    }

    public PhysicsToViewLocation() {
        this(Vector3f.ZERO);
    }

    public Vector3f getOffset() {
        return offset;
    }

    public static final class Logic extends BaseSimpleEntityLogic {

        @Override
        public void registerComponents() {
            dependsOn(PhysicsToViewLocation.class, PhysicsPosition.class);
        }

        @Override
        public void init() {
            update();
        }

        @Override
        public void update() {
            Vector3f location = get(PhysicsPosition.class).getLocation();
            Vector3f offset = get(PhysicsToViewLocation.class).getOffset();
            set(new ViewLocation(location.add(offset)));
        }
    }

}
