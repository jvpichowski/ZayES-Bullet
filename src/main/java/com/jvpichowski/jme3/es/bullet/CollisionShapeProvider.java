package com.jvpichowski.jme3.es.bullet;

import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jvpichowski.jme3.es.bullet.components.BoxShape;
import com.jvpichowski.jme3.es.bullet.components.CustomShape;
import com.jvpichowski.jme3.es.bullet.components.SphereShape;
import com.simsilica.es.EntityComponent;

/**
 * A factory class which provides a CollisionShape based on the shape component.
 * It is used in RigidBodyContainer and SingleTypeContainer.
 *
 * @param <T>
 */
interface CollisionShapeProvider <T extends EntityComponent> {

    CollisionShape getShape(T definition);

    static CollisionShapeProvider<CustomShape> customShapeProvider(CustomShapeFactory factory){
        return definition -> factory.getShape(definition.getDefinition());
    }

    static CollisionShapeProvider<SphereShape> sphereShapeProvider(){
        return definition -> new SphereCollisionShape(definition.getRadius());
    }

    static CollisionShapeProvider<BoxShape> boxShapeProvider(){
        return definition -> new BoxCollisionShape(definition.getHalfExtends());
    }
}
