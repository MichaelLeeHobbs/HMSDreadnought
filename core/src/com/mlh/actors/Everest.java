/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mlh.actors;

import com.badlogic.gdx.math.Vector2;
import com.mlh.game.HMSDreadnought;
import com.mlh.interfaces.WrapedVector2ITarg;
import com.mlh.managers.PhraseManager;
import com.mlh.models.ImpPhraseCollidable;
import com.mlh.models.World;

/**
 *
 * @author michael
 */
public class Everest extends EnemyActor {

    // first boss
    private static final String NAME = "Everest";
    private static final int SCORE_VALUE = 1000;

    private boolean enter = true;

    public Everest(World world, Player player, PhraseManager phraseManager, int phraseLength, String phraseLetters) {
        super(world, NAME, player, phraseManager, phraseLength, phraseLetters);
    }

    @Override
    public void init(int drawLayer) {
        String SHIP_FILE = "core/assets/data/ships/Everest.shp";
        super.init(SHIP_FILE, Vector2.Zero, Vector2.Zero, drawLayer);
        spaceShip.setBoundryResponce(World.BOUNDRY_IGNORE);
        impPhraseCollidable = new ImpPhraseCollidable(spaceShip, player, phraseManager, HMSDreadnought.phraseTimer, phraseLength, phraseLetters);
        world.addObject(impPhraseCollidable);
        this.scoreValue = SCORE_VALUE;
    }

    @Override
    protected void doActions() {
        super.doActions();

        if (enter) {
            targetPos.set(path.getNextWayPoint());
            spaceShip.getSteering().setTarget(new WrapedVector2ITarg(targetPos));
            spaceShip.getSteering().SeekOn();
            enter = false;
        } else {
            if (spaceShip.getPosition().dst(targetPos) < 30) {
                if (path.getNextWayPoint() != null) {
                    targetPos.set(path.getCurrentWayPoint());
                    spaceShip.getSteering().setTarget(new WrapedVector2ITarg(targetPos));
                } else {
                    path.reset();
                    spaceShip.setPosition(path.getNextWayPoint());
                    targetPos.set(path.getNextWayPoint());
                    spaceShip.getSteering().setTarget(new WrapedVector2ITarg(targetPos));
                }
                //spaceShip.getSteering().SeekOff();
                //spaceShip.setVelocity(Vector2.Zero);
            }
        }
    }
}
