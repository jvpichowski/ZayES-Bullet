package com.jvpichowski.jme3.es.bullet.components;

import com.jme3.math.Vector3f;
import com.simsilica.es.EntityComponent;

/**
 * Set the linear and angular factor(freedom of movement) of a RigidBody.
 * It will be applied in the next physics tick.
 */
public final class Factor implements EntityComponent {

    private final Vector3f linearFactor;
    private final Vector3f angularFactor;
    //private final float singleAngularFactor;

    public Factor(Vector3f linearFactor, Vector3f angularFactor/*, float singleAngularFactor*/) {
        this.linearFactor = linearFactor;
        this.angularFactor = angularFactor;
        //this.singleAngularFactor = singleAngularFactor;
    }

    /**
     * Default factors(full freedom^, same as in bullet): Linear (1,1,1) Angular (1,1,1)
     */
    public Factor() {
        this(new Vector3f(1,1,1), new Vector3f(1,1,1)/*, 1*/);
    }

    public Vector3f getLinearFactor() {
        return linearFactor;
    }

    //public float getSingleAngularFactor() {
    //    return singleAngularFactor;
    //}

    public Vector3f getAngularFactor() {
        return angularFactor;
    }
}
