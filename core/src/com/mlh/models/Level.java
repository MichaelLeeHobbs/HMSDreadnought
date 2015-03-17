/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mlh.models;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mlh.actors.*;
import com.mlh.game.HMSDreadnought;
import com.mlh.gamestates.PlayState;
import com.mlh.managers.GameStateManager;
import com.mlh.managers.PhraseManager;

import java.util.logging.Logger;

/**
 *
 * @author michael
 */
public abstract class Level {

    private static final int PATH_OFFSET = 40;

    protected PlayState playState;
    protected GameStateManager gsm;
    protected World world;
    protected Player player;
    protected PhraseManager phraseManager;
    protected long score;
    protected int pathOffset;
    protected String phraseLetters;

    protected Class<Level> nextLevel;
    // back ground
    // nebula by default
    protected String backGround = "nebula";

    // back ground
    protected String backGroundMusic = "none";

    // level controlers
    protected int[] stageTime;
    protected boolean[] stageUpdate;
    protected int stage;
    protected boolean spawnOn;

    // level time needs to start at 1 to avoid div by 0 or a lot of extra code
    protected float levelTime = 1;
    protected int levelWpm = HMSDreadnought.wpm;
    protected int spawnedWpm = 0;
    protected int spawnedChars = 0;

    // this is the acutal typed wpm
    protected int wordsPerMin = 0;
    protected int wordsPerMinOld = 0;

    // player
    protected Vector2 playerHeading;
    protected Vector2 playerPosition;
    protected String playerShip;

    // play state
    protected int level;

    // spawn limiter
    private static final float MAX_SPAWN_RATE = 1;  // per second
    private float totalSpawned = 0;

    // bg music
    public Level() {
        this(0);
    }

    public Level(long score) {
        stage = 0;
        spawnOn = false;
        this.score = score;
        pathOffset = PATH_OFFSET;

    }

    public void init(PlayState playState, GameStateManager gsm, World world, Player player, PhraseManager phraseManager) {
        this.playState = playState;
        this.gsm = gsm;
        this.world = world;
        this.player = player;
        this.phraseManager = phraseManager;
        stageUpdate = new boolean[stageTime.length + 1];
        level = 0;
        phraseLevel(level);
    }

    public void update(float dt) {
        // update level time
        levelTime += dt;

        // Time Base Actions
        for (int i = stage; i < stageTime.length; i++) {
            if (levelTime > stageTime[i]) {
                stage = i + 1;
            }
        }

        //System.out.println("stage = " + stage);
        updateStage();

        // update wordsPerMinute
        updateWpm();
    }

    public void updateStage() {
        switch (stage) {
            case 0:
                // this is the default case
                // use this for any action to take before starting spawn of enemy
                stage0();
                break;

            case 1:
                stage1();
                break;
            case 2:
                stage2();
                if (!stageUpdate[stage]) {
                    levelWpm += 1;
                    stageUpdate[stage] = true;
                }
                break;

            case 3:
                stage3();
                if (!stageUpdate[stage]) {
                    levelWpm += 1;
                    stageUpdate[stage] = true;
                }
                break;

            case 4:
                stage4();
                if (!stageUpdate[stage]) {
                    levelWpm += 1;
                    stageUpdate[stage] = true;
                }
                break;
            case 5:
                stage5();
                break;
        }
        updateStageFinal();
        updateSpawnedWpm();
    }

    protected abstract void stage0();

    protected abstract void stage1();

    protected abstract void stage2();

    protected abstract void stage3();

    protected abstract void stage4();

    protected abstract void stage5();

    protected abstract void updateStageFinal();

