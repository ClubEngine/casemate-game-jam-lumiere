
package states;

import architecture.AbstractApplicationState;
import architecture.AppStateEnum;
import main.Main;
import org.jsfml.audio.Music;
import org.jsfml.graphics.FloatRect;
import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Sprite;
import org.jsfml.system.Time;
import org.jsfml.system.Vector2i;
import org.jsfml.window.event.Event;
import sounds.MusicEngine;

/**
 *
 */
public class MainMenuState extends AbstractApplicationState {
    private Sprite back;
    private Vector2i mCursorPosition = Vector2i.ZERO;

    @Override
    public AppStateEnum getStateId() {
        return Main.MyStates.MAINMENUSTATE;
    }

    @Override
    public void initialize() {
        

        back = new Sprite(getGraphicEngine().getTexture("Menu.png"));
    }

    @Override
    public void notifyEntering() {
        MusicEngine mesMusiques = getAppContent().getMusicEngine();
        Music gameMusic = mesMusiques.getMusic("Digital_Native.ogg");
        gameMusic.play();
    }



    @Override
    public void handleEvent(Event event) {

        if (event.type == Event.Type.KEY_PRESSED) {
            switch (event.asKeyEvent().key) {
                case ESCAPE:
                    getAppContent().exit();
                    break;
            }
            
        } else if (event.type == Event.Type.MOUSE_MOVED) {
            mCursorPosition = event.asMouseEvent().position;
        } else if (event.type == Event.Type.MOUSE_BUTTON_RELEASED) {
            if (new IntRect(330, 320, 128, 50).contains(mCursorPosition)) { // play
                getAppContent().getOptions().set("prefix", "Level");
                getAppContent().getOptions().set("interface", true);
                getAppContent().goToState(Main.MyStates.LOADING);
            } else if (new IntRect(270, 400, 256, 50).contains(mCursorPosition)) { // demo
                getAppContent().getOptions().set("prefix", "demo");
                getAppContent().getOptions().set("interface", false);
                getAppContent().goToState(Main.MyStates.LOADING);
            } else if (new IntRect(320, 500, 150, 50).contains(mCursorPosition)) { // exit
                getAppContent().exit();
            }
        }
    }

    @Override
    public void update(Time time) {
        
    }

    @Override
    public void render() {
        final RenderTarget target = getGraphicEngine().getRenderTarget();

        FloatRect a = back.getGlobalBounds();

        back.setOrigin(a.width / 2, a.height / 2);

        target.draw(back);
    }


}
