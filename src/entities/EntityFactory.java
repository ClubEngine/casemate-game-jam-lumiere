
package entities;

import architecture.AppContent;
import com.artemis.ComponentType;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.managers.GroupManager;
import com.artemis.managers.TagManager;
import components.AILuming;
import components.AIMonsterComponent;
import components.AIPetComponent;
import components.AbstractTextureComponent;
import components.AbstractTextureRect;
import components.AnimatedTextureRect;
import components.Collector;
import components.CollideWithMap;
import components.DamageMaker;
import components.Damageable;
import components.DebugName;
import components.Exit;
import components.Expiration;
import components.Filter;
import components.FixedTextureRect;
import components.HitBox;
import components.MultipleAnimations;
import components.MultipleTextures;
import components.Orientation;
import static components.Orientation.Direction.DOWN;
import components.Player;
import components.SpriteAnimation;
import components.TextureComponent;
import components.Transformation;
import components.Velocity;
import content.Animations;
import content.Groups;
import content.Masks;
import static content.Masks.COLOR_BLUE;
import static content.Masks.COLOR_GREEN;
import static content.Masks.COLOR_RED;
import content.Textures;
import java.util.List;
import org.jsfml.graphics.ConstTexture;
import org.jsfml.graphics.FloatRect;
import org.jsfml.graphics.IntRect;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;

/**
 *
 */
public class EntityFactory {

    public static Entity createPlayer(AppContent appContent, World world, float x, float y) {
        Entity player = world.createEntity();
        player.addComponent(new DebugName("Player"));
        player.addComponent(new Transformation(x, y));
        player.addComponent(new Velocity());
        
        player.addComponent(new TextureComponent(getTexture(appContent, "joueur1.png")));

        MultipleAnimations ma = new MultipleAnimations();
        ma.add(Animations.GO_LEFT, AnimatedTextureRect.createLinearAnimation(new IntRect(0, 32, 32, 32), 4, 500, true));
        ma.add(Animations.GO_UP, AnimatedTextureRect.createLinearAnimation(new IntRect(0, 96, 32, 32), 4, 500, true));
        ma.add(Animations.GO_RIGHT, AnimatedTextureRect.createLinearAnimation(new IntRect(0, 64, 32, 32), 4, 500, true));
        ma.add(Animations.GO_DOWN, AnimatedTextureRect.createLinearAnimation(new IntRect(0, 0, 32, 32), 4, 500, true));
        ma.setAnimation(Animations.GO_DOWN);
        player.addComponent(ma);
        player.addComponent(ma, ComponentType.getTypeFor(AbstractTextureRect.class));

        player.addComponent(new SpriteAnimation(3, 500, true));
        player.addComponent(new HitBox(new FloatRect(6, 16, 20, 16)));
        player.addComponent(new CollideWithMap());
        player.addComponent(new Player());
        player.addComponent(new Orientation(DOWN));
        player.addComponent(new Collector());
        player.addComponent(new Damageable(Masks.DAMAGE_FROM_MONSTER | Masks.DAMAGE_FROM_NATURE));
        
        

        player.addToWorld();

        world.getManager(TagManager.class).register("PLAYER", player);

        return player;
    }

    public static Entity createNoixCoco(AppContent appContent, World world, float x, float y) {
        Entity noixCoco = world.createEntity();
        noixCoco.addComponent(new DebugName("A noix de coco"));
        noixCoco.addComponent(new Transformation(x, y));
        ConstTexture tex = getTexture(appContent, "coco.png");
        noixCoco.addComponent(new TextureComponent(tex));
        Vector2i texSize = tex.getSize();
        noixCoco.addComponent(new FixedTextureRect(new IntRect(0, 0, texSize.x, texSize.y)), ComponentType.getTypeFor(AbstractTextureRect.class));
        noixCoco.addComponent(new HitBox(new FloatRect(32, 32, 16, 16)));
        noixCoco.addToWorld();
        world.getManager(GroupManager.class).add(noixCoco, Groups.COLLECTABLES);
        return noixCoco;
    }


