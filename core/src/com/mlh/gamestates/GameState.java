/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mlh.gamestates;

import com.mlh.managers.GameInputProcessor;
import com.mlh.managers.GameStateManager;

/**
 *
 * @author michael
 */
public abstract class GameState {
    protected GameStateManager gsm;
    protected GameInputProcessor gameInputProcessor;
    protected Object data;
    
    @SuppressWarnings("OverridableMethodCallInConstructor")
    protected GameState(GameStateManager gsm, GameInputProcessor gameInputProcessor, Object data) {
        this.gsm = gsm;
        this.gameInputProcessor = gameInputProcessor;
        this.data = data;
        init();
    }
    
    public abstract void init();
    public abstract void update(float dt);
    public abstract void draw();
    public abstract void handleInput();
    public abstract void dispose();
}
