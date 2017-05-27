package com.jvpichowski.jme3.es.bullet.extension.character;

import com.simsilica.es.EntityComponent;

/**
 * Contains the jump count of the character.
 * (read only)
 */
public final class PhysicsCharacterJumpCount implements EntityComponent {

    private final int count;

    PhysicsCharacterJumpCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }
}
