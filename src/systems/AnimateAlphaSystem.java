
package systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.IntervalEntityProcessingSystem;
import components.AnimatedAlpha;

/**
 *
 */
public class AnimateAlphaSystem extends IntervalEntityProcessingSystem {

    @Mapper
    ComponentMapper<AnimatedAlpha> aam;

    @SuppressWarnings("unchecked")
    public AnimateAlphaSystem() {
        super(Aspect.getAspectForAll(
                AnimatedAlpha.class
        ), 1.f / 60.f);
    }

    @Override
    protected void process(Entity entity) {
        AnimatedAlpha aa = aam.get(entity);
        aa.addAlpha(world.delta);
    }

}
