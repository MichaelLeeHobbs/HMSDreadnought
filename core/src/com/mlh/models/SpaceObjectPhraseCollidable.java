/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mlh.models;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.mlh.interfaces.IPhraseCollidable;
import com.mlh.managers.PhraseManager;
import com.mlh.view.FontWriter;

/**
 *
 * @author michael
 */
public abstract class SpaceObjectPhraseCollidable extends SpaceObject implements IPhraseCollidable {

    protected PhraseManager phraseManager;
    protected Phrase phrase;
    protected float phraseTimer;
    protected float phraseTime;
    protected int phraseLength;
    protected String phraseLetters;

    public SpaceObjectPhraseCollidable(String name,
            SpaceObject parent,
            World world,
            int drawLayer,
            int phraseLength,
            String phraseLetters,
            PhraseManager phraseManager) {
        super(name, parent, world, drawLayer);

        this.phraseLength = phraseLength;
        this.phraseLetters = phraseLetters;
        this.phraseManager = phraseManager;
        this.phraseManager.register(this, phraseLength, phraseLetters);
        phraseTime = 0;
        // after two second reset phrase
        // this assumes whatever shot at us missed
        phraseTimer = 2;

    }

    @Override
    public int checkChar(char character) {
        return phrase.checkChar(character);
    }

    @Override
    public void setPhrase(Phrase phrase) {
        this.phrase = new Phrase(phrase);
    }

    @Override
    public void clearTyped() {
        phrase.clearTyped();
    }

    @Override
    public void draw(ShapeRenderer sr, SpriteBatch sb) {
        FontWriter.write(
                new Vector2(position.x, position.y - 20),
                phrase.getWords()[phrase.getCurrentWord()],
                phrase.getTyped());
        super.draw(sr, sb);
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        if (remove) {
            phraseManager.deregister(this);
        } else if (phrase.isDone()) {
            phraseTime += dt;
            if (phraseTime > phraseTimer) {
                phrase.reset();
                phraseTime = 0;
            }
        }
    }

    @Override
    public char getFirstLetter() {
        return phrase.getFirstLetter();
    }
}
