/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mlh.models;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.mlh.actors.Actor;
import com.mlh.interfaces.IDrawable;
import com.mlh.interfaces.IPhraseCollidable;
import com.mlh.interfaces.IUpdateable;
import com.mlh.managers.PhraseManager;
import com.mlh.view.FontWriter;

/**
 *
 * @author michael
 */
public class ImpPhraseCollidable implements IPhraseCollidable, IDrawable, IUpdateable {

    private final PhraseManager phraseManager;
    private Phrase phrase;
    private float phraseTimer;
    private final float phraseTime;
    private final SpaceObject parent;
    private final Actor player;

    public ImpPhraseCollidable(SpaceObject parent, Actor player, PhraseManager phraseManager, float phraseTime, int phraseLength, String phraseLetters) {
        this.parent = parent;
        this.phraseManager = phraseManager;
        this.phraseTime = phraseTime;
        this.player = player;

        this.phraseManager.register(this, phraseLength, phraseLetters);
        phraseTimer = 0;
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
    public void update(float dt) {
        if (parent.shouldRemove()) {
            phraseManager.deregister(this);
        } else if (phrase.isDone()) {
            phraseTimer += dt;
            if (phraseTimer > phraseTime) {
                phrase.reset();
                phraseTimer = 0;
            }
        }
       
    }
    
    @Override
    public boolean shouldRemove(){
        return parent.shouldRemove();
    }

    @Override
    public char getFirstLetter() {
        return phrase.getFirstLetter();
    }

    @Override
    public void phraseCollision(int collisionStrength) {
        player.fireAt(parent);
    }

    @Override
    public void draw(ShapeRenderer sr, SpriteBatch sb) {
        FontWriter.write(new Vector2(parent.getPosition().x, parent.getPosition().y - 20),
                phrase.getWords()[phrase.getCurrentWord()],
                phrase.getTyped());
    }

    @Override
    public int getDrawLayer() {
        return parent.getDrawLayer();
    }
    
    public int getPhraseLength(){
        return phrase.getLength();
    }

    public boolean isValid() {
        return parent.isValid();
    }
}
