package states;

import architecture.AbstractApplicationState;
import architecture.AppStateEnum;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.managers.GroupManager;
import com.artemis.managers.TagManager;
import com.artemis.managers.TeamManager;
import components.Orientation;
import components.Transformation;
import content.Masks;
import entities.EntityFactory;
import graphics.Camera;
import java.util.List;
import main.Main;
import map.Loader;
import map.Map;
import map.MapObject;
import org.jsfml.audio.Music;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Time;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;
import org.jsfml.window.event.Event;
import sounds.MusicEngine;
import systems.AILumingSystem;
import systems.AIMonsterSystem;
import systems.AIPetSystem;
import systems.AnimateAlphaSystem;
import systems.AnimateTextRectSystem;
import systems.CheckLevelEndSystem;
import systems.CollectSystem;
import systems.DamageSystem;
import systems.DebugRenderingSystem;
import systems.ExitSystem;
import systems.ExpirationSystem;
import systems.FilterSystem;
import systems.GateReversedSystem;
import systems.GateSystem;
import systems.MovemementCollideMapSystem;
import systems.MovemementSystem;
import systems.MultipleAnimationSystem;
import systems.PlayerControlSystem;
import systems.RenderSpriteSystem;

/**
 *
 */
public class GameState extends AbstractApplicationState {

    private boolean mDebugGraphics = true;

    private Music gameMusic;
    private Map myMap;

    private World world;
    private PlayerControlSystem mPlayerControlSystem;
    private RenderSpriteSystem mRenderingSystem;
    private DebugRenderingSystem mDebugRenderingSystem;
    private Entity mEntityPlayer;
    private Vector2i mCursorPosition = Vector2i.ZERO;
    private int mCursorState;
    private int mCursorObj;

    private int levelId;

    @Override
    public AppStateEnum getStateId() {
        return Main.MyStates.GAMESTATE;
    }

    @Override
    public void notifyEntering() {
        MusicEngine mesMusiques = getAppContent().getMusicEngine();
        gameMusic = mesMusiques.getMusic("happy.ogg");
        //gameMusic.play();
    }

    @Override
    public void notifyExiting() {
        gameMusic.pause();
    }

    @Override
    public void initialize() {
        //getAppContent().getOptions().setIfUnset("maps.filepath", "./assets/maps/lum01.tmx");

        /*
         New Loading system : with loader class
         */
        Loader ld = new Loader(getAppContent().getOptions().get("maps.filepath"), getGraphicEngine());
        myMap = ld.getMap();

        /*
         World entities creation
         */
        world = new World();
        world.setManager(new GroupManager());
        world.setManager(new TagManager());
        world.setManager(new TeamManager());

        world.setSystem(new ExpirationSystem());
        world.setSystem(mPlayerControlSystem = new PlayerControlSystem());
        world.setSystem(new MovemementSystem());
        world.setSystem(new MovemementCollideMapSystem(getAppContent(), myMap));
        world.setSystem(mDebugRenderingSystem = new DebugRenderingSystem(getGraphicEngine()), true);
        world.setSystem(new CollectSystem(getAppContent()));
        world.setSystem(new AIPetSystem(myMap));
        world.setSystem(new AIMonsterSystem(myMap, getAppContent()));
        world.setSystem(mRenderingSystem = new RenderSpriteSystem(getGraphicEngine()), true);
        world.setSystem(new AnimateTextRectSystem());
        world.setSystem(new MultipleAnimationSystem());
        world.setSystem(new DamageSystem());
        world.setSystem(new AILumingSystem(myMap));
        world.setSystem(new ExitSystem(getAppContent()));
        world.setSystem(new FilterSystem(getAppContent()));
        world.setSystem(new AnimateAlphaSystem());
        world.setSystem(new GateSystem(getAppContent()));
        world.setSystem(new GateReversedSystem());
        world.setSystem(new CheckLevelEndSystem(this));

        addFilters("filterRed", Masks.COLOR_RED);
        addFilters("filterGreen", Masks.COLOR_GREEN);
        addFilters("filterBlue", Masks.COLOR_BLUE);
        addFilters("filterYellow", Masks.COLOR_RED | Masks.COLOR_GREEN);
        addFilters("filterMagenta", Masks.COLOR_RED | Masks.COLOR_BLUE);
        addFilters("filterCyan", Masks.COLOR_GREEN | Masks.COLOR_BLUE);

        addGates("gateRed", Masks.COLOR_RED);
        addGates("gateGreen", Masks.COLOR_GREEN);
        addGates("gateBlue", Masks.COLOR_BLUE);
        addGates("gateYellow", Masks.COLOR_RED | Masks.COLOR_GREEN);
        addGates("gateMagenta", Masks.COLOR_RED | Masks.COLOR_BLUE);
        addGates("gateCyan", Masks.COLOR_GREEN | Masks.COLOR_BLUE);

        addExit("exitRed", Masks.COLOR_RED);
        addExit("exitGreen", Masks.COLOR_GREEN);
        addExit("exitBlue", Masks.COLOR_BLUE);
        addExit("exitYellow", Masks.COLOR_RED | Masks.COLOR_GREEN);
        addExit("exitMagenta", Masks.COLOR_RED | Masks.COLOR_BLUE);
        addExit("exitCyan", Masks.COLOR_GREEN | Masks.COLOR_BLUE);
        addExit("exitCyan", Masks.COLOR_RED | Masks.COLOR_GREEN | Masks.COLOR_BLUE);

        EntityFactory.createLuming(getAppContent(), world,
                myMap.getSpawnPoint(),
                Masks.COLOR_RED | Masks.COLOR_GREEN | Masks.COLOR_BLUE);

        world.initialize();
    }

