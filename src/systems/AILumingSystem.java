package systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import components.AILuming;
import static components.AILuming.LumState.DEAD;
import static components.AILuming.LumState.FALLING;
import static components.AILuming.LumState.MOVING;
import components.CollideWithMap;
import components.HitBox;
import components.MultipleAnimations;
import components.Orientation;
import components.Orientation.Direction;
import static components.Orientation.Direction.LEFT;
import static components.Orientation.Direction.RIGHT;
import components.Transformation;
import components.Velocity;
import map.Map;
import org.jsfml.graphics.FloatRect;
import org.jsfml.system.Vector2f;
import systems.helpers.AnimationHelper;

/**
 *
 */
public class AILumingSystem extends EntityProcessingSystem {

    static final float LUM_VELOCITY_X = 60;
    static final int MAX_FALLING_COUNTER = 120;
    static final float GRAVITY = 100;


    @Mapper
    ComponentMapper<AILuming> ailumm;

    @Mapper
    ComponentMapper<Velocity> vm;

    @Mapper
    ComponentMapper<Transformation> tm;

    @Mapper
    ComponentMapper<HitBox> hm;

    @Mapper
    ComponentMapper<MultipleAnimations> mam;

    @Mapper
    ComponentMapper<Orientation> om;

    @Mapper
    ComponentMapper<CollideWithMap> cwmm;

    private final Map mMap;

    @SuppressWarnings("unchecked")
    public AILumingSystem(Map map) {
        super(Aspect.getAspectForAll(
                AILuming.class,
                Velocity.class,
                Transformation.class,
                HitBox.class,
                CollideWithMap.class,
                MultipleAnimations.class,
                Orientation.class
        ));
        mMap = map;
    }

    @Override
    protected void begin() {

    }

    @Override
    protected void process(Entity entity) {
        AILuming ailum = ailumm.get(entity);
        Transformation t = tm.get(entity);
        Velocity velocity = vm.get(entity);
        Vector2f pos = t.getTransformable().getPosition();
        FloatRect hitbox = hm.get(entity).getHitBox();
        MultipleAnimations ma = mam.get(entity);
        Orientation orientation = om.get(entity);
        CollideWithMap collideMap = cwmm.get(entity);

        final AILuming.LumState currentState = ailum.getState();
        AILuming.LumState nextState = currentState;

        switch (currentState) {
            case INIT:
                nextState = FALLING;
                velocity.setVelocity(0, GRAVITY);
                orientation.setDirection(Orientation.Direction.RIGHT);
                break;

            case MOVING:
                if (isThereWall(t, hitbox, orientation.getDirection())) {
                    System.out.println("Wall");
                    if (orientation.getDirection() == LEFT) {
                        orientation.setDirection(RIGHT);
                    } else if (orientation.getDirection() == RIGHT) {
                        orientation.setDirection(LEFT);
                    }
                    setVelByOrientation(velocity, orientation);
                    AnimationHelper.setAnimationByOrientation(ma, orientation);
                } else if (!isThereGround(t, hitbox)) {
                    nextState = FALLING;
                    velocity.setVelocity(0, GRAVITY);
                    ailum.rstFallingCounter();
                }
                break;

            case FALLING:
                if (isThereGround(t, hitbox)) {
                    nextState = MOVING;
                    setVelByOrientation(velocity, orientation);
                } else if (ailum.getFallingCounter() >= MAX_FALLING_COUNTER) {
                    nextState = DEAD;
                    System.out.println("Dead");
                } else {
                    ailum.incrFallingCounter();
                }
                break;

            case DEAD:

                break;
        }

        ailum.setState(nextState);

    }

    private boolean isThereWall(Transformation t, FloatRect hitbox, Direction direction) {
        Vector2f pos = t.getPosition();
        float y = pos.y - hitbox.height / 2;
        float x = pos.x;

        float off = hitbox.width / 2 + 1;

        if (direction == RIGHT) {
            x += off;
        } else if (direction == LEFT) {
            x -= off;
        }

        return mMap.isThereABlock(x, y);
    }

    private boolean isThereGround(Transformation t, FloatRect hitbox) {
        Vector2f pos = t.getPosition();
        float y = pos.y + 1;
        float x = pos.x;
        float off_x = hitbox.width / 2;

        return mMap.isThereABlock(x, y)
                || mMap.isThereABlock(x + off_x, y)
                || mMap.isThereABlock(x - off_x, y);
    }

    private void setVelByOrientation(Velocity velocity, Orientation orientation) {
        if (orientation.getDirection() == LEFT) {
            velocity.setVelocity(-LUM_VELOCITY_X, 0);
        } else if (orientation.getDirection() == RIGHT) {
            velocity.setVelocity(LUM_VELOCITY_X, 0);
        }
    }

}
