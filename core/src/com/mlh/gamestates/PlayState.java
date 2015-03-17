/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mlh.gamestates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mlh.actors.EnemyActor;
import com.mlh.actors.Player;
import com.mlh.game.HMSDreadnought;
import com.mlh.managers.*;
import com.mlh.models.DrawableSprite;
import com.mlh.models.Level;
import com.mlh.models.World;
import com.mlh.utilities.Debuger;
import com.mlh.view.FontWriter;
import com.mlh.view.PlayStateUI;

/**
 *
 * @author michael
 */
public class PlayState extends GameState {

    // Settings
    // for debuging
    private static final boolean DRAW_WPM = true;

    // world
    private World world;

    // gfx
    private SpriteBatch sb;
    private ShapeRenderer sr;
    private PlayStateUI playStateUI;

    // phrase manager
    private PhraseManager phraseManager;

    // font and life marker
    private BitmapFont font;

    // player
    private Player player;

    // bg music

    // level
    private Level gameLevel;
    
    // typed - used for healing player for typing
    private int typed = 0;

    public PlayState(GameStateManager gsm, GameInputProcessor gameInputProcessor, Object data) {
        super(gsm, gameInputProcessor, data);
    }

    @Override
    public void init() {
        // gamelevel
        gameLevel = (Level) data;

        // world
        world = new World(HMSDreadnought.WIDTH, HMSDreadnought.HEIGHT, (int) (HMSDreadnought.WIDTH * 0.5f), (int) (HMSDreadnought.HEIGHT * 0.5f));

        // managers
        phraseManager = new PhraseManager();
        gameInputProcessor.addListener(phraseManager);

        // player create
        player = new Player(world);

        // level
        gameLevel.init(this, gsm, world, player, phraseManager);

        // player init
        player.init(gameLevel.getPlayerShip(), gameLevel.getPlayerPosition(), gameLevel.getPlayerHeading(), 1);
        world.addObject(player);

        // state
        int phraseDifLevel = 0;

        // set up music
        // set font
        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(
                Gdx.files.internal("core/assets/fonts/Hyperspace Bold.ttf")
        );
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = 20;
        font = gen.generateFont(parameter);

        // gfx
        sb = new SpriteBatch();
        sr = new ShapeRenderer();
        playStateUI = new PlayStateUI(font);
        playStateUI.setDrawWpmOn(DRAW_WPM);

        // back ground
        DrawableSprite backGroundSprite = new DrawableSprite("playstate background", this, world, BackGroundManager.get(gameLevel.getBackGround()), 0);
        world.addObject(backGroundSprite);

        Debuger.debugOn = false;
        Debuger.debugLevel = 4;

        //Everest testactor = new Everest(world, player, phraseManager, 6, "QP");
        //testactor.init(1);
        //testactor.setPath(testactor.pathTopLeftToBottomRight(80, 80));
        //testactor.setType("enemy");
        //world.addObject(testactor);

        // ship template
        //SpaceShipNew tmpShip = new SpaceShipTemplateNew(this, world, 1);
        //tmpShip = new SpaceShipTemplate(this, world, 1);
        //tmpShip.setPosition(new Vector2(600, 600));
        //tmpShip.save();

    }

    @Override
    public void update(float dt) {
        handleInput();
        
        // update level
        gameLevel.update(dt);

        // update world
        world.update(dt);

        // update player
        if (player.isDead()) {
            // player dead
            Jukebox.stopAll();

            gsm.setState(GameStateManager.GAMEOVER, gameLevel.getScore());
        }

        // play background music
        // update ui
        playStateUI.setAll(gameLevel.getScore(), player.getHealth(), player.getShield(), gameLevel.getWordsPerMin(), gameLevel.getLevelWpm(), gameLevel.getSpawnedWpm(), EnemyActor.getEnemyCount());

        int typedOld = typed;
        typed = phraseManager.getTyped();
        
        // update date player typed
        player.incrementTyped(typed - typedOld);

    }

    @Override
    public void draw() {
        sb.setProjectionMatrix(HMSDreadnought.camera.combined);
        sr.setProjectionMatrix(HMSDreadnought.camera.combined);

        // draw world
        world.draw(sr, sb);

        // draw font for objects
        FontWriter.draw(sb);

        // draw ui
        playStateUI.draw(sr, sb);
    }

    @Override
    public void handleInput() {
        if (GameKeys.isPressed(GameKeys.ESCAPE)) {
            gsm.setState(GameStateManager.PAUSE);
        }
    }

    @Override
    public void dispose() {
        sb.dispose();
        sr.dispose();
        font.dispose();
    }

}
