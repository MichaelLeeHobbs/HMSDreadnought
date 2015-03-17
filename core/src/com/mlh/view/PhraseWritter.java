/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mlh.view;

import com.badlogic.gdx.math.Vector2;
import com.mlh.models.Phrase;

/**
 *
 * @author michael
 */
public class PhraseWritter {
    
    public void draw(Phrase phrase, Vector2 position) {
        FontWriter.write(position, phrase.getWords()[phrase.getCurrentWord()], phrase.getTyped());
    }
    
}
