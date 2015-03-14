package systems;

import architecture.AppContent;
import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.managers.GroupManager;
import com.artemis.systems.IntervalEntityProcessingSystem;
import com.artemis.utils.ImmutableBag;
import components.AILuming;
import components.Filter;
import components.HitBox;
import components.Transformation;
import content.Groups;
import systems.helpers.CollisionHelper;

/**
 *
 */
public class FilterSystem extends IntervalEntityProcessingSystem {

    @Mapper
    ComponentMapper<Transformation> tm;

    @Mapper
    ComponentMapper<HitBox> hm;

    @Mapper
    ComponentMapper<Filter> fm;

    @Mapper
    ComponentMapper<AILuming> ailm;

    private final AppContent mAppContent;
    private ImmutableBag<Entity> mLumings;

    @SuppressWarnings("unchecked")
    public FilterSystem(AppContent application) {
        super(Aspect.getAspectForAll(
                Filter.class,
                Transformation.class,
                HitBox.class
        ), 0.1f);
        mAppContent = application;
    }

    @Override
    protected void begin() {
        GroupManager gm = world.getManager(GroupManager.class);
        mLumings = gm.getEntities(Groups.LUMINGS);
    }

    @Override
    protected void process(Entity exit) {

        for (int i = 0, s = mLumings.size(); i < s; ++i) {
            Entity luming = mLumings.get(i);
            if (tm.has(luming) && hm.has(luming)) {

                if (CollisionHelper.isHitting(tm.get(exit),
                        hm.get(exit),
                        tm.get(luming),
                        hm.get(luming))) {

                    AILuming ail = ailm.get(luming);
                    final int color = ail.getMaskColor();
                    int newColor = color & fm.get(exit).getMaskColor();
                    ail.setMaskColor(newColor);
                    if (newColor != color) {
                        ail.colorChanged();
                    }
                }

            }
        }
    }

}
