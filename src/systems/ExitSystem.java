package systems;

import architecture.AppContent;
import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.managers.GroupManager;
import com.artemis.systems.IntervalEntityProcessingSystem;
import com.artemis.utils.ImmutableBag;
import components.Exit;
import components.ExitCollected;
import components.HitBox;
import components.Transformation;
import content.Groups;
import systems.helpers.CollisionHelper;

/**
 *
 */
public class ExitSystem extends IntervalEntityProcessingSystem {

    @Mapper
    ComponentMapper<Transformation> tm;

    @Mapper
    ComponentMapper<HitBox> hm;

    @Mapper
    ComponentMapper<ExitCollected> ecm;

    @Mapper
    ComponentMapper<Exit> em;

    private final AppContent mAppContent;
    private ImmutableBag<Entity> mLumings;

    @SuppressWarnings("unchecked")
    public ExitSystem(AppContent application) {
        super(Aspect.getAspectForAll(
                Exit.class,
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
            if (!ecm.has(luming) && tm.has(luming) && hm.has(luming)) {

                if (CollisionHelper.isHitting(tm.get(exit),
                        hm.get(exit),
                        tm.get(luming),
                        hm.get(luming))) {

                    luming.addComponent(new ExitCollected());
                    luming.changedInWorld();

                    em.get(exit).addCollectedLuming(luming);

                    mAppContent.getMusicEngine().getSound("coin.wav").play();

                    

                }

            }
        }
    }

}
