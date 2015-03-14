
package systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.IntervalEntityProcessingSystem;
import components.GateReversed;

/**
 *
 */
public class GateReversedSystem extends IntervalEntityProcessingSystem {

    @Mapper
    ComponentMapper<GateReversed> gm;

    @SuppressWarnings("unchecked")
    public GateReversedSystem() {
        super(Aspect.getAspectForAll(
                GateReversed.class
        ), 1.f / 60.f);
    }

    @Override
    protected void process(Entity entity) {
        GateReversed g = gm.get(entity);

        g.incrDate();

        if (g.isAcquit() > 50) {
            entity.removeComponent(GateReversed.class);
            entity.changedInWorld();
        }
    }

}
