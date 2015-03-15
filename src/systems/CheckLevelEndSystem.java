
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
    private final int totalRequested;
    private int totalLumingsCollected;
    private final GameState mGame;

    @SuppressWarnings("unchecked")
    public CheckLevelEndSystem(GameState game, int totalLumsRequested) {
        super(Aspect.getAspectForAll(
                Exit.class
        ), 1.f);
        mGame = game;
        totalRequested = totalLumsRequested;
    }

    @Override
    protected void begin() {
        totalLumingsCollected = 0;
    }

    @Override
    protected void process(Entity entity) {
        Exit e = em.get(entity);
        
        totalLumingsCollected += e.getNumberLumingsCollected();
        
    }

    @Override
    protected void end() {
        if (totalLumingsCollected >= totalRequested) {
            mGame.levelFinish();
        }
        //System.out.println("lums collected = " + totalLumingsCollected);
    }


}
