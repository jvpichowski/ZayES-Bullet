package com.jvpichowski.jme3.es.bullet.components;

import com.simsilica.es.EntityComponent;

/**
 * Every physics object needs a collision shape.
 * Only if an entity has this component it can be added to the physics space.
 *
 * Warning: It could be possible that I remove this component in favor for
 * concrete shape components.
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
