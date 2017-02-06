package com.jvpichowski.jme3.es.bullet;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.simsilica.es.EntityComponent;

/**
 * Created by Jan on 30.01.2017.
 */
public final class CollisionShapeComponent implements EntityComponent {

    private final CollisionShape collisionShape;

    public CollisionShapeComponent(CollisionShape collisionShape) {
        this.collisionShape = collisionShape;
    }

    public CollisionShapeComponent() {
        this(null);
    }

    public CollisionShape getCollisionShape() {
        return collisionShape;
    }
}
