package com.jvpichowski.jme3.es.logic;

/**
 * A logic definition which could be used with script engines and so on..
 */
public interface EntityLogic {

    /**
     * This method is called if a new entity is created which fits to the needed components
     */
    void init();

    /**
     * This method is called if a value of the needed components has changed
     */
    void update();

    /**
     * This method is called if an entity with the needed components
     * is destroyed or if a needed component is removed from it.
     */
    void destroy();

    /**
     * This method is called every time the logic should be executed.
     */
    void run();
}