    @Override
    public void handleEvent(Event e) {
        if (e.type == Event.Type.KEY_PRESSED) {
            switch (e.asKeyEvent().key) {
                case ESCAPE:
                    getAppContent().exit();
                    break;
                case R: // reset
                    initialize();
                    break;
                case D: // toggle graphic debug
                    mDebugGraphics = !mDebugGraphics;
                    break;
                case UP:
                    mPlayerControlSystem.goUp();
                    break;
                case SPACE:
                    break;
                case F:
                    Vector2f pos = mEntityPlayer.getComponent(Transformation.class).getPosition();
                    Orientation or = mEntityPlayer.getComponent(Orientation.class);
                    EntityFactory.createFireBall(getAppContent(), world, pos, or);
            }
        } else if (e.type == Event.Type.KEY_RELEASED) {
            switch (e.asKeyEvent().key) {
                case UP:
                    break;
            }
        } else if (e.type == Event.Type.MOUSE_MOVED) {
            mCursorPosition = e.asMouseEvent().position;
        } else if (e.type == Event.Type.MOUSE_BUTTON_RELEASED) {

            if (new IntRect(0, 600 - 150, 800, 150).contains(mCursorPosition)) {
                if (new IntRect(0, 600 - 64, 64, 64).contains(mCursorPosition)) {
                    mCursorObj = 1;
                } else if (new IntRect(0, 0, 64, 64).contains(mCursorPosition)) {
                    mCursorObj = 2;
                } else if (new IntRect(0, 0, 64, 64).contains(mCursorPosition)) {
                    mCursorObj = 3;
                } else if (new IntRect(0, 0, 64, 64).contains(mCursorPosition)) {
                    mCursorObj = 4;
                } else if (new IntRect(0, 0, 64, 64).contains(mCursorPosition)) {
                    mCursorObj = 5;
                } else if (new IntRect(0, 0, 64, 64).contains(mCursorPosition)) {
                    mCursorObj = 6;
                } else if (new IntRect(0, 0, 64, 64).contains(mCursorPosition)) {
                    mCursorObj = 7;
                } else if (new IntRect(0, 0, 64, 64).contains(mCursorPosition)) {
                    mCursorObj = 8;
                } else if (new IntRect(0, 0, 64, 64).contains(mCursorPosition)) {
                    mCursorObj = 9;
                } else if (new IntRect(0, 0, 64, 64).contains(mCursorPosition)) {
                    mCursorObj = 10;
                } else if (new IntRect(0, 0, 64, 64).contains(mCursorPosition)) {
                    mCursorObj = 10;
                } else if (new IntRect(0, 0, 64, 64).contains(mCursorPosition)) {
                    mCursorObj = 12;
                }
            } else if (mCursorObj != 0) {

                Vector2f pos = (myMap.getRealPosition(
                        myMap.getTilePosition(
                                getGraphicEngine().getRealPoint(mCursorPosition), 1
                        ), 1));

                switch (mCursorObj) {
                    case 1:
                        EntityFactory.createFilter(getAppContent(), world, pos, Masks.COLOR_RED);
                        break;
                    case 2:
                        EntityFactory.createFilter(getAppContent(), world, pos, Masks.COLOR_GREEN);
                        break;
                    case 3:
                        EntityFactory.createFilter(getAppContent(), world, pos, Masks.COLOR_BLUE);
                        break;
                    case 4:
                        EntityFactory.createFilter(getAppContent(), world, pos, Masks.COLOR_YELLOW);
                        break;
                    case 5:
                        EntityFactory.createFilter(getAppContent(), world, pos, Masks.COLOR_MAGENTA);
                        break;
                    case 6:
                        EntityFactory.createFilter(getAppContent(), world, pos, Masks.COLOR_CYAN);
                        break;

                    case 7:
                        EntityFactory.createGate(getAppContent(), world, pos, Masks.COLOR_RED);
                        break;
                    case 8:
                        EntityFactory.createGate(getAppContent(), world, pos, Masks.COLOR_GREEN);
                        break;
                    case 9:
                        EntityFactory.createGate(getAppContent(), world, pos, Masks.COLOR_BLUE);
                        break;
                    case 10:
                        EntityFactory.createGate(getAppContent(), world, pos, Masks.COLOR_YELLOW);
                        break;
                    case 11:
                        EntityFactory.createGate(getAppContent(), world, pos, Masks.COLOR_MAGENTA);
                        break;
                    case 12:
                        EntityFactory.createGate(getAppContent(), world, pos, Masks.COLOR_CYAN);
                        break;

                }
            }
        }
    }