    public static Entity createCoin(AppContent appContent, World world, float x, float y) {
        Entity coin = world.createEntity();
        coin.addComponent(new DebugName("A coin"));
        Transformation t = new Transformation(x, y);
        t.getTransformable().scale(.5f, .5f);
        coin.addComponent(t);

        coin.addComponent(new TextureComponent(getTexture(appContent, "coin.png")));

        AnimatedTextureRect animatedRect
                = AnimatedTextureRect.createSquareAnimation(new IntRect(0, 0, 64, 64), 8, 8, 1000);
        animatedRect.setLoop(true);

        coin.addComponent(animatedRect, ComponentType.getTypeFor(AbstractTextureRect.class));
        // to allow specific accesses
        coin.addComponent(animatedRect, ComponentType.getTypeFor(AnimatedTextureRect.class));

        coin.addComponent(new HitBox(new FloatRect(0, 0, 32, 32)));
        coin.addToWorld();
        world.getManager(GroupManager.class).add(coin, Groups.COLLECTABLES);
        return coin;
    }

    public static Entity createPet(AppContent appContent, World world, float x, float y) {
        Entity pet = world.createEntity();
        pet.addComponent(new DebugName("A pet"));
        pet.addComponent(new Transformation(x, y));
        pet.addComponent(new Velocity());
        
        pet.addComponent(new TextureComponent(getTexture(appContent, "pet.png")));

        MultipleAnimations ma = new MultipleAnimations();
        ma.add(Animations.GO_LEFT, AnimatedTextureRect.createLinearAnimation(new IntRect(0, 0, 24, 21), 4, 500, true));
        ma.add(Animations.GO_UP, AnimatedTextureRect.createLinearAnimation(new IntRect(0, 42, 21, 21), 4, 500, true));
        ma.add(Animations.GO_RIGHT, AnimatedTextureRect.createLinearAnimation(new IntRect(0, 53, 24, 21), 4, 500, true));
        ma.add(Animations.GO_DOWN, AnimatedTextureRect.createLinearAnimation(new IntRect(0, 64, 32, 32), 4, 500, true));
        ma.setAnimation(Animations.GO_LEFT);
        pet.addComponent(ma);
        pet.addComponent(ma, ComponentType.getTypeFor(AbstractTextureRect.class));

        pet.addComponent(new HitBox(new FloatRect(0, 5, 24, 16)));
        pet.addComponent(new AIPetComponent());
        pet.addComponent(new Orientation());

        pet.addComponent(new Damageable(Masks.DAMAGE_FROM_ALL));

        pet.addToWorld();

        return pet;
    }

    public static Entity createMonster(AppContent appContent, World world, float x, float y,List<Vector2f> path) {
        Entity monster = world.createEntity();
        monster.addComponent(new DebugName("A monster"));
        monster.addComponent(new Transformation(x, y));
        monster.addComponent(new Velocity());

        monster.addComponent(new HitBox(new FloatRect(0, 5, 24, 31)));
        monster.addComponent(new Orientation());

        monster.addComponent(new TextureComponent(getTexture(appContent, "monster.png")));

        MultipleAnimations ma = new MultipleAnimations();
        ma.add(Animations.GO_UP, AnimatedTextureRect.createLinearAnimation(new IntRect(0, 0, 24, 36), 4, 500, true));
        ma.add(Animations.GO_RIGHT, AnimatedTextureRect.createLinearAnimation(new IntRect(0, 36, 24, 36), 4, 500, true));
        ma.add(Animations.GO_DOWN, AnimatedTextureRect.createLinearAnimation(new IntRect(0, 36 * 2, 24, 36), 4, 500, true));
        ma.add(Animations.GO_LEFT, AnimatedTextureRect.createLinearAnimation(new IntRect(0, 36 * 3, 24, 36), 4, 500, true));
        ma.setAnimation(Animations.GO_LEFT);
        monster.addComponent(ma);
        monster.addComponent(ma, ComponentType.getTypeFor(AbstractTextureRect.class));

        monster.addComponent(new AIMonsterComponent(path));

        monster.addComponent(new Damageable(Masks.DAMAGE_FROM_PLAYER | Masks.DAMAGE_FROM_NATURE));
        

        monster.addToWorld();

        return monster;
    }

    private static ConstTexture getTexture(AppContent appContent, String textureName) {
        return appContent.getGraphicEngine().getTexture(textureName);
    }

