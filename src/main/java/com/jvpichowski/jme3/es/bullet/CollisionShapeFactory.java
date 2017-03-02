package com.jvpichowski.jme3.es.bullet;

import com.jme3.bullet.collision.shapes.CollisionShape;

/**
 * Default factory for CustomShapes.
 * It can only handle CollisionShapes as definition property of the CustomShapes.
 */
public class CollisionShapeFactory implements CustomShapeFactory {

    /**
     * @param definition the CustomShape with a CollisionShape as property
     * @return the CollisionShape
     */
    @Override
    public CollisionShape getShape(Object definition) {
        if(definition instanceof CollisionShape) {
            return (CollisionShape)definition;
        }
        return null;
    }
}
