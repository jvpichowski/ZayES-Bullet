package com.jvpichowski.jme3.es.bullet.components;

import com.simsilica.es.EntityComponent;

/**
 * Created by Jan on 30.01.2017.
 */
public final class CollisionShape implements EntityComponent {

    private final com.jme3.bullet.collision.shapes.CollisionShape collisionShape;

    public CollisionShape(com.jme3.bullet.collision.shapes.CollisionShape collisionShape) {
        this.collisionShape = collisionShape;
    }

    public CollisionShape() {
        this(null);
    }

    public com.jme3.bullet.collision.shapes.CollisionShape getCollisionShape() {
        return collisionShape;
    }
}
