/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mlh.models.levels;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mlh.actors.EnemyActor;
import com.mlh.game.HMSDreadnought;
import com.mlh.models.BossPhrases;
import com.mlh.models.Level;

/**
 *
 * @author michael
 */
public class LevelFreePlay extends Level {

    // settings
    private static final String BACK_GROUND = "nebula";
    private static final int[] STAGE_TIME = new int[]{5, 60, 120, 240, 480};
    private static final Vector2 PLAYER_HEADING = new Vector2(1f, 0);
    private static final Vector2 PLAYER_POSITION = new Vector2(HMSDreadnought.WIDTH / 2 - 70, 200);
    private static final String PLAYER_SHIP = "core/assets/data/ships/Frisbee2.shp";
    private static final String BACK_GROUND_MUSIC = "none";
    private static final Class NEXT_LEVEL = LevelFreePlay.class;
    private static final int WORD_LENGTH = HMSDreadnought.wordLength;

    // static
    private static final int FIGHTER_CHANCE = 70;
    private static final int BAT_CHANCE = 50;
    private static final int SONGBIRD_CHANCE = 30;
    private static final int EVEREST_CHANCE = 10;

    private static final int PUMA_CHANCE = 50;
    private static final int DEVILYN_CHANCE = 50;

    private static final int PUMA_PAIR_CHANCE = 25;
    private static final int KNIFE_EDGE_PAIR_CHANCE = 25;

    // bg music
    // level controlers
    // stages are in seconds, can not be the same as another
    //private final int[] stageTime = {5, 60, 120, 240, 480};
    //for testing: 
    private static final int PATH_OFFSET = 40;
    private boolean spawnBoss = false;
    private EnemyActor[] boss;

    public LevelFreePlay() {
        this(0);
    }

    public LevelFreePlay(long score) {
        super();
        stageTime = STAGE_TIME;
        playerHeading = PLAYER_HEADING;
        playerPosition = PLAYER_POSITION;
        playerShip = PLAYER_SHIP;
        backGround = BACK_GROUND;
        backGroundMusic = BACK_GROUND_MUSIC;
        nextLevel = NEXT_LEVEL;
        this.score = score;
        this.pathOffset = PATH_OFFSET;

        if (HMSDreadnought.demo) {
            stageTime = new int[]{5, 10, 15, 20, 25};
        } else {
            stageTime = STAGE_TIME;
        }
    }

    @Override
    protected void stage0() {

    }
    /*
     private static final int FIGHTER_CHANCE = 70;
     private static final int BAT_CHANCE = 50;
     private static final int SONGBIRD_CHANCE = 30;
     private static final int EVEREST_CHANCE = 10;

     private static final int PUMA_CHANCE = 50;
     private static final int DEVILYN_CHANCE = 50;

     private static final int PUMA_PAIR_CHANCE = 25;
     private static final int KNIFE_EDGE_PAIR_CHANCE = 25;
     */

    @Override
    protected void stage1() {
        spawnOn = true;
        if (spawnedWpm < levelWpm) {
            if (MathUtils.random(100) < FIGHTER_CHANCE) {
                spawn("fighter", MathUtils.random(1, 5), WORD_LENGTH);
            }
            if (MathUtils.random(100) < BAT_CHANCE) {
                spawn("bat", MathUtils.random(1, 2), WORD_LENGTH);
            }
        }

    }

    @Override
    protected void stage2() {
        if (spawnedWpm < levelWpm) {
            if (MathUtils.random(100) < FIGHTER_CHANCE) {
                spawn("fighter", MathUtils.random(1, 5), WORD_LENGTH);
            }
            if (MathUtils.random(100) < BAT_CHANCE) {
                spawn("bat", MathUtils.random(1, 2), WORD_LENGTH);
            }
            if (MathUtils.random(100) < SONGBIRD_CHANCE) {
                spawn("songbird", MathUtils.random(1, 2), WORD_LENGTH);
            }
        }
    }

