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
import components.Gate;
import components.GateReversed;
import components.HitBox;
import components.Transformation;
import content.Groups;
import systems.helpers.CollisionHelper;

/**
 *
 */
public class GateSystem extends IntervalEntityProcessingSystem {

    @Mapper
    ComponentMapper<Transformation> tm;

    @Mapper
    ComponentMapper<HitBox> hm;

    @Mapper
    ComponentMapper<Gate> gm;

    @Mapper
    ComponentMapper<AILuming> ailm;

    @Mapper
    ComponentMapper<GateReversed> grm;

    private final AppContent mAppContent;
    private ImmutableBag<Entity> mLumings;

    @SuppressWarnings("unchecked")
    public GateSystem(AppContent application) {
        super(Aspect.getAspectForAll(
                Gate.class,
                Transformation.class,
                HitBox.class
        ), 0.1f);
        mAppContent = application;
    }

    @Override
    protected void begin() {
        GroupManager groupm = world.getManager(GroupManager.class);
        mLumings = groupm.getEntities(Groups.LUMINGS);
    }

    @Override
    protected void process(Entity gate) {

        for (int i = 0, s = mLumings.size(); i < s; ++i) {
            Entity luming = mLumings.get(i);
            if (tm.has(luming) && hm.has(luming)) {

                if (CollisionHelper.isHitting(tm.get(gate),
                        hm.get(gate),
                        tm.get(luming),
                        hm.get(luming))) {

                    AILuming ail = ailm.get(luming);
                    final int color = ail.getMaskColor();

                    if ((color & gm.get(gate).getMaskColor()) == 0) {
                        if (!grm.has(luming)) {
                            luming.addComponent(new GateReversed());
                            luming.changedInWorld();
                        }
                    }

                }

            }
        }
    }

}
