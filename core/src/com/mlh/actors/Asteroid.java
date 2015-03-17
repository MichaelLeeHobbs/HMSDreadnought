/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mlh.actors;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mlh.interfaces.ICollidable;
import com.mlh.interfaces.ITargetable;
import com.mlh.interfaces.WrapedVector2ITarg;
import com.mlh.managers.Jukebox;
import com.mlh.managers.PhraseManager;
import com.mlh.models.*;

/**
 *
 * @author michael
 */
public class Asteroid extends SpaceObjectPhraseCollidable implements ITargetable {

    private static final String AS_NAME = "Asteriod_01";
    private static final String SPRITE_TEXTURE = "core/assets/textures/Asteriod_01.png";

    private final int asteroidType;
    public static final int SMALL = 0;
    public static final int MEDIUM = 1;
    public static final int LARGE = 2;

    private int score;
    private final Player player;

    public int getScore() {
        return score;
    }

    public Asteroid(String name,
            SpaceObject parent,
            int drawLayer,
            World world,
            int phraseDifficulty,
            String phraseLetters,
            PhraseManager phraseManager,
            Player player,
            int type) {
        super(name, parent, world, drawLayer, phraseDifficulty, phraseLetters, phraseManager);
        this.player = player;
        this.asteroidType = type;
        this.boundryResponce = World.BOUNDRY_WRAP;
        maxSpeed = 300;

        setPosition(
                MathUtils.random(0, world.getVisiableSpaceWidth()),
                MathUtils.random(0, world.getVisiableSpaceHeight())
        );

        getSteering().setTarget(
                new WrapedVector2ITarg(
                new Vector2(
                        MathUtils.random(world.getVisiableSpaceWidth() + world.getBoundarySpaceWidth()) * MathUtils.randomSign(),
                        MathUtils.random(world.getVisiableSpaceHeight() + world.getBoundarySpaceHeight()) * MathUtils.randomSign()
                ))
        );
        
        heading.set(getSteering().getTarget().getOrigin());
        velocity.set(getSteering().getTarget().getOrigin());

        int spriteSize = 0;
        if (type == SMALL) {
            spriteSize = MathUtils.random(5, 20);
            maxSpeed = MathUtils.random(70, 100);
            score = 100;
            collisionDamage = 5;
            health = 10;
        } else if (type == MEDIUM) {
            spriteSize = MathUtils.random(20, 40);
            maxSpeed = MathUtils.random(50, 60);
            score = 50;
            collisionDamage = 10;
            health = 20;
        } else if (type == LARGE) {
            spriteSize = MathUtils.random(40, 60);
            maxSpeed = MathUtils.random(20, 30);
            score = 20;
            collisionDamage = 15;
            health = 100;
        }
        
        getSteering().SeekOn();

        anchorSprite = new AnchorSprite(AS_NAME, SPRITE_TEXTURE, spriteSize, spriteSize, 0f, spriteSize / 2, spriteSize / 2);
        anchorSprite.setOriginCenter();
        anchorSprite.addAnchor("origin", new Vector2(spriteSize / 2, spriteSize / 2));
        maxTurnRate = MathUtils.random(-20, 20);

        this.drawAnchor = true;
        this.drawBoundingBox = false;
    }

    @Override
    public void update(float dt) {
        if (remove) {
            return;
        }
        super.update(dt);
        anchorSprite.rotate(maxTurnRate * dt);
        if (disabled) {
            splitAsteroid();
            remove = true;
        }
    }

    @Override
    public void collision(int box, ICollidable other) {
        if (other instanceof Asteroid) {
            // add bounce code
        } else {
            super.collision(box, other);
        }

        if (remove) {
            if (other instanceof Projectile && other.getParent() == player) {
                ((Player) (other).getParent()).incrementScore(score);
            }
            death();
        }
    }

    private void death() {
        Jukebox.play("explode");
        splitAsteroid();
        remove = true;
    }

    @Override
    public void phraseCollision(int collisionStrength) {
        player.fireAt(this, -1);
    }

    private void splitAsteroid() {
        createParticles(position.x, position.y);
        if (asteroidType == Asteroid.LARGE) {
            for (int i = 0; i < 2; i++) {
                Asteroid newAsteroid = new Asteroid(
                        "asteroid",
                        (SpaceObject) owner,
                        1,
                        world,
                        phraseLength,
                        phraseLetters,
                        phraseManager,
                        player,
                        Asteroid.MEDIUM
                );
                newAsteroid.setPosition(position);
                world.addObject(newAsteroid);

            }
        }
        if (asteroidType == Asteroid.MEDIUM) {
            for (int i = 0; i < 2; i++) {
                Asteroid newAsteroid = new Asteroid(
                        "asteroid",
                        (SpaceObject) owner,
                        1,
                        world,
                        phraseLength,
                        phraseLetters,
                        phraseManager,
                        player,
                        Asteroid.SMALL
                );
                newAsteroid.setPosition(position);
                world.addObject(newAsteroid);
            }
        }
    }

    private void createParticles(float x, float y, int number) {
        for (int i = 0; i < number; i++) {
            world.addObject(new Particle(world, x, y));
        }
    }

    private void createParticles(float x, float y) {
        createParticles(x, y, 6);
    }
}
