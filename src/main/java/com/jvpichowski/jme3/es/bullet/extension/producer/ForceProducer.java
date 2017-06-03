package com.jvpichowski.jme3.es.bullet.extension.producer;

import com.jme3.math.Vector3f;
import com.jvpichowski.jme3.es.bullet.BulletSystem;
import com.jvpichowski.jme3.es.bullet.components.ForceComponent;
import com.jvpichowski.jme3.es.bullet.components.PhysicsPosition;
import com.jvpichowski.jme3.es.bullet.components.RigidBody;
import com.jvpichowski.jme3.es.logic.BaseSimpleEntityLogic;
import com.simsilica.es.EntityComponent;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntitySet;

import java.util.function.Function;

/**
 *
 */
public final class ForceProducer implements EntityComponent {

    //TODO add BiFunction type producer
    /**
     * Input points from the producer to the position of the affected object
     */
    private final Function<Vector3f, Vector3f> forceDefinition;

    public ForceProducer(Function<Vector3f, Vector3f> forceDefinition) {
        this.forceDefinition = forceDefinition;
    }

    public Function<Vector3f, Vector3f> getForceDefinition() {
        return forceDefinition;
    }


}
