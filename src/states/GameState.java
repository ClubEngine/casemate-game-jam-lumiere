package states;

import architecture.AbstractApplicationState;
import architecture.AppStateEnum;
import architecture.ApplicationOptions;
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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.Main;
import map.Loader;
import map.Map;
import map.MapObject;
import org.jsfml.audio.Music;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.ConstFont;
import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Text;
import org.jsfml.system.Clock;
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

    private boolean mDebugGraphics = false;

    private Map myMap;

    private World world;
    private PlayerControlSystem mPlayerControlSystem;
    private RenderSpriteSystem mRenderingSystem;
    private DebugRenderingSystem mDebugRenderingSystem;
    private Entity mEntityPlayer;
    private Vector2i mCursorPosition = Vector2i.ZERO;
    private int mCursorObj;

    private int mLevelId;
    private Sprite gui;
    private int[] mObjectQuantities;
    private Text mTmpText;
    private int mLumingsCollected;
    private int mRequestedLumings;
    private Boolean mShowInterface;
    private boolean mLevelFinished;
    private Clock mClockLevelFinished;
    private String mStringToDisplay = "";
    private int mStringX;

    @Override
    public AppStateEnum getStateId() {
        return Main.MyStates.GAMESTATE;
    }


    @Override
    public void initialize() {
        gui = new Sprite(getGraphicEngine().getTexture("background.png"));
        gui.setPosition(0, 600 - 150);

        ConstFont font = getGraphicEngine().getFont("dbg-font.otf");

        mTmpText = new Text();
        mTmpText.setFont(font);
        mTmpText.setCharacterSize(24);
        mTmpText.setStyle(1);

        mClockLevelFinished = new Clock();
    }

    @Override
    public void notifyEntering() {
// ************* dbg
        //getAppContent().getOptions().set("prefix", "Level");
        //getAppContent().getOptions().set("interface", true);

        mShowInterface = getAppContent().getOptions().get("interface", true);

        levelReset();
    }

    @Override
    public void notifyExiting() {
        MusicEngine mesMusiques = getAppContent().getMusicEngine();
        Music gameMusic = mesMusiques.getMusic("Digital_Native.ogg");
        gameMusic.stop();
    }


    public void levelFinish() {
        if (!mLevelFinished) {
            mLevelId++;
            mLevelFinished = true;
            mClockLevelFinished.restart();
            System.out.println("Level finished");
        }
    }

    private void levelReset() {
        ApplicationOptions opts = getAppContent().getOptions();

        String prefix = opts.get("prefix");
        String name = "./assets/maps/" + prefix + mLevelId;

        int totalReq = 0;
        try {
            Scanner scanner = new Scanner(new File(name + ".t"));
            if (scanner.hasNextInt()) {
                totalReq = scanner.nextInt();
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GameState.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("No more levels.");
            getAppContent().goToState(Main.MyStates.GAME_OVER);
            return;
        }

        mLevelFinished = false;
        loadLevel(name + ".tmx", name + ".q", totalReq, name + ".txt");
    }

    private static final int NUM_OBJS = 12;

    private void loadLevel(String mapFilepath,
            String lvlQtFilepath,
            int totalLumingsRequested,
            String textPath) {
        /*
         New Loading system : with loader class
         */
        Loader ld = new Loader(mapFilepath, getGraphicEngine());
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
        world.setSystem(new ExitSystem(getAppContent(), this));
        world.setSystem(new FilterSystem(getAppContent()));
        world.setSystem(new AnimateAlphaSystem());
        world.setSystem(new GateSystem(getAppContent()));
        world.setSystem(new GateReversedSystem());
        world.setSystem(new CheckLevelEndSystem(this, totalLumingsRequested));

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

        addExits("exitRed", Masks.COLOR_RED);
        addExits("exitGreen", Masks.COLOR_GREEN);
        addExits("exitBlue", Masks.COLOR_BLUE);
        addExits("exitYellow", Masks.COLOR_RED | Masks.COLOR_GREEN);
        addExits("exitMagenta", Masks.COLOR_RED | Masks.COLOR_BLUE);
        addExits("exitCyan", Masks.COLOR_GREEN | Masks.COLOR_BLUE);
        addExits("exitWhite", Masks.COLOR_RED | Masks.COLOR_GREEN | Masks.COLOR_BLUE);

        addLumings("lumRed", Masks.COLOR_RED);
        addLumings("lumGreen", Masks.COLOR_GREEN);
        addLumings("lumBlue", Masks.COLOR_BLUE);
        addLumings("lumYellow", Masks.COLOR_RED | Masks.COLOR_GREEN);
        addLumings("lumMagenta", Masks.COLOR_RED | Masks.COLOR_BLUE);
        addLumings("lumCyan", Masks.COLOR_GREEN | Masks.COLOR_BLUE);
        addLumings("lumWhite", Masks.COLOR_RED | Masks.COLOR_GREEN | Masks.COLOR_BLUE);

        world.initialize();

        // Load quantities
        mObjectQuantities = new int[NUM_OBJS];

        try {
            Scanner scanner = new Scanner(new File(lvlQtFilepath));
            int i = 0;
            while (scanner.hasNextInt()) {
                mObjectQuantities[i++] = scanner.nextInt();
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GameState.class.getName()).log(Level.SEVERE, null, ex);
        }

        // load txt
        BufferedReader brTest;
        mStringToDisplay = "";
        mStringX = 800;
        try {
            brTest = new BufferedReader(new FileReader(textPath));
            mStringToDisplay = brTest.readLine();
            if (mStringToDisplay == null) {
                mStringToDisplay = "";
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GameState.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GameState.class.getName()).log(Level.SEVERE, null, ex);
        }

        // set vars
        mLumingsCollected = 0;
        mRequestedLumings = totalLumingsRequested;
    }

    @Override
    public void handleEvent(Event e) {
        if (e.type == Event.Type.KEY_PRESSED) {
            switch (e.asKeyEvent().key) {
                case ESCAPE:
                    getAppContent().exit();
                    break;
                case R: // reset
                    //mLevelId = 0;
                    levelReset();
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
        } else if (e.type == Event.Type.MOUSE_BUTTON_RELEASED
                && mShowInterface) {

            if (new IntRect(0, 600 - 150, 800, 150).contains(mCursorPosition)) {

                Vector2i mCursorForGui = Vector2i.sub(mCursorPosition, new Vector2i(0, 600 - 150));

                if (new IntRect(134, 28, 40, 40).contains(mCursorForGui)) {
                    mCursorObj = 1;
                } else if (new IntRect(193, 27, 40, 40).contains(mCursorForGui)) {
                    mCursorObj = 2;
                } else if (new IntRect(253, 27, 40, 40).contains(mCursorForGui)) {
                    mCursorObj = 3;
                } else if (new IntRect(313, 27, 40, 40).contains(mCursorForGui)) {
                    mCursorObj = 4;
                } else if (new IntRect(373, 27, 40, 40).contains(mCursorForGui)) {
                    mCursorObj = 5;
                } else if (new IntRect(434, 27, 40, 40).contains(mCursorForGui)) {
                    mCursorObj = 6;

                } else if (new IntRect(133, 83, 40, 40).contains(mCursorForGui)) {
                    mCursorObj = 7;
                } else if (new IntRect(193, 83, 40, 40).contains(mCursorForGui)) {
                    mCursorObj = 8;
                } else if (new IntRect(253, 83, 40, 40).contains(mCursorForGui)) {
                    mCursorObj = 9;
                } else if (new IntRect(313, 83, 40, 40).contains(mCursorForGui)) {
                    mCursorObj = 10;
                } else if (new IntRect(373, 83, 40, 40).contains(mCursorForGui)) {
                    mCursorObj = 11;
                } else if (new IntRect(434, 83, 40, 40).contains(mCursorForGui)) {
                    mCursorObj = 12;
                }

                if (mCursorObj != 0) {
                    if (mObjectQuantities[mCursorObj - 1] == 0) {
                        mCursorObj = 0;
                    }
                }

                System.out.println("Obj = " + mCursorObj);
            } else if (mCursorObj != 0) {

                boolean placed = false;

                if (new IntRect(0, 0, 800, 600 - 150).contains(mCursorPosition)) {

                    Vector2f pos = (myMap.getRealPosition(
                            myMap.getTilePosition(
                                    getGraphicEngine().getRealPoint(mCursorPosition), 0
                            ), 0));

                    switch (mCursorObj) {
                        case 1:
                            EntityFactory.createFilter(getAppContent(), world, pos, Masks.COLOR_RED);
                            placed = true;
                            break;
                        case 2:
                            EntityFactory.createFilter(getAppContent(), world, pos, Masks.COLOR_GREEN);
                            placed = true;
                            break;
                        case 3:
                            EntityFactory.createFilter(getAppContent(), world, pos, Masks.COLOR_BLUE);
                            placed = true;
                            break;
                        case 4:
                            EntityFactory.createFilter(getAppContent(), world, pos, Masks.COLOR_YELLOW);
                            placed = true;
                            break;
                        case 5:
                            EntityFactory.createFilter(getAppContent(), world, pos, Masks.COLOR_MAGENTA);
                            placed = true;
                            break;
                        case 6:
                            EntityFactory.createFilter(getAppContent(), world, pos, Masks.COLOR_CYAN);
                            placed = true;
                            break;

                        case 7:
                            EntityFactory.createGate(getAppContent(), world, pos, Masks.COLOR_RED);
                            placed = true;
                            break;
                        case 8:
                            EntityFactory.createGate(getAppContent(), world, pos, Masks.COLOR_GREEN);
                            placed = true;
                            break;
                        case 9:
                            EntityFactory.createGate(getAppContent(), world, pos, Masks.COLOR_BLUE);
                            placed = true;
                            break;
                        case 10:
                            EntityFactory.createGate(getAppContent(), world, pos, Masks.COLOR_YELLOW);
                            placed = true;
                            break;
                        case 11:
                            EntityFactory.createGate(getAppContent(), world, pos, Masks.COLOR_MAGENTA);
                            placed = true;
                            break;
                        case 12:
                            EntityFactory.createGate(getAppContent(), world, pos, Masks.COLOR_CYAN);
                            placed = true;
                            break;

                    }
                }

                if (placed) {
                    mObjectQuantities[mCursorObj - 1]--;
                    mCursorObj = 0;
                    getAppContent().getMusicEngine().getSound("plop.ogg").play();
                }
            }
        }
    }

    @Override
    public void update(Time time) {
        world.setDelta(time.asSeconds());
        world.process();

        if (mLevelFinished && mClockLevelFinished.getElapsedTime().asSeconds() > 2) {
            levelReset();
        }

        mStringX -= 100 * time.asSeconds();
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

        // ***
        getGraphicEngine().resetView();

        if (mCursorObj != 0) {
            if (new IntRect(0, 0, 800, 600 - 150).contains(mCursorPosition)) {
                Vector2f pos = (myMap.getRealPosition(
                        myMap.getTilePosition(
                                getGraphicEngine().getRealPoint(mCursorPosition), 0
                        ), 0));

                RectangleShape rs = new RectangleShape(new Vector2f(32, 32));
                rs.setFillColor(new Color(192, 192, 192, 192));
                rs.setPosition(pos);
                getGraphicEngine().getRenderTarget().draw(rs);
            }
        }

        target.draw(gui);

        mTmpText.setColor(Color.WHITE);
        if (mShowInterface) {
            float x = 157;
            float y = 600 - 150 + 40;
            for (int i = 0; i < NUM_OBJS; ++i, x += 60) {

                mTmpText.setString(Integer.toString(mObjectQuantities[i]));
                mTmpText.setPosition(x, y);
                target.draw(mTmpText);

                if (i == 5) {
                    x = 157 - 60;
                    y = 600 - 150 + 96;
                }
            }
        }

        mTmpText.setPosition(690, 600 - 150 + 50);
        mTmpText.setString("" + mLumingsCollected + " / " + mRequestedLumings);
        if (mLumingsCollected >= mRequestedLumings) {
            mTmpText.setColor(Color.GREEN);
        }
        target.draw(mTmpText);

        // text
        mTmpText.setColor(Color.BLACK);
        mTmpText.setString(mStringToDisplay);
        mTmpText.setPosition(mStringX, 50);
        target.draw(mTmpText);

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

    private void addExits(String exitName, int color) {
        List<MapObject> exits = myMap.getObjectsByName(exitName);
        for (MapObject exit : exits) {
            EntityFactory.createExit(getAppContent(), world, exit.getPosition(), color);
        }
    }

    private void addLumings(String lumName, int color) {
        List<MapObject> lumings = myMap.getObjectsByName(lumName);
        for (MapObject luming : lumings) {
            EntityFactory.createLuming(getAppContent(), world, luming.getPosition(), color);
        }
    }

    public void addLumingCollected() {
        mLumingsCollected++;
    }

}
