
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
public class LoadingState extends AbstractApplicationState {
    private Sprite insts;
    private Clock mClock;
    private Sprite current;
    private int state;
    private Sprite casemate;
    private Sprite game;

    public LoadingState() {
    }

    @Override
    public AppStateEnum getStateId() {
        return Main.MyStates.LOADING;
    }

    @Override
    public void initialize() {

        current = new Sprite(getGraphicEngine().getTexture("instructions.png"));
        mClock = new Clock();
    }

    @Override
    public void notifyEntering() {
        mClock.restart();
    }



    @Override
    public void handleEvent(Event event) {
        if (event.type == Event.Type.KEY_PRESSED) {
            getAppContent().goToState(Main.MyStates.GAMESTATE);
        }
    }

    @Override
    public void update(Time time) {
        if (mClock.getElapsedTime().asSeconds() > 1.5) {
            getAppContent().goToState(Main.MyStates.GAMESTATE);
        }
    }

    @Override
    public void render() {
        
        final RenderTarget target = getGraphicEngine().getRenderTarget();
        target.clear(Color.BLACK);

        FloatRect a = current.getGlobalBounds();
        
        current.setOrigin(a.width / 2, a.height / 2);
        target.draw(current);
    }


}
