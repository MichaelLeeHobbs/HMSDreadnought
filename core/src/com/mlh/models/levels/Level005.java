/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mlh.models.levels;

import com.badlogic.gdx.math.Vector2;
import com.mlh.actors.EnemyActor;
import com.mlh.game.HMSDreadnought;
import com.mlh.managers.GameStateManager;
import com.mlh.managers.Jukebox;
import com.mlh.models.BossPhrases;
import com.mlh.models.Level;
import com.mlh.models.LevelCompleteData;

/**
 *
 * @author michael
 */
public class Level005 extends Level {

    // settings
    private static final String BACK_GROUND = "nebula";
    private static final int[] STAGE_TIME = new int[]{5, 60, 120, 240, 480};
    private static final Vector2 PLAYER_HEADING = new Vector2(1f, 0);
    private static final Vector2 PLAYER_POSITION = new Vector2(HMSDreadnought.WIDTH / 2 - 70, 200);
    private static final String PLAYER_SHIP = "core/assets/data/ships/Frisbee2.shp";
    private static final String BACK_GROUND_MUSIC = "none";
    private static final Class NEXT_LEVEL = LevelFreePlay.class;
    private static final int WORD_LENGTH = HMSDreadnought.wordLength + 4;

    // bg music
    // level controlers
    // stages are in seconds, can not be the same as another
    //private final int[] stageTime = {5, 60, 120, 240, 480};
    //for testing: 
    private static final int PATH_OFFSET = 40;
    private boolean spawnBoss = false;
    private EnemyActor[] boss;

    public Level005() {
        this(0);
    }

    public Level005(long score) {
        super();
        stageTime = STAGE_TIME;
        playerHeading = PLAYER_HEADING;
        playerPosition = PLAYER_POSITION;
        playerShip = PLAYER_SHIP;
        backGround = BACK_GROUND;
        backGroundMusic = BACK_GROUND_MUSIC;
        nextLevel = NEXT_LEVEL;
        this.score = score;

        if (HMSDreadnought.demo) {
            stageTime = new int[]{5, 10, 15, 20, 25};
        } else {
            stageTime = STAGE_TIME;
        }
    }

    @Override
    protected void stage0() {

    }

    @Override
    protected void stage1() {
        spawnOn = true;
        if (spawnedWpm < levelWpm) {
            spawn("songbird", 1, WORD_LENGTH);
            spawn("fighter", 1, WORD_LENGTH);
            spawn("bat", 1, WORD_LENGTH);
        }
    }

    @Override
    protected void stage2() {
        if (spawnedWpm < levelWpm) {
            spawn("songbird", 2, WORD_LENGTH);
            spawn("fighter", 2, WORD_LENGTH);
            spawn("bat", 2, WORD_LENGTH);
        }
    }

    @Override
    protected void stage3() {
        if (spawnedWpm < levelWpm) {
            spawn("songbird", 3, WORD_LENGTH);
            spawn("fighter", 3, WORD_LENGTH);
            spawn("bat", 3, WORD_LENGTH);
        }
    }

    @Override
    protected void stage4() {
        if (!spawnBoss) {

            spawnBoss = true;
            boss = spawn("devilyn", 1, WORD_LENGTH);
            boss[0].setPhrase(BossPhrases.getRandomPhrase());
            spawnedChars += boss[0].getPhraseLength();
        }
        if (spawnedWpm < levelWpm && !boss[0].isDead()) {
            spawn("songbird", 5, WORD_LENGTH);
        }
        if (isBossDead(boss)) {
            stage++;
        }
    }

    @Override
    protected void stage5() {
        spawnOn = false;
        // no more spawn
        // level is done
        if (EnemyActor.getEnemyCount() == 0) {
            Jukebox.stopAll();
            gsm.setState(GameStateManager.LEVEL_COMPLETE, new LevelCompleteData(getScore(), wordsPerMin, getNextLevel(getScore())));
        }
    }

    @Override
    protected void updateStageFinal() {
        // if not done and out of enemys
        if (EnemyActor.getEnemyCount() == 0 && spawnOn) {
            spawn("fighter", 1, WORD_LENGTH);
            levelWpm++;
        }
    }
}
