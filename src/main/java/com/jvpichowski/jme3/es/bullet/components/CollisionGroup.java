package com.jvpichowski.jme3.es.bullet.components;

import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.simsilica.es.EntityComponent;


/**
 * Created by jan on 13.02.2017.
 */
public class CollisionGroup implements EntityComponent {

    private int collideWithGroups;
    private int collisionGroup;


    public CollisionGroup(int collideWithGroups, int collisionGroup) {
        this.collideWithGroups = collideWithGroups;
        this.collisionGroup = collisionGroup;
    }

    public CollisionGroup() {
        this(PhysicsCollisionObject.COLLISION_GROUP_01, PhysicsCollisionObject.COLLISION_GROUP_01);
    }

    public int getCollisionGroup() {
        return collisionGroup;
    }

    public int getCollideWithGroups() {
        return collideWithGroups;
    }

    public static final int COLLISION_GROUP_NONE = 0x00000000;
    public static final int COLLISION_GROUP_01 = 0x00000001;
    public static final int COLLISION_GROUP_02 = 0x00000002;
    public static final int COLLISION_GROUP_03 = 0x00000004;
    public static final int COLLISION_GROUP_04 = 0x00000008;
    public static final int COLLISION_GROUP_05 = 0x00000010;
    public static final int COLLISION_GROUP_06 = 0x00000020;
    public static final int COLLISION_GROUP_07 = 0x00000040;
    public static final int COLLISION_GROUP_08 = 0x00000080;
    public static final int COLLISION_GROUP_09 = 0x00000100;
    public static final int COLLISION_GROUP_10 = 0x00000200;
    public static final int COLLISION_GROUP_11 = 0x00000400;
    public static final int COLLISION_GROUP_12 = 0x00000800;
    public static final int COLLISION_GROUP_13 = 0x00001000;
    public static final int COLLISION_GROUP_14 = 0x00002000;
    public static final int COLLISION_GROUP_15 = 0x00004000;
    public static final int COLLISION_GROUP_16 = 0x00008000;

    public static int setGroup(int groups, int group){
        return groups | group;
    }

    public static boolean hasGroup(int groups, int group){
        return (groups & group) != 0;
    }

    public static int removeGroup(int groups, int group){
        return groups & ~group;
    }

}