    @Override
    protected void stage3() {
        if (spawnedWpm < levelWpm) {
            if (MathUtils.random(100) < FIGHTER_CHANCE) {
                spawn("fighter", MathUtils.random(1, 10), WORD_LENGTH);
            }
            if (MathUtils.random(100) < BAT_CHANCE) {
                spawn("bat", MathUtils.random(1, 5), WORD_LENGTH);
            }
            if (MathUtils.random(100) < SONGBIRD_CHANCE) {
                spawn("songbird", MathUtils.random(1, 2), WORD_LENGTH);
            }
            if (MathUtils.random(100) < EVEREST_CHANCE) {
                spawn("everest", MathUtils.random(1, 2), WORD_LENGTH);
            }
        }
    }

    @Override
    protected void stage4() {
        if (!spawnBoss) {

            spawnBoss = true;
            boss = null;
            if (MathUtils.random(100) < PUMA_CHANCE) {
                boss = spawn("puma", 1, WORD_LENGTH);
                boss[0].setPhrase(BossPhrases.getRandomPhrase());
                spawnedChars += boss[0].getPhraseLength();
            } else {
                boss = spawn("devilyn", 1, WORD_LENGTH);
                boss[0].setPhrase(BossPhrases.getRandomPhrase());
                spawnedChars += boss[0].getPhraseLength();
            }

            if (MathUtils.random(100) < PUMA_PAIR_CHANCE) {
                boss = spawn("puma pair", 1, WORD_LENGTH);
                boss[0].setPhrase(BossPhrases.getRandomPhrase());
                spawnedChars += boss[0].getPhraseLength();
                boss[1].setPhrase(BossPhrases.getRandomPhrase());
                spawnedChars += boss[1].getPhraseLength();
            } else if (MathUtils.random(100) < KNIFE_EDGE_PAIR_CHANCE) {
                boss = spawn("knife_edge_pair", 3, WORD_LENGTH);
                boss[0].setPhrase(BossPhrases.getRandomPhrase());
                spawnedChars += boss[0].getPhraseLength();
                boss[1].setPhrase(BossPhrases.getRandomPhrase());
                spawnedChars += boss[1].getPhraseLength();
            }

        }
        if (spawnedWpm < levelWpm) {
            if (MathUtils.random(100) < FIGHTER_CHANCE) {
                spawn("fighter", MathUtils.random(1, 10), WORD_LENGTH);
            }
            if (MathUtils.random(100) < BAT_CHANCE) {
                spawn("bat", MathUtils.random(1, 5), WORD_LENGTH);
            }
            if (MathUtils.random(100) < SONGBIRD_CHANCE) {
                spawn("songbird", MathUtils.random(1, 2), WORD_LENGTH);
            }
            if (MathUtils.random(100) < EVEREST_CHANCE) {
                spawn("everest", MathUtils.random(1, 2), WORD_LENGTH);
            }
        }
        if (isBossDead(boss)) {
            stage++;
        }
    }

    @Override
    protected void stage5() {
        if (!stageUpdate[stage]) {
            levelWpm += 2;
            stageUpdate[stage] = true;
        }
        stage = 0;
        levelTime = 1;
        spawnBoss = false;
        for (int i = 0; i < stageUpdate.length; i++) {
            stageUpdate[i] = false;
        }
    }

    @Override
    protected void updateStageFinal() {
        // if not done and out of enemys
        if (EnemyActor.getEnemyCount() == 0 && spawnOn) {
            if (MathUtils.random(100) > FIGHTER_CHANCE) {
                spawn("fighter", MathUtils.random(1, 5), WORD_LENGTH);
            }
            if (MathUtils.random(100) > BAT_CHANCE) {
                spawn("bat", MathUtils.random(1, 2), WORD_LENGTH);
            }
            if (MathUtils.random(100) > SONGBIRD_CHANCE) {
                spawn("songbird", MathUtils.random(1, 2), WORD_LENGTH);
            }
            if (MathUtils.random(100) > EVEREST_CHANCE) {
                spawn("everest", MathUtils.random(1, 1), WORD_LENGTH);
            }
            levelWpm++;
        }
    }

}
