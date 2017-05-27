package com.jvpichowski.jme3.es.bullet.extension.character;

import com.simsilica.es.EntityComponent;

/**
 * Define the base properties of the character
 */
public final class PhysicsCharacter implements EntityComponent {

    private final float radius;
    private final float height; // the whole height of the character
    private final float mass;

    private final float maxSpeed; //m/s
    private final float acceleration; //m/s^2

    private final float stepHeight; // the height of steps that this character can climb
    private final float jumpHeight; // the max jump height a character can reach with a single jump
    private final int maxJumpNumber; //Double JumpState, 3 = Triple JumpState, 1 = normal jump, 0 = no jumping


    /**
     * We use base units.
     *
     * @param radius the radius of the character
     * @param height the whole height of the character
     * @param mass the mass of the character
     * @param maxSpeed max move speed of the character
     * @param acceleration acceleration of the character
     * @param stepHeight the height of steps that this character can climb
     * @param jumpHeight the max jump height a character can reach with a single jump
     * @param maxJumpNumber 2 = double jump, 3 = triple jump, 1 = normal jump, 0 = no jumping
     */
    public PhysicsCharacter(float radius, float height, float mass,
                            float maxSpeed, float acceleration,
                            float stepHeight, float jumpHeight, int maxJumpNumber) {
        this.radius = radius;
        this.height = height;
        this.mass = mass;
        this.maxSpeed = maxSpeed;
        this.acceleration = acceleration;
        this.stepHeight = stepHeight;
        this.jumpHeight = jumpHeight;
        this.maxJumpNumber = maxJumpNumber;
    }

    public PhysicsCharacter(float radius, float height, float mass, float maxSpeed, float acceleration) {
        this.radius = radius;
        this.height = height;
        this.mass = mass;
        this.maxSpeed = maxSpeed;
        this.acceleration = acceleration;
        this.stepHeight = 0.2f;
        this.jumpHeight = 0.5f;
        this.maxJumpNumber = 1;
    }

    public PhysicsCharacter(float radius, float height, float mass, float stepHeight, float jumpHeight, int maxJumpNumber) {
        this.radius = radius;
        this.height = height;
        this.mass = mass;
        this.stepHeight = stepHeight;
        this.jumpHeight = jumpHeight;
        this.maxJumpNumber = maxJumpNumber;
        this.acceleration = 2;
        this.maxSpeed = 5;
    }

    public PhysicsCharacter(float radius, float height, float mass) {
        this.radius = radius;
        this.height = height;
        this.mass = mass;
        this.maxSpeed = 5;
        this.acceleration = 2;
        this.stepHeight = 0.2f;
        this.jumpHeight = 0.5f;
        this.maxJumpNumber = 1;
    }

    public float getRadius() {
        return radius;
    }

    public float getHeight() {
        return height;
    }

    public float getMass() {
        return mass;
    }

    public float getMaxSpeed() {
        return maxSpeed;
    }

    public float getAcceleration() {
        return acceleration;
    }

    public float getStepHeight() {
        return stepHeight;
    }

    public float getJumpHeight() {
        return jumpHeight;
    }

    public int getMaxJumpNumber() {
        return maxJumpNumber;
    }
}
