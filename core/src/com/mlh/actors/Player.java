/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mlh.actors;

import com.badlogic.gdx.math.Vector2;
import com.mlh.game.HMSDreadnought;
import com.mlh.models.World;

/**
 *
 * @author michael
 */
public class Player extends Actor {

    private static final String NAME = "Player";

    private long score;
    private int typed;

    public Player(World world) {
        super(NAME, world);

        score = 0;
        typed = 0;
    }

    @Override
    public void update(float dt) {
        super.update(dt);

        // todo we died?
        if (spaceShip.isDisabled()) {
            this.dead = true;
            //spaceShip.spawn(new Vector2(Game.WIDTH / 2 - 70, 200), new Vector2(1f, 0));
        }

    }

    @Override
    public void init(int drawLayer) {
        this.drawLayer = drawLayer;
    }

    @Override
    public void init(String ship, Vector2 position, Vector2 heading, int drawLayer) {
        super.init(ship, position, heading, drawLayer);
        spaceShip.setType("player");
    }

    public void reset(Vector2 position, Vector2 heading) {
        spaceShip.spawn(position, heading);
        spaceShip.setVelocity(Vector2.Zero);
        dead = false;
    }

    public long getScore() {
        return score;
    }

    public void incrementScore(long l) {
        score += l;
    }

    /*
     @Override
     public Vector2 getOrigin() {
     return spaceShip.getOrigin();
     }*/
    @Override
    protected void doActions() {
        // nothing for player to do
    }

    public void incrementTyped(int typed) {
        if (typed > 0) {
            // every time we increase typed we heal the player some
            spaceShip.increaseHealth(HMSDreadnought.getHealForTyping());
            spaceShip.increaseShield(HMSDreadnought.getHealForTyping());
        }
        this.typed += typed;
    }

    public int getTyped() {
        return typed;
    }

    @Override
    protected void handleDisabled() {
        remove = true;
        spaceShip.setEngineSndEftActive(false);
    }

    /**
     * Returns health as a % of max health
     *
     * @return
     */
    public float getHealth() {
        //System.out.println("health = " + spaceShip.getHealth() + "  max = " + spaceShip.getMaxHealth());
        return (spaceShip.getHealth() / spaceShip.getMaxHealth()) * 100;
    }

    public float getShield() {
        //System.out.println("shield = " + spaceShip.getShield() + "  max = " + spaceShip.getMaxShield());
        return (spaceShip.getShield() / spaceShip.getMaxShield()) * 100;
    }
}
