
package systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.IntervalEntityProcessingSystem;
import components.Exit;
import states.GameState;

/**
 *
 */
public class CheckLevelEndSystem extends IntervalEntityProcessingSystem {

    @Mapper
    ComponentMapper<Exit> em;
    private int totalExits;
    private int totalOk;
    private final GameState mGame;

    @SuppressWarnings("unchecked")
    public CheckLevelEndSystem(GameState game) {
        super(Aspect.getAspectForAll(
                Exit.class
        ), 1.f);
        mGame = game;
    }

    @Override
    protected void begin() {
        totalExits = getActives().size();
        totalOk = 0;
    }

    @Override
    protected void process(Entity entity) {
        Exit e = em.get(entity);
        if (e.isOk()) {
            totalOk++;
        }
    }

    @Override
    protected void end() {
        if (totalExits == totalOk) {
            mGame.levelFinish();
        }
    }


}
