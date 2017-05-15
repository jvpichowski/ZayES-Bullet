package com.jvpichowski.jme3.es.bullet.extension.view;

import com.jme3.math.Quaternion;
import com.jvpichowski.jme3.es.bullet.components.PhysicsPosition;
import com.jvpichowski.jme3.es.logic.BaseSimpleEntityLogic;
import com.jvpichowski.jme3.states.view.ViewRotation;
import com.simsilica.es.EntityComponent;

/**
 *
 */
public final class PhysicsToViewRotation implements EntityComponent {

    private final Quaternion offset;

    public PhysicsToViewRotation(Quaternion offset) {
        this.offset = offset;
    }

    public PhysicsToViewRotation() {
        this(Quaternion.DIRECTION_Z);
    }

    public Quaternion getOffset() {
        return offset;
    }

    public static final class Logic extends BaseSimpleEntityLogic {

        @Override
        public void registerComponents() {
            dependsOn(PhysicsToViewRotation.class, PhysicsPosition.class);
        }

        @Override
        public void init() {
            update();
        }

        @Override
        public void update() {
            Quaternion rotation = get(PhysicsPosition.class).getRotation();
            Quaternion offset = get(PhysicsToViewRotation.class).getOffset();
            set(new ViewRotation(rotation.mult(offset)));
        }
    }
}
