/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mlh.actors;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mlh.game.HMSDreadnought;
import com.mlh.interfaces.IPhraseCollidable;
import com.mlh.managers.PhraseManager;
import com.mlh.models.*;
import com.mlh.utilities.Debuger;

import java.util.ArrayList;

public abstract class EnemyActor extends Actor implements IPhraseCollidable {

    private static final String TYPE = "enemy";
    private static int enemyCount = 0;

    // -1 cause we need 0 to x - 1
    private static final int NUM_PATHS = 5 - 1;

    protected final Player player;
    protected Path_Old path;

    protected final PhraseManager phraseManager;
    protected final int phraseLength;
    protected final String phraseLetters;
    protected ImpPhraseCollidable impPhraseCollidable;
    protected boolean moveSoundOn = false;
    protected boolean firing = false;

    protected Vector2 startPos;
    protected Vector2 targetPos;

    protected static int lastRandomPath = -1;
    protected int scoreValue = 0;

    protected float lastVisible = 0;
    protected float lastVisibleMax = 15;

    // death animation
    protected String TEXTURE_FILE = "core/assets/textures/explosion.png";
    protected int FRAME_COLS = 17;
    protected int FRAME_ROWS = 1;
    protected float FRAME_TIME = 0.040f;
    protected GameAnimation deathAnimation;

    public EnemyActor(World world, String name, Player player, PhraseManager phraseManager, int phraseLength, String phraseLetters) {
        super(name, world);
        EnemyActor.enemyCount++;
        this.player = player;
        this.phraseManager = phraseManager;
        this.phraseLength = phraseLength;
        this.phraseLetters = phraseLetters;
        targetPos = new Vector2();
        startPos = new Vector2();
        deathAnimation = new GameAnimation(TEXTURE_FILE, FRAME_COLS, FRAME_ROWS, FRAME_TIME, false);
        deathAnimation.init();
    }

    @Override
    public void init(String ship, Vector2 position, Vector2 heading, int drawLayer) {
        super.init(ship, position, heading, drawLayer);
        super.setType(TYPE);
        scoreValue *= HMSDreadnought.wpm;
    }

    public Path_Old getLastPath(float xOffset, float yOffset) {
        switch (lastRandomPath) {
            case 0:
                return pathOne(xOffset, yOffset);
            case 1:
                return pathTopLeftToBottomRight(xOffset, yOffset);
            case 2:
                return pathTopRightToBottomLeft(xOffset, yOffset);
            case 3:
                return pathMidRightToLeft(xOffset, yOffset);
            case 4:
                return pathTLBRTRBL(xOffset, yOffset);

        }
        return null;
    }

    public Path_Old getRandomPath(float xOffset, float yOffset) {
        // dont give out a path the same as the last call
        // this is to avoid ships ontop of each other
        int rPath = lastRandomPath;
        while (rPath == lastRandomPath) {
            rPath = MathUtils.random(NUM_PATHS);
        }
        lastRandomPath = rPath;

        switch (rPath) {
            case 0:
                return pathOne(xOffset, yOffset);
            case 1:
                return pathTopLeftToBottomRight(xOffset, yOffset);
            case 2:
                return pathTopRightToBottomLeft(xOffset, yOffset);
            case 3:
                return pathMidRightToLeft(xOffset, yOffset);
            case 4:
                return pathTLBRTRBL(xOffset, yOffset);

        }
        return null;
    }

    public Path_Old pathOne(float xOffset, float yOffset) {
        Path_Old newPath = new Path_Old();
        newPath.addWayPoint(new Vector2(world.getVisiableSpaceWidth() + world.getBoundarySpaceWidth() + xOffset, 200 + yOffset));
        newPath.addWayPoint(new Vector2(player.getPosition().x + 400 + xOffset, player.getPosition().y + 100 + yOffset));
        newPath.addWayPoint(new Vector2(player.getPosition().x + 300 + xOffset, player.getPosition().y + 200 + yOffset));
        newPath.addWayPoint(new Vector2(player.getPosition().x + 200 + xOffset, player.getPosition().y + 300 + yOffset));
        newPath.addWayPoint(new Vector2(player.getPosition().x + 100 + xOffset, player.getPosition().y + 400 + yOffset));
        newPath.addWayPoint(new Vector2(0 - world.getBoundarySpaceWidth() + xOffset, player.getPosition().y + 400 + yOffset));

        return newPath;
    }

    /**
     * Top Left Bottom Right Top Right Bottom Left
     *
     * @param xOffset
     * @param yOffset
     * @return
     */
    public Path_Old pathTLBRTRBL(float xOffset, float yOffset) {
        Path_Old newPath = new Path_Old();
        newPath.addWayPoint(new Vector2(
                0 - spaceShip.getAnchorSprite().getWidth() * 2 - xOffset,
                world.getBoundarySpaceHeight() + world.getBoundarySpaceHeight() + yOffset
        ));

        newPath.addWayPoint(new Vector2(
                world.getVisiableSpaceWidth() + world.getBoundarySpaceWidth() + xOffset,
                0 - world.getBoundarySpaceHeight() - yOffset
        ));
        newPath.addWayPoint(new Vector2(
                world.getVisiableSpaceWidth() + spaceShip.getAnchorSprite().getWidth() * 2 + xOffset,
                world.getBoundarySpaceHeight() + world.getBoundarySpaceHeight() + yOffset
        ));

        newPath.addWayPoint(new Vector2(
                0 - world.getBoundarySpaceWidth() - xOffset,
                0 - world.getBoundarySpaceHeight() - yOffset
        ));
        return newPath;
    }

