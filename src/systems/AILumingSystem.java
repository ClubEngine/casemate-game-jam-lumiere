package systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import components.AILuming;
import static components.AILuming.LumState.COLLECTED;
import static components.AILuming.LumState.DEAD;
import static components.AILuming.LumState.FALLING;
import static components.AILuming.LumState.MOVING;
import components.AnimatedAlpha;
import components.CollideWithMap;
import components.ExitCollected;
import components.GateReversed;
import components.HitBox;
import components.MultipleAnimations;
import components.MultipleTextures;
import components.Orientation;
import components.Orientation.Direction;
import static components.Orientation.Direction.LEFT;
import static components.Orientation.Direction.RIGHT;
import components.Transformation;
import components.Velocity;
import content.Animations;
import static content.Animations.EXIT;
import static content.Masks.COLOR_BLUE;
import static content.Masks.COLOR_GREEN;
import static content.Masks.COLOR_RED;
import static content.Textures.LUMING_BLUE;
import static content.Textures.LUMING_CYAN;
import static content.Textures.LUMING_GREEN;
import static content.Textures.LUMING_MAGENTA;
import static content.Textures.LUMING_RED;
import static content.Textures.LUMING_WHITE;
import static content.Textures.LUMING_YELLOW;
import map.Map;
import org.jsfml.graphics.FloatRect;
import org.jsfml.system.Vector2f;
import systems.helpers.AnimationHelper;

/**
 *
 */
public class AILumingSystem extends EntityProcessingSystem {

    static final float LUM_VELOCITY_X = 60;
    static final int MAX_FALLING_COUNTER = 90;
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
    ComponentMapper<MultipleTextures> mtm;

    @Mapper
    ComponentMapper<Orientation> om;

    @Mapper
    ComponentMapper<CollideWithMap> cwmm;

    @Mapper
    ComponentMapper<ExitCollected> ecm;

    @Mapper
    ComponentMapper<GateReversed> grm;

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
                MultipleTextures.class,
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
        MultipleTextures multTexs = mtm.get(entity);

        final AILuming.LumState currentState = ailum.getState();
        AILuming.LumState nextState = currentState;

        if (ailum.colorHasChanged()) {
            int color = ailum.getMaskColor();

            if (color == COLOR_RED) {
                multTexs.setTexture(LUMING_RED);
            } else if (color == COLOR_GREEN) {
                multTexs.setTexture(LUMING_GREEN);
            } else if (color == COLOR_BLUE) {
                multTexs.setTexture(LUMING_BLUE);
            } else if (color == (COLOR_RED | COLOR_GREEN)) {
                multTexs.setTexture(LUMING_YELLOW);
            } else if (color == (COLOR_RED | COLOR_BLUE)) {
                multTexs.setTexture(LUMING_MAGENTA);
            } else if (color == (COLOR_GREEN | COLOR_BLUE)) {
                multTexs.setTexture(LUMING_CYAN);
            } else if (color == (COLOR_RED | COLOR_GREEN | COLOR_BLUE)) {
                multTexs.setTexture(LUMING_WHITE);
            } else if (color == 0) {
                nextState = DEAD;
                ma.setAnimation(Animations.DEAD);
                velocity.setVelocity(Vector2f.ZERO);
                entity.addComponent(new AnimatedAlpha(255, -400));
                entity.changedInWorld();
            }
        }

        switch (currentState) {
            case INIT:
                nextState = FALLING;
                velocity.setVelocity(0, GRAVITY);
                orientation.setDirection(Orientation.Direction.RIGHT);
                break;

            case MOVING:
                if (isThereWall(t, hitbox, orientation.getDirection())) {
                    System.out.println("Wall");
                    reverseDirection(orientation, velocity, ma);
                } else if (!isThereGround(t, hitbox)) {
                    nextState = FALLING;
                    ma.setAnimation(Animations.FALLING);
                    velocity.setVelocity(0, GRAVITY);
                    ailum.rstFallingCounter();
                } else if (ecm.has(entity)) {
                    nextState = COLLECTED;
                    velocity.setVelocity(Vector2f.ZERO);
                    ma.setAnimation(EXIT);

                    entity.addComponent(new AnimatedAlpha(255, -400));
                    entity.removeComponent(HitBox.class);
                    entity.removeComponent(CollideWithMap.class);
                    entity.changedInWorld();
                } else if (grm.has(entity) && grm.get(entity).acquit()) {
                    reverseDirection(orientation, velocity, ma);
                }
                break;

            case FALLING:
                if (isThereGround(t, hitbox)) {
                    nextState = MOVING;
                    setVelByOrientation(velocity, orientation);
                    AnimationHelper.setAnimationByOrientation(ma, orientation);
                } else if (ailum.getFallingCounter() >= MAX_FALLING_COUNTER) {
                    nextState = DEAD;
                    ma.setAnimation(Animations.DEAD);
                    entity.addComponent(new AnimatedAlpha(255, -400));
                    entity.changedInWorld();
                    System.out.println("Dead");
                } else {
                    ailum.incrFallingCounter();
                }
                break;

            case DEAD:
                entity.removeComponent(HitBox.class);
                entity.removeComponent(CollideWithMap.class);
                entity.changedInWorld();
                break;

            case COLLECTED:
                break;
        }

        ailum.setState(nextState);
    }

    private void reverseDirection(Orientation orientation, Velocity velocity, MultipleAnimations ma) {
        if (orientation.getDirection() == LEFT) {
            orientation.setDirection(RIGHT);
        } else if (orientation.getDirection() == RIGHT) {
            orientation.setDirection(LEFT);
        }
        setVelByOrientation(velocity, orientation);
        AnimationHelper.setAnimationByOrientation(ma, orientation);
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
