package com.jvpichowski.jme3.es.bullet.extension.fields;

import com.jme3.math.Vector3f;
import com.simsilica.es.EntityComponent;

/**
 * Needs a CollisionShape
 */
public final class ForceField implements EntityComponent {

    private final Vector3f factor;

    public ForceField(Vector3f factor) {
        this.factor = factor;
    }

    public Vector3f getFactor() {
        return factor;
    }
}
