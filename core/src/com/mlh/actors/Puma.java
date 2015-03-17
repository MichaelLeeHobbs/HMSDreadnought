/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mlh.actors;

import com.badlogic.gdx.math.Vector2;
import com.mlh.game.HMSDreadnought;
import com.mlh.interfaces.IPhraseCollidable;
import com.mlh.interfaces.WrapedVector2ITarg;
import com.mlh.managers.PhraseManager;
import com.mlh.models.GameAnimation;
import com.mlh.models.ImpPhraseCollidable;
import com.mlh.models.World;

/**
 *
 * @author michael
 */
public class Puma extends EnemyActor implements IPhraseCollidable {

    // first boss
    private static final String NAME = "Puma";
    private static final String SHIP_FILE = "core/assets/data/ships/Puma.shp";
    private static final int SCORE_VALUE = 10000;

    private boolean init = true;
    private boolean enter = false;

    public Puma(World world, Player player, PhraseManager phraseManager, int phraseLength, String phraseLetters) {
        super(world, NAME, player, phraseManager, phraseLength, phraseLetters);
    }

    @Override
    public void init(int drawLayer) {
        super.init(SHIP_FILE, Vector2.Zero, Vector2.Zero, drawLayer);
        spaceShip.setBoundryResponce(World.BOUNDRY_IGNORE);
        impPhraseCollidable = new ImpPhraseCollidable(spaceShip, player, phraseManager, HMSDreadnought.phraseTimer, phraseLength, phraseLetters);
        world.addObject(impPhraseCollidable);
        this.scoreValue = SCORE_VALUE;
        
        // death animation
        this.TEXTURE_FILE = "core/assets/textures/explosion3x3.png";
        deathAnimation = new GameAnimation(TEXTURE_FILE, FRAME_COLS, FRAME_ROWS, FRAME_TIME, false);
        deathAnimation.init();
    }

    @Override
    protected void doActions() {
        super.doActions();

        if (init) {
            init = false;
            spaceShip.setPosition(startPos);
            enter = true;
        }

        if (enter) {
            // fly to top center of screen then attack player till dead
            spaceShip.getSteering().setTarget(new WrapedVector2ITarg(targetPos));
            spaceShip.getSteering().SeekOn();
            enter = false;
        } else {
            if (spaceShip.getPosition().dst(targetPos) < 30) {
                spaceShip.getSteering().SeekOff();
                spaceShip.setVelocity(Vector2.Zero);
            }
        }

    }
}
