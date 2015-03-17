/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mlh.models;

/**
 *
 * @author michael
 */
public class LevelCompleteData {

    public LevelCompleteData(long score, int wpm, Level nextLevel) {
        this.score = score;
        this.wpm = wpm;
        this.nextLevel = nextLevel;
    }
    
    private long score = 0;
    private int wpm = 0;
    private Level nextLevel;

    /**
     * @return the score
     */
    public long getScore() {
        return score;
    }

    /**
     * @param score the score to set
     */
    public void setScore(long score) {
        this.score = score;
    }

    /**
     * @return the wpm
     */
    public int getWpm() {
        return wpm;
    }

    /**
     * @param wpm the wpm to set
     */
    public void setWpm(int wpm) {
        this.wpm = wpm;
    }

    /**
     * @return the nextLevel
     */
    public Level getNextLevel() {
        return nextLevel;
    }
}
