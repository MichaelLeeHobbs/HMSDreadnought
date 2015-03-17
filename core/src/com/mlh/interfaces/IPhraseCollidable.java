/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mlh.interfaces;

import com.mlh.models.Phrase;

/**
 *
 * @author michael
 */
public interface IPhraseCollidable {

    public static final int PHRASE_COLLISION_MINOR = 1;
    public static final int PHRASE_COLLISION_MEDIUM = 2;
    public static final int PHRASE_COLLISION_MAJOR = 3;

    public void phraseCollision(int collisionStrength);
    public int checkChar(char character);
    public void setPhrase(Phrase phrase);
    public void clearTyped();
    public char getFirstLetter();
    public void update(float dt);
    public boolean isValid();
}