    protected EnemyActor[] spawn(String actor, int count, int wordLength) {
        // spawn controls to prevent flooding
        float currentSpawnRate = totalSpawned / levelTime;
        // if not equal to a boss, we must always spawn bosses
        if (!(actor.equals("puma") || actor.equals("puma pair") || actor.equals("knife_edge_pair") || actor.equals("devilyn"))) {
            // if spawn rate to dont spawn
            if (currentSpawnRate > MAX_SPAWN_RATE) {
                return null;
            }
        }
        totalSpawned += count;

        EnemyActor[] enemyActor = new EnemyActor[count];

        if (actor.equals("fighter")) {

            int xOffset = MathUtils.random(pathOffset);
            int yOffset = MathUtils.random(pathOffset);

            for (int i = 0; i < count; i++) {
                if (MathUtils.randomBoolean()) {
                    xOffset += pathOffset * count;
                } else {
                    yOffset += pathOffset * count;
                }
                enemyActor[i] = new Fighter(world, player, phraseManager, wordLength, phraseLetters);
                enemyActor[i].init(1);
                if (i == 0) {
                    enemyActor[i].setPath(enemyActor[i].getRandomPath(xOffset, yOffset));
                } else {
                    enemyActor[i].setPath(enemyActor[i].getLastPath(xOffset, yOffset));
                }
                enemyActor[i].setType("enemy");
                world.addObject(enemyActor[i]);
                spawnedChars += enemyActor[i].getPhraseLength();
            }
        }

        if (actor.equals("songbird")) {

            int xOffset = MathUtils.random(pathOffset);
            int yOffset = MathUtils.random(pathOffset);

            for (int i = 0; i < count; i++) {
                if (MathUtils.randomBoolean()) {
                    xOffset += pathOffset * count;
                } else {
                    yOffset += pathOffset * count;
                }
                enemyActor[i] = new SongBird(world, player, phraseManager, wordLength, phraseLetters);
                enemyActor[i].init(1);
                if (i == 0) {
                    enemyActor[i].setPath(enemyActor[i].getRandomPath(xOffset, yOffset));
                } else {
                    enemyActor[i].setPath(enemyActor[i].getLastPath(xOffset, yOffset));
                }
                enemyActor[i].setType("enemy");
                world.addObject(enemyActor[i]);
                spawnedChars += enemyActor[i].getPhraseLength();
            }
        }

        if (actor.equals("bat")) {

            int xOffset = MathUtils.random(pathOffset);
            int yOffset = MathUtils.random(pathOffset);

            for (int i = 0; i < count; i++) {
                if (MathUtils.randomBoolean()) {
                    xOffset += pathOffset * count;
                } else {
                    yOffset += pathOffset * count;
                }
                enemyActor[i] = new Bat(world, player, phraseManager, wordLength, phraseLetters);
                enemyActor[i].init(1);
                if (i == 0) {
                    enemyActor[i].setPath(enemyActor[i].getRandomPath(xOffset, yOffset));
                } else {
                    enemyActor[i].setPath(enemyActor[i].getLastPath(xOffset, yOffset));
                }
                enemyActor[i].setType("enemy");
                world.addObject(enemyActor[i]);
                spawnedChars += enemyActor[i].getPhraseLength();
            }
        }

        if (actor.equals("everest")) {

            int xOffset = MathUtils.random(pathOffset);
            int yOffset = MathUtils.random(pathOffset);

            for (int i = 0; i < count; i++) {
                if (MathUtils.randomBoolean()) {
                    xOffset += pathOffset * count;
                } else {
                    yOffset += pathOffset * count;
                }
                enemyActor[i] = new Everest(world, player, phraseManager, wordLength, phraseLetters);
                enemyActor[i].init(1);
                if (i == 0) {
                    enemyActor[i].setPath(enemyActor[i].getRandomPath(xOffset, yOffset));
                } else {
                    enemyActor[i].setPath(enemyActor[i].getLastPath(xOffset, yOffset));
                }
                enemyActor[i].setType("enemy");
                world.addObject(enemyActor[i]);
                spawnedChars += enemyActor[i].getPhraseLength();
            }
        }

        if (actor.equals("puma")) {
            enemyActor[0] = new Puma(world, player, phraseManager, wordLength, phraseLetters);
            enemyActor[0].init(1);
            enemyActor[0].setType("enemy");
            enemyActor[0].setStartPos(new Vector2(0f - world.getBoundarySpaceWidth(), (float) world.getVisiableSpaceHeight() - 200f));
            enemyActor[0].setTargetPos(new Vector2((float) world.getVisiableSpaceWidth() / 2f, (float) world.getVisiableSpaceHeight() - 200f));
            world.addObject(enemyActor[0]);
        }

        if (actor.equals("devilyn")) {
            enemyActor[0] = new Devilyn(world, player, phraseManager, wordLength, phraseLetters);
            enemyActor[0].init(1);
            enemyActor[0].setType("enemy");
            enemyActor[0].setStartPos(new Vector2(world.getVisiableSpaceWidth() / 2 - 100, (float) world.getVisiableSpaceHeight() + world.getBoundarySpaceHeight()));
            enemyActor[0].setTargetPos(new Vector2((float) world.getVisiableSpaceWidth() / 2f - 100, (float) world.getVisiableSpaceHeight() - 300f));
            world.addObject(enemyActor[0]);
        }

        if (actor.equals("puma pair")) {
            enemyActor = new EnemyActor[2];
            //one
            enemyActor[0] = new Puma(world, player, phraseManager, wordLength, phraseLetters);
            enemyActor[0].init(1);
            enemyActor[0].setType("enemy");
            enemyActor[0].setStartPos(new Vector2((float) world.getVisiableSpaceWidth() * 0.25f, 0f - world.getBoundarySpaceHeight()));
            enemyActor[0].setTargetPos(new Vector2((float) world.getVisiableSpaceWidth() * 0.25f, (float) world.getVisiableSpaceHeight() * 0.2f));
            world.addObject(enemyActor[0]);
            //two
            enemyActor[1] = new Puma(world, player, phraseManager, wordLength, phraseLetters);
            enemyActor[1].init(1);
            enemyActor[1].setType("enemy");
            enemyActor[1].setStartPos(new Vector2((float) world.getVisiableSpaceWidth() * 0.75f, 0f - world.getBoundarySpaceHeight()));
            enemyActor[1].setTargetPos(new Vector2((float) world.getVisiableSpaceWidth() * 0.75f, (float) world.getVisiableSpaceHeight() * 0.2f));
            world.addObject(enemyActor[1]);

            // we spawned two thus add one more
            totalSpawned++;
        }

        if (actor.equals("knife_edge_pair")) {
            enemyActor = new EnemyActor[2];
            // one
            enemyActor[0] = new KnifeEdge(world, player, phraseManager, wordLength, phraseLetters);
            enemyActor[0].init(1);
            enemyActor[0].setType("enemy");
            enemyActor[0].setStartPos(new Vector2((float) world.getVisiableSpaceWidth() * 0.25f, 0f - world.getBoundarySpaceHeight()));
            enemyActor[0].setTargetPos(new Vector2((float) world.getVisiableSpaceWidth() * 0.25f, (float) world.getVisiableSpaceHeight() * 0.2f));
            world.addObject(enemyActor[0]);

            // two
            enemyActor[1] = new KnifeEdge(world, player, phraseManager, wordLength, phraseLetters);
            enemyActor[1].init(1);
            enemyActor[1].setType("enemy");
            enemyActor[1].setStartPos(new Vector2((float) world.getVisiableSpaceWidth() * 0.75f, 0f - world.getBoundarySpaceHeight()));
            enemyActor[1].setTargetPos(new Vector2((float) world.getVisiableSpaceWidth() * 0.75f, (float) world.getVisiableSpaceHeight() * 0.2f));
            world.addObject(enemyActor[1]);

            // we spawned two thus add one more
            totalSpawned++;
        }

        return enemyActor;
    }