    @Override
    public void update(Time time) {
        world.setDelta(time.asSeconds());
        world.process();
    }

    @Override
    public void render() {
        final RenderTarget target = getGraphicEngine().getRenderTarget();

        target.clear(new Color(64, 64, 64));

        Camera cam = getGraphicEngine().getCamera();
        cam.setTarget(new Vector2f(400, 300));

        // Drawing map
        myMap.render(getGraphicEngine(), cam.getTopLeft(), (int) (cam.getView().getSize().x * 1.5), (int) (cam.getView().getSize().y * 1.5));
        mRenderingSystem.process();
        myMap.renderFg(getGraphicEngine(), cam.getTopLeft(), (int) (cam.getView().getSize().x * 1.5), (int) (cam.getView().getSize().y * 1.5));

        if (mDebugGraphics) {
            mDebugRenderingSystem.process();
        }

        getGraphicEngine().resetView();

        if (mCursorObj != 0) {
            Vector2f pos = (myMap.getRealPosition(
                    myMap.getTilePosition(
                            getGraphicEngine().getRealPoint(mCursorPosition), 1
                    ), 1));

            RectangleShape rs = new RectangleShape(new Vector2f(32, 32));
            rs.setFillColor(new Color(192, 192, 192, 128));
            rs.setPosition(pos);
            getGraphicEngine().getRenderTarget().draw(rs);
        }

    }

    private void addFilters(String filterName, int color) {
        List<MapObject> filters = myMap.getObjectsByName(filterName);
        for (MapObject filter : filters) {
            EntityFactory.createFilter(getAppContent(), world, filter.getPosition(), color);
        }
    }

    private void addGates(String gateName, int color) {
        List<MapObject> gates = myMap.getObjectsByName(gateName);
        for (MapObject gate : gates) {
            EntityFactory.createGate(getAppContent(), world, gate.getPosition(), color);
        }
    }

    private void addExit(String exitName, int color) {
        List<MapObject> exits = myMap.getObjectsByName(exitName);
        for (MapObject exit : exits) {
            EntityFactory.createExit(getAppContent(), world, exit.getPosition(), color);
        }
    }

    public void levelFinish() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
