package com.jvpichowski.jme3.es.bullet.components;

import com.jme3.math.Vector3f;
import com.simsilica.es.EntityComponent;

/**
 * Will be set to empty after each impulse is applied.
 */
public final class CombinedImpulses implements EntityComponent {

    private final ImpulseDefinition[] impulses;

    public CombinedImpulses(){
        impulses = new ImpulseDefinition[0];
    }

    public CombinedImpulses(Vector3f impulse, Vector3f relativeLocation){
        impulses = new ImpulseDefinition[]{new ImpulseDefinition(impulse, relativeLocation)};
    }

    private CombinedImpulses(ImpulseDefinition[] listedImpulses, Vector3f impulse, Vector3f relativeLocation){
        impulses = new ImpulseDefinition[listedImpulses.length+1];
        System.arraycopy(listedImpulses, 0, impulses, 0, listedImpulses.length);
        impulses[impulses.length-1] = new ImpulseDefinition(impulse, relativeLocation);
    }

    /**
     * Non of the parameters are allowed to be null
     *
     * @param impulse
     * @param relativeLocation
     * @return
     */
    public CombinedImpulses addImpulse(Vector3f impulse, Vector3f relativeLocation){
        return new CombinedImpulses(impulses, impulse, relativeLocation);
    }

    public int numImpulses(){
        return impulses.length;
    }

    public Vector3f getImpulse(int num) {
        return impulses[num].impulse;
    }

    public Vector3f getRelativeLocation(int num){
        return impulses[num].relativeLocation;
    }

    private static final class ImpulseDefinition {

        public final Vector3f impulse;
        public final Vector3f relativeLocation;

        public ImpulseDefinition(Vector3f impulse, Vector3f relativeLocation) {
            this.impulse = impulse;
            this.relativeLocation = relativeLocation;
        }
    }

}
