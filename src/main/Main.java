/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import architecture.AppStateEnum;
import architecture.Application;
import states.GameOverState;
import states.GameState;
import states.LoadingState;
import states.MainMenuState;
import states.SplashScreenState;

/**
 *
 */
public class Main {

    public enum MyStates implements AppStateEnum {

        SPLASHSCREENSTATE,
        MAINMENUSTATE,
        LOADING,
        GAMESTATE,
        GAME_OVER

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        Application app = new Application("Lummings", args);
        app.setDisplayMode(800, 600, false);

        app.addState(new SplashScreenState());
        app.addState(new MainMenuState());
        app.addState(new LoadingState());
        app.addState(new GameState());
        app.addState(new GameOverState());
        app.setStartingState(MyStates.SPLASHSCREENSTATE);

        app.run();
    }

}
