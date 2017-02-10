package com.jvpichowski.jme3.states;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;

/**
 * Created by jan on 11.02.2017.
 */
public class CameraPostionPrinter extends BaseAppState implements ActionListener{

    private Application app;

    @Override
    protected void initialize(Application app) {
        this.app = app;
        app.getInputManager().addMapping("PrintCameraPosition", new KeyTrigger(KeyInput.KEY_F8));
    }

    @Override
    protected void cleanup(Application app) {
        app.getInputManager().deleteMapping("PrintCameraPosition");
    }

    @Override
    protected void onEnable() {
        app.getInputManager().addListener(this, "PrintCameraPosition");
    }

    @Override
    protected void onDisable() {
        app.getInputManager().removeListener(this);
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if(isPressed){
            System.out.println("Location: "+app.getCamera().getLocation()+" LEFT: "+app.getCamera().getLeft()+ " UP: "+app.getCamera().getUp()+" DIR: "+app.getCamera().getDirection());
        }
    }
}