    public static Entity createDamageFromPlayer(World world, Vector2f pos, Orientation o) {
        Entity damage = world.createEntity();

        damage.addComponent(new Transformation(Vector2f.add(pos, o.getVector2f(32))));
        damage.addComponent(new HitBox(new FloatRect(0, 0, 32, 32)));
        damage.addComponent(new Expiration(50));
        damage.addComponent(new DamageMaker(Masks.DAMAGE_FROM_PLAYER));
        damage.addToWorld();

        world.getManager(GroupManager.class).add(damage, Groups.DAMAGEABLES);

        return damage;
    }

    public static Entity createFire(AppContent appContent, World world, float x, float y) {
        Entity fire = world.createEntity();

        fire.addComponent(new Transformation(x, y));
        fire.addComponent(new TextureComponent(getTexture(appContent, "fire.png")));
        AnimatedTextureRect anim = AnimatedTextureRect.createLinearAnimation(new IntRect(0, 0, 96, 96), 4, 500, true);
        fire.addComponent(anim, ComponentType.getTypeFor(AbstractTextureRect.class));
        fire.addComponent(anim);
        fire.addComponent(new HitBox(new FloatRect(0, 0, 96, 96)));
        fire.addComponent(new DamageMaker(Masks.DAMAGE_FROM_NATURE));

        fire.addToWorld();

        world.getManager(GroupManager.class).add(fire, Groups.DAMAGEABLES);

        return fire;
    }

    public static Entity createFireBall(AppContent appContent, World world, Vector2f pos, Orientation orientation) {
        Entity fireBall = world.createEntity();

        Transformation t = new Transformation(Vector2f.add(pos, orientation.getVector2f(32)));
        t.getTransformable().setOrigin(32, 32);
        t.getTransformable().setRotation(orientation.getAngle() - 90f);
        //t.getTransformable().scale(.5f, .5f);
        //t.getTransformable().setOrigin(0, 0);
        fireBall.addComponent(t);
        fireBall.addComponent(new TextureComponent(getTexture(appContent, "fire_ball.png")));
        AnimatedTextureRect anim = AnimatedTextureRect.createLinearAnimation(new IntRect(0, 0, 64, 64), 5, 300, true);
        fireBall.addComponent(anim, ComponentType.getTypeFor(AbstractTextureRect.class));
        fireBall.addComponent(anim);
        fireBall.addComponent(new HitBox(new FloatRect(-15, -15, 32, 32)));
        fireBall.addComponent(new Velocity(orientation.getVector2f(100f)));
        fireBall.addComponent(new DamageMaker(Masks.DAMAGE_FROM_MONSTER));

        fireBall.addToWorld();

        world.getManager(GroupManager.class).add(fireBall, Groups.DAMAGEABLES);

        return fireBall;
    }

