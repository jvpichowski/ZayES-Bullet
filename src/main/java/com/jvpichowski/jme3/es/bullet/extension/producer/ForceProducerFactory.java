package com.jvpichowski.jme3.es.bullet.extension.producer;

import com.jme3.math.Vector3f;
import com.jvpichowski.jme3.es.bullet.components.Force;

import java.util.function.Function;

/**
 *
 */
public final class ForceProducerFactory {

    private ForceProducerFactory() {}

    public static ForceProducer createForceFieldProducer(Vector3f halfExtents, Vector3f force){
        return new ForceProducer(r  -> {
            if(r.x <= halfExtents.x && r.x >= -halfExtents.x
                    && r.y <= halfExtents.y && r.y >= -halfExtents.y
                    && r.z <= halfExtents.z && r.z >= -halfExtents.z){
                return force;
            }
            return Vector3f.ZERO;
        });
    }

    /**
     *
     * @param factor
     * @param radius
     * @return
     */
    public static ForceProducer createConstantBoundedForceProducer(float factor, float radius){
        return new ForceProducer(r -> {
            if(r.lengthSquared() > radius*radius){
                return Vector3f.ZERO;
            }
            return r.normalize().mult(factor);
        });
    }

    /**
     *
     * @param factor negative pulling towards the producer, positive pushing from the producer
     * @return F(x) = factor*r
     */
    public static ForceProducer createLinearForceProducer(float factor){
        return new ForceProducer(r -> r.normalize().mult(r.length()*factor));
    }

    /**
     * Pushing
     * @param factor
     * @return F(x) = factor/r
     */
    public static ForceProducer createAntiLinearForceProducer(float factor){
        return new ForceProducer(r -> r.normalize().mult(factor/r.length()));
    }

    /**
     *
     * @param factor
     * @return F(r) = factor*r*r
     */
    public static ForceProducer createQuadraticForceProducer(float factor){
        return new ForceProducer(r -> r.normalize().mult(r.lengthSquared()*factor));
    }

    /**
     *
     * @param factor
     * @return F(r) = factor/r^2
     */
    public static ForceProducer createAntiQuadraticForceProducer(float factor){
        return new ForceProducer(r -> r.normalize().mult(factor/r.lengthSquared()));
    }
}
