/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import architecture.AppStateEnum;
import architecture.Application;
import states.GameState;
import states.MainMenuState;
import states.SplashScreenState;

/**
 *
 */
public class Main {

    public enum MyStates implements AppStateEnum {

        SPLASHSCREENSTATE,
        MAINMENUSTATE,
        GAMESTATE;

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        Application app = new Application("Lumings", args);
        app.setDisplayMode(800, 600, false);

        app.addState(new SplashScreenState());
        app.addState(new MainMenuState());
        app.addState(new GameState());
        app.setStartingState(MyStates.SPLASHSCREENSTATE);

        app.run();
    }

}