    public static Entity createLuming(AppContent appContent, World world, Vector2f position, int color) {
        Entity luming = world.createEntity();
        luming.addComponent(new DebugName("Luming"));
        Transformation t = new Transformation(position.x + 16, position.y + 32);
        t.getTransformable().setOrigin(16, 32);
        luming.addComponent(t);
        luming.addComponent(new Velocity());

        MultipleTextures mt = new MultipleTextures();
        mt.add(Textures.LUMING_BLUE, getTexture(appContent, "luming_blue.png"));
        mt.add(Textures.LUMING_RED, getTexture(appContent, "luming_red.png"));
        mt.add(Textures.LUMING_GREEN, getTexture(appContent, "luming_green.png"));
        mt.add(Textures.LUMING_CYAN, getTexture(appContent, "luming_cyan.png"));
        mt.add(Textures.LUMING_MAGENTA, getTexture(appContent, "luming_magenta.png"));
        mt.add(Textures.LUMING_WHITE, getTexture(appContent, "luming_white.png"));
        mt.add(Textures.LUMING_YELLOW, getTexture(appContent, "luming_yellow.png"));
        mt.setTexture(Textures.LUMING_WHITE);
        luming.addComponent(mt);
        luming.addComponent(mt, ComponentType.getTypeFor(AbstractTextureComponent.class));

        MultipleAnimations ma = new MultipleAnimations();
        ma.add(Animations.GO_LEFT, AnimatedTextureRect.createLinearAnimation(new IntRect(0, 32, 32, 32), 4, 500, true));
        ma.add(Animations.GO_UP, AnimatedTextureRect.createLinearAnimation(new IntRect(0, 96, 32, 32), 4, 500, true));
        ma.add(Animations.GO_RIGHT, AnimatedTextureRect.createLinearAnimation(new IntRect(0, 64, 32, 32), 4, 500, true));
        ma.add(Animations.GO_DOWN, AnimatedTextureRect.createLinearAnimation(new IntRect(0, 0, 32, 32), 4, 500, true));
        ma.add(Animations.FALLING, AnimatedTextureRect.createLinearAnimation(new IntRect(32, 0, 32, 32), 1, 500, false));
        ma.add(Animations.DEAD, AnimatedTextureRect.createLinearAnimation(new IntRect(32, 0, 32, 32), 1, 500, false));
        ma.add(Animations.EXIT, AnimatedTextureRect.createLinearAnimation(new IntRect(32, 0, 32, 32), 1, 500, false));
        ma.setAnimation(Animations.GO_RIGHT);
        luming.addComponent(ma);
        luming.addComponent(ma, ComponentType.getTypeFor(AbstractTextureRect.class));

        //luming.addComponent(new SpriteAnimation(3, 500, true));
        luming.addComponent(new HitBox(new FloatRect(-15, -31, 30, 30)));
        luming.addComponent(new CollideWithMap());

        luming.addComponent(new Orientation(DOWN));
        //luming.addComponent(new Gravity());
        AILuming ail = new AILuming(color);
        ail.colorChanged();
        luming.addComponent(ail);

        luming.addToWorld();

        world.getManager(GroupManager.class).add(luming, Groups.LUMINGS);

        return luming;
    }

    public static Entity createExit(AppContent appContent, World world, Vector2f position) {
        Entity exit = world.createEntity();

        exit.addComponent(new DebugName("An exit"));
        exit.addComponent(new Transformation(position));
        exit.addComponent(new TextureComponent(getTexture(appContent, "coin.png")), ComponentType.getTypeFor(AbstractTextureComponent.class));

        AnimatedTextureRect animatedRect
                = AnimatedTextureRect.createSquareAnimation(new IntRect(0, 0, 64, 64), 8, 8, 1000);
        animatedRect.setLoop(true);

        exit.addComponent(animatedRect, ComponentType.getTypeFor(AbstractTextureRect.class));
        // to allow specific accesses
        exit.addComponent(animatedRect, ComponentType.getTypeFor(AnimatedTextureRect.class));

        exit.addComponent(new HitBox(new FloatRect(13, 13, 6, 6)));
        exit.addComponent(new Exit(5));

        exit.addToWorld();

        return exit;
    }

    public static Entity createFilter(AppContent appContent, World world, Vector2f position, int color) {
        Entity filter = world.createEntity();

        filter.addComponent(new Transformation(position));

        String texPath = "";
        if (color == COLOR_RED) {
            texPath = "filtre_rouge.png";
        } else if (color == COLOR_GREEN) {
            texPath = "filtre_vert.png";
        } else if (color == COLOR_BLUE) {
            texPath = "filtre_bleu.png";
        } else if (color == (COLOR_RED | COLOR_GREEN)) {
            texPath = "filtre_jaune.png";
        } else if (color == (COLOR_RED | COLOR_BLUE)) {
            texPath = "filtre_magenta.png";
        } else if (color == (COLOR_GREEN | COLOR_BLUE)) {
            texPath = "filtre_cyan.png";
        }


        filter.addComponent(new TextureComponent(getTexture(appContent, texPath)), ComponentType.getTypeFor(AbstractTextureComponent.class));

        AnimatedTextureRect animatedRect
                = AnimatedTextureRect.createLinearAnimation(new IntRect(0, 0, 32, 32), 11, 1000, true);

        filter.addComponent(animatedRect, ComponentType.getTypeFor(AbstractTextureRect.class));
        // to allow specific accesses
        filter.addComponent(animatedRect, ComponentType.getTypeFor(AnimatedTextureRect.class));

        filter.addComponent(new HitBox(new FloatRect(13, 13, 6, 6)));
        filter.addComponent(new Filter(color));

        filter.addToWorld();

        return filter;
    }

}
