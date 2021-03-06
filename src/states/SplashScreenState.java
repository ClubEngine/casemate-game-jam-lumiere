
package states;

import architecture.AbstractApplicationState;
import architecture.AppStateEnum;
import main.Main;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.FloatRect;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Sprite;
import org.jsfml.system.Clock;
import org.jsfml.system.Time;
import org.jsfml.window.event.Event;

/**
 *
 */
public class SplashScreenState extends AbstractApplicationState {
    private Sprite sfml;
    private Clock mClock;
    private Sprite current;
    private int state;
    private Sprite casemate;
    private Sprite game;

    public SplashScreenState() {
    }

    @Override
    public AppStateEnum getStateId() {
        return Main.MyStates.SPLASHSCREENSTATE;
    }

    @Override
    public void initialize() {

        sfml = new Sprite(getGraphicEngine().getTexture("sfml-logo-small.png"));
        casemate = new Sprite(getGraphicEngine().getTexture("lacasemate-logo.png"));
        game = new Sprite(getGraphicEngine().getTexture("logo.png"));
        
        current = sfml;
        mClock = new Clock();
        state = 1;     
    }


    @Override
    public void handleEvent(Event event) {
        if (event.type == Event.Type.KEY_PRESSED) {
            getAppContent().goToState(Main.MyStates.MAINMENUSTATE);
        }
    }

    @Override
    public void update(Time time) {
        if (state == 1 && mClock.getElapsedTime().asSeconds() > 1) {
            state = 2;
            mClock.restart();
            current = casemate;
        } else if (state == 2 && mClock.getElapsedTime().asSeconds() > 1) {
            state = 3;
            mClock.restart();
            current = game;
        } else if (state == 3 && mClock.getElapsedTime().asSeconds() > 1) {
            getAppContent().goToState(Main.MyStates.MAINMENUSTATE);
        }
    }

    @Override
    public void render() {
        
        final RenderTarget target = getGraphicEngine().getRenderTarget();
        getGraphicEngine().resetView();
        target.clear(Color.BLACK);

        FloatRect a = current.getLocalBounds();
        
        current.setOrigin(a.width / 2, a.height / 2);
        current.setPosition(400, 300);
        target.draw(current);
    }


}
