/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mlh.models.entities.weapons;

import com.badlogic.gdx.math.Vector2;
import com.mlh.actors.Player;
import com.mlh.interfaces.IPhraseCollidable;
import com.mlh.interfaces.ITargetable;
import com.mlh.managers.PhraseManager;
import com.mlh.models.*;

/**
 *
 * @author michael
 */
public class ProjectilePhraseCollidable extends Projectile implements IPhraseCollidable, ITargetable {

    private final ImpPhraseCollidable impPhraseCollidable;
    private final Player player;
    private static final String COLLISION_SND_EFT = "explode";
    private static final int PROJECTILE_DAMAGE_VALUE = 10;

    public ProjectilePhraseCollidable(
            String name,
            SpaceObject parent,
            World world,
            int drawLayer,
            int phraseLength,
            String phraseLetters,
            Vector2 position,
            Vector2 heading,
            AnchorSprite anchorSprite,
            int speed,
            PhraseManager phraseManager,
            Player player
    ) {
        super(name, parent, world, drawLayer, anchorSprite, speed, COLLISION_SND_EFT, PROJECTILE_DAMAGE_VALUE);

        this.player = player;
        int phraseTime = 2;     // time before phrase will reset after miss

        impPhraseCollidable = new ImpPhraseCollidable(
                parent,
                player,
                phraseManager,
                phraseTime,
                phraseLength,
                phraseLetters
        );
    }

    @Override
    public void update(float dt) {
        if (remove) {
            return;
        }
        super.update(dt);
        impPhraseCollidable.update(dt);
    }

    @Override
    public void phraseCollision(int collisionStrength) {
        player.fireAt(this);
    }

    @Override
    public int checkChar(char character) {
        return impPhraseCollidable.checkChar(character);
    }

    @Override
    public void setPhrase(Phrase phrase) {
        impPhraseCollidable.setPhrase(phrase);
    }

    @Override
    public void clearTyped() {
        impPhraseCollidable.clearTyped();
    }

    @Override
    public char getFirstLetter() {
        return impPhraseCollidable.getFirstLetter();
    }

    @Override
    public int getCollisionValue() {
        return 0;
    }
}