    public Path_Old pathTopLeftToBottomRight(float xOffset, float yOffset) {
        Path_Old newPath = new Path_Old();
        newPath.addWayPoint(new Vector2(
                0 - spaceShip.getAnchorSprite().getWidth() * 2 - xOffset,
                world.getBoundarySpaceHeight() + world.getBoundarySpaceHeight() + yOffset
        ));

        newPath.addWayPoint(new Vector2(
                world.getVisiableSpaceWidth() + world.getBoundarySpaceWidth() + xOffset,
                0 - world.getBoundarySpaceHeight() - yOffset
        ));

        newPath.addWayPoint(new Vector2(
                0 - spaceShip.getAnchorSprite().getWidth() * 2 - xOffset,
                world.getBoundarySpaceHeight() + world.getBoundarySpaceHeight() + xOffset
        ));

        return newPath;
    }

    public Path_Old pathTopRightToBottomLeft(float xOffset, float yOffset) {
        Path_Old newPath = new Path_Old();
        newPath.addWayPoint(new Vector2(
                world.getVisiableSpaceWidth() + spaceShip.getAnchorSprite().getWidth() * 2 + xOffset,
                world.getBoundarySpaceHeight() + world.getBoundarySpaceHeight() + yOffset
        ));

        newPath.addWayPoint(new Vector2(
                0 - world.getBoundarySpaceWidth() - xOffset,
                0 - world.getBoundarySpaceHeight() - yOffset
        ));

        newPath.addWayPoint(new Vector2(
                world.getVisiableSpaceWidth() + spaceShip.getAnchorSprite().getWidth() * 2 + xOffset,
                world.getBoundarySpaceHeight() + world.getBoundarySpaceHeight() + yOffset
        ));

        return newPath;
    }

    public Path_Old pathMidRightToLeft(float xOffset, float yOffset) {
        Path_Old newPath = new Path_Old();
        newPath.addWayPoint(new Vector2(
                world.getVisiableSpaceWidth() + world.getBoundarySpaceWidth() + xOffset,
                world.getVisiableSpaceHeight() / 2 + yOffset
        ));

        newPath.addWayPoint(new Vector2(
                0 - world.getBoundarySpaceWidth() - xOffset,
                world.getVisiableSpaceHeight() / 2 + yOffset
        ));

        newPath.addWayPoint(new Vector2(
                world.getVisiableSpaceWidth() + world.getBoundarySpaceWidth() + xOffset,
                world.getVisiableSpaceHeight() / 2 + yOffset
        ));

        return newPath;
    }

    public void setPath(Path_Old path) {
        this.path = path;
        spaceShip.setPosition(path.getWayPoint(0));
        spaceShip.getSteering().ObstacleAvoidanceOn();
    }

    @Override
    public void phraseCollision(int collisionStrength) {
        impPhraseCollidable.phraseCollision(collisionStrength);
    }

    @Override
    public char getFirstLetter() {
        return impPhraseCollidable.getFirstLetter();
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

    public boolean isMoveSoundOn() {
        return moveSoundOn;
    }

    public void setMoveSoundOn(boolean moveSoundOn) {
        this.moveSoundOn = moveSoundOn;
    }

    @Override
    protected void handleDisabled() {
        spaceShip.setEngineSndEftActive(false);
        spaceShip.setRemove(true);
        dead = true;

        if (deathAnimation.isDone()) {
            remove = true;
            EnemyActor.enemyCount--;
            player.incrementScore(getScoreValue());
        }
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        if (spaceShip.isDisabled()) {
            deathAnimation.setPosition(spaceShip.getOrigin());
            deathAnimation.update(dt);
            return;
        }
        if (spaceShip.isVisible()) {
            lastVisible = 0;
        } else {
            lastVisible += dt;
            if (lastVisible > lastVisibleMax) {
                remove = true;
                EnemyActor.enemyCount--;
                System.out.println("removed " + this.getName() + "  count " + EnemyActor.getEnemyCount());
                spaceShip.setRemove(true);
            }
        }

    }

    @Override
    public void draw(ShapeRenderer sr, SpriteBatch sb) {
        super.draw(sr, sb);
        if (!deathAnimation.isDone() && spaceShip.isDisabled()) {
            deathAnimation.draw(sb);
        }
    }

    
    
    @Override
    protected void doActions() {
        if (spaceShip.isVisible()) {
            if (!firing) {
                Debuger.print(2, this.getName(), "fireAt", player.getName());
                fireAllWeaponsAt(player, true);
                firing = true;
            }
            if (!spaceShip.isEngineSndEftActive()) {
                spaceShip.setEngineSndEftActive(true);
            }

        } else if (!spaceShip.isVisible()) {
            if (firing) {
                Debuger.print(2, this.getName(), "stop fireAt", player.getName());
                stopFiring();
                firing = false;
            }
            if (spaceShip.isEngineSndEftActive()) {
                spaceShip.setEngineSndEftActive(false);
            }
        }

        if (behaviorAI.pathDone()) {
            remove = true;
        }
    }

    public int getPhraseLength() {
        return impPhraseCollidable.getPhraseLength();
    }

    public Vector2 getTargetPos() {
        return targetPos.cpy();
    }

    public void setTargetPos(Vector2 targetPos) {
        this.targetPos = targetPos.cpy();
    }

    public Vector2 getStartPos() {
        return startPos.cpy();
    }

    public void setStartPos(Vector2 startPos) {
        this.startPos = startPos.cpy();
    }

    public int getScoreValue() {
        float modifer = lifeTime / 100;
        return (int) (scoreValue * modifer);
    }

    @Override
    public boolean isValid() {
        return spaceShip.isValid();
    }

    public static int getEnemyCount() {
        return enemyCount;
    }

    public void setPhrase(ArrayList<String> phraseList) {
        impPhraseCollidable.setPhrase(new Phrase(phraseList));
    }
}
