/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mlh.models;

import java.io.Serializable;

/**
 *
 * @author michael
 */
public class ScoreData implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private final int MAX_SCORES = 10;
    private final long[] highScores;
    private final String[] names;
    private long tentativeScore;

    public long[] getHighScores() {
        return highScores.clone();
    }

    public String[] getNames() {
        return names.clone();
    }

    public long getTentativeScore() {
        return tentativeScore;
    }

    public void setTentativeScore(long tentativeScore) {
        this.tentativeScore = tentativeScore;
    }
    
    public boolean isHighScore(long score) {
        return score > highScores[MAX_SCORES - 1];
    }
    
    public void addHighScore(long newScore, String name) {
        if(isHighScore(newScore)) {
            highScores[MAX_SCORES-1] = newScore;
            names[MAX_SCORES-1] = name;
            sortHighScores();
        }
    }

    private void sortHighScores(){
        for(int i = 0; i < MAX_SCORES; i++){
            long score = highScores[i];
            String name = names[i];
            int j;
            for(j=i-1;
                    j >= 0 && highScores[j] < score;
                    j--) {
                highScores[j+1] = highScores[j];
                names[j+1] = names[j];
            }
            highScores[j+1] = score;
            names[j+1] = name;
        }
    }
    
    public ScoreData() {
        highScores = new long[MAX_SCORES];
        names = new String[MAX_SCORES];
        
    }
    
    // setup empty high scores
    public void init() {
        for(int i=0; i < MAX_SCORES; i++) {
            highScores[i] = 0;
            names[i] = "---";
        }
    }
    
    
}
