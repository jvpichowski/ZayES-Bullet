package com.jvpichowski.jme3.es.bullet.extension.character;

import com.simsilica.es.EntityComponent;

/**
 *
 */
public final class PhysicsCharacterState implements EntityComponent {

    private final boolean jumping;
    private final boolean ducking;

    public PhysicsCharacterState(boolean jumping, boolean ducking) {
        this.jumping = jumping;
        this.ducking = ducking;
    }

    public boolean isJumping() {
        return jumping;
    }

    public boolean isDucking() {
        return ducking;
    }
}
