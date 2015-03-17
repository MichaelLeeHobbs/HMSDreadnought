/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mlh.managers;

import com.mlh.gamestates.*;

/**
 *
 * @author michael
 */
public class GameStateManager {

    // current game state
    private GameState gameState;
    private GameState oldGameState;
    private final GameInputProcessor gameInputProcessor;

    public static final int MENU = 0;
    public static final int PLAY = 1;
    public static final int HIGHSCORE = 2;
    public static final int GAMEOVER = 3;
    public static final int PAUSE = 4;
    public static final int LEVEL_COMPLETE = 5;
    public static final int DIFFICULTY_SELECT = 6;

    public GameStateManager() {
        gameInputProcessor = new GameInputProcessor();
        setState(MENU);
    }

    public final void setState(int state) {
        this.setState(state, null);
    }

    public void setState(int state, Object data) {
        if (gameState != null && state != PAUSE) {
            gameState.dispose();
        }
        if (state == MENU) {
            gameState = new MenuState(this, gameInputProcessor, data);
        }
        if (state == PLAY) {
            if (oldGameState != null) {
                gameState = oldGameState;
                oldGameState = null;
            } else {
                gameState = new PlayState(this, gameInputProcessor, data);
            }
        }
        if (state == HIGHSCORE) {
            gameState = new HighScoreState(this, gameInputProcessor, data);
        }
        if (state == GAMEOVER) {
            gameState = new GameOverState(this, gameInputProcessor, data);
        }
        if (state == PAUSE) {
            oldGameState = gameState;
            gameState = new PauseState(this, gameInputProcessor, data);
        }

        if (state == LEVEL_COMPLETE) {
            gameState = new LevelCompleteState(this, gameInputProcessor, data);
        }

        if (state == DIFFICULTY_SELECT) {
            gameState = new MenuSelectDifficultyState(this, gameInputProcessor, data);
        }

    }

    public void update(float dt) {
        gameState.update(dt);
    }

    public void draw() {
        gameState.draw();
    }

}