    @SuppressWarnings("fallthrough")
    private void phraseLevel(int level) {
        StringBuilder phraseLetters = new StringBuilder();
        switch (level) {
            case 10:
                phraseLetters.append("BN");
            case 9:
                phraseLetters.append("VM");
            case 8:
                phraseLetters.append("C,");
            case 7:
                phraseLetters.append("X.");
            case 6:
                phraseLetters.append("Z/");
            case 5:
                phraseLetters.append("TY");
            case 4:
                phraseLetters.append("RU");
            case 3:
                phraseLetters.append("EI");
            case 2:
                phraseLetters.append("WO");
            case 1:
                phraseLetters.append("QP");
            default:
                phraseLetters.append("");
        }
        this.phraseLetters = phraseLetters.toString();
    }

    public void updateSpawnedWpm() {
        spawnedWpm = ((spawnedChars / HMSDreadnought.wordLength) * 60) / (int) levelTime;
    }

    private void updateWpm() {
        wordsPerMinOld = wordsPerMin;
        wordsPerMin = ((phraseManager.getTyped() / HMSDreadnought.wordLength) * 60) / (int) levelTime;
    }

    public Vector2 getPlayerHeading() {
        return playerHeading;
    }

    public Vector2 getPlayerPosition() {
        return playerPosition;
    }

    public String getPlayerShip() {
        return playerShip;
    }

    public String getBackGround() {
        return backGround;
    }

    public int getLevelWpm() {
        return levelWpm;
    }

    public int getSpawnedWpm() {
        return spawnedWpm;
    }

    public int getWordsPerMin() {
        return wordsPerMin;
    }

    public Level getNextLevel(long score) {
        try {
            Level newLevel = nextLevel.newInstance();
            newLevel.setScore(score);
            return newLevel;

        } catch (InstantiationException ex) {
            Logger.getLogger(Level.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Level.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        return null;
    }

    public long getScore() {
        return score + player.getScore();
    }

    public long setScore(long score) {
        return this.score = score;
    }

    public int getWordsPerMinOld() {
        return wordsPerMinOld;
    }

    protected boolean isBossDead(Actor[] boss) {
        boolean bossDead = true;
        for (Actor b : boss) {
            if (!b.isDead()) {
                bossDead = false;
            }
        }
        return bossDead;
    }

}
