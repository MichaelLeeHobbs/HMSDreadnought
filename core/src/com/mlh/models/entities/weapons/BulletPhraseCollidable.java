/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mlh.models.entities.weapons;

import com.badlogic.gdx.math.Vector2;
import com.mlh.actors.Player;
import com.mlh.interfaces.ITargetable;
import com.mlh.managers.PhraseManager;
import com.mlh.models.AnchorSprite;
import com.mlh.models.SpaceObject;
import com.mlh.models.World;

/**
 *
 * @author michael
 */
public class BulletPhraseCollidable extends ProjectilePhraseCollidable implements ITargetable {

    private static final String NAME = "Plasma Bolt PC";
    private static final String AS_NAME = "Plasma Bolt";
    private static final String SPRITE_TEXTURE = "core/assets/textures/bullet.png";
    private static final int SPRITE_WIDTH = 10;
    private static final int SPRITE_HEIGHT = 10;
    private static final Vector2 ORGIN = new Vector2(SPRITE_WIDTH / 2, SPRITE_HEIGHT / 2);
    private static final int SPRITE_ROTATION = -90;
    private static final int MAX_LIFE_TIME = 5;
    private static final int SPEED = 150;
    private static final int DAMAGE_VALUE = 10;

    public BulletPhraseCollidable(
            SpaceObject parent,
            World world,
            int drawLayer,
            Vector2 position,
            Vector2 heading,
            int phraseLength,
            String phraseLetters,
            PhraseManager phraseManager,
            Player player
    ) {
        super(NAME,
                parent,
                world,
                drawLayer,
                phraseLength, 
                phraseLetters, 
                position, 
                heading, 
                new AnchorSprite(AS_NAME, SPRITE_TEXTURE, SPRITE_WIDTH, SPRITE_HEIGHT, 0f, SPRITE_WIDTH / 2, SPRITE_HEIGHT / 2), 
                SPEED, 
                phraseManager, 
                player
        );
        anchorSprite.addAnchor("origin", ORGIN);
    }

    @Override
    public int getCollisionValue() {
        return DAMAGE_VALUE;
    }

}
