package com.jvpichowski.jme3.es.bullet;

import com.jme3.bullet.collision.shapes.CollisionShape;

/**
 * To use CustomShapes you have to implement this factory and
 * attach it to the BulletSystem.
 */
public interface CustomShapeFactory {

    /**
     *
     * @param definition the property of the custom shape
     * @return a CollisionShape based on this property
     */
    CollisionShape getShape(Object definition);
}
