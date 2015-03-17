/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mlh.managers;

import com.mlh.interfaces.IPhraseCollidable;
import com.mlh.models.Phrase;

import java.util.ArrayList;
import java.util.List;

import static com.mlh.interfaces.IPhraseCollidable.*;

/**
 *
 * @author michael
 */
public class PhraseManager implements GameInputListener {

    private final List<IPhraseCollidable> phraseCollidables;
    private boolean locked;
    private IPhraseCollidable lockedCollidable;
    private int typed;

    public PhraseManager() {
        phraseCollidables = new ArrayList<IPhraseCollidable>();
        locked = false;
        typed = 0;
    }

    private void checkForMatch(char character) {
        // if ESCAPE_CHAR break out and clear current word
        if (character == Phrase.BACKSPACE_CHAR) {
            if (locked) {
                locked = false;
                lockedCollidable.clearTyped();
                return;
            }
        }

        if (locked) {
            if (!lockedCollidable.isValid()) {
                locked = false;
            }
        }

        if (locked) {
            process(lockedCollidable, character);
        } else {
            for (IPhraseCollidable p : phraseCollidables) {
                if (process(p, character)) {
                    break;
                }
            }
        }
    }

    private boolean process(IPhraseCollidable p, char character) {
        int result = Phrase.NOT_VALID_CHAR;
        if (!p.isValid()) {
            return result > Phrase.NOT_VALID_CHAR;
        }
        
        result = p.checkChar(character);
        switch (result) {
            case Phrase.VALID_CHAR:
                typed++;
                if (!locked) {
                    lockedCollidable = p;
                    locked = true;
                }
                break;
            case Phrase.WORD_DONE:
                lockedCollidable.phraseCollision(PHRASE_COLLISION_MINOR);
                locked = false;
                break;
            case Phrase.LINE_DONE:
                lockedCollidable.phraseCollision(PHRASE_COLLISION_MEDIUM);
                locked = false;
                break;
            case Phrase.PHRASE_DONE:
                lockedCollidable.phraseCollision(PHRASE_COLLISION_MAJOR);
                locked = false;
                break;
            case Phrase.NOT_VALID_CHAR:
                // do nothing
                break;
        }
        return result > Phrase.NOT_VALID_CHAR;
    }

    public void register(IPhraseCollidable phraseCollidable, int phraseLength, String phraseLetters) {
        // get phrase
        //phrase = PhraseGenerator.randomPhrase(MathUtils.random(1,10));
        boolean badPhrase;
        Phrase p;
        int attempts = 0;
        do {
            p = PhraseFactory.phrase(phraseLength, phraseLetters);
            badPhrase = false;
            for (IPhraseCollidable pc : phraseCollidables) {
                if (pc.getFirstLetter() == p.getFirstLetter()) {
                    badPhrase = true;
                    attempts++;
                }
            }
            if (attempts > 100) {
                badPhrase = false;
            }
        } while (badPhrase);
        phraseCollidable.setPhrase(p);
        phraseCollidables.add(phraseCollidable);
    }

    public void deregister(IPhraseCollidable phraseCollidable) {
        if (phraseCollidable == lockedCollidable) {
            locked = false;
        }
        phraseCollidables.remove(phraseCollidable);
    }

    @Override
    public boolean keyTyped(char character) {
        checkForMatch(character);
        return false;
    }
    
    public int getTyped() {
        return typed;
    }
}
