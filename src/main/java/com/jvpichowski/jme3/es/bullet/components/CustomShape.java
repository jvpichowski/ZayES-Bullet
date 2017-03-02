package com.jvpichowski.jme3.es.bullet.components;

import com.simsilica.es.EntityComponent;

/**
 * Every physics object needs a collision shape.
 * Only if an entity has this or other *Shape components it can be added to the physics space.
 *
 * This custom shape works together with a CustomShapeFactory. Without the right factory
 * the entity won't be added to the physics space.
 */
public final class CustomShape implements EntityComponent {

    private final Object definition;

    /**
     *
     * @param definition the property which will be given to the factory
     */
    public CustomShape(Object definition) {
        this.definition = definition;
    }

    public CustomShape() {
        this(null);
    }

    public Object getDefinition() {
        return definition;
    }
}
