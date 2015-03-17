/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mlh.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.mlh.game.FileManager;
import com.mlh.interfaces.ICollidable;
import com.mlh.interfaces.ITargetable;
import com.mlh.managers.Jukebox;
import com.mlh.utilities.Debuger;

/**
 *
 * @author michael
 */
public class Projectile extends SpaceObject {

    private ITargetable target;

    public static Projectile load(String filePathName) {
        Debuger.print(3, "Projectile.load", "load", filePathName);
        Json json = new Json();
        Projectile newProjectile = json.fromJson(Projectile.class, Gdx.files.internal(filePathName).read());
        AnchorSprite newAnchorSprite = AnchorSprite.load(newProjectile.getAnchorSpriteFile());
        newProjectile.setAnchorSprite(newAnchorSprite);
        Debuger.print(3, "Projectile.load", "completed", "successfully");
        return newProjectile;
    }

    protected String collisionSndEft;

    public Projectile() {
        super();
    }

    public Projectile(
            String name,
            SpaceObject owner,
            World world,
            int drawLayer,
            AnchorSprite anchorSprite,
            float speed,
            String collisionSndEft,
            int collisionDamage) {
        super(name, owner, world, drawLayer);

        this.anchorSprite = anchorSprite;
        this.anchorSpriteFile = FileManager.generatePathFileName(this.anchorSprite);
        this.anchorSprite.setOriginCenter();
        this.anchorSprite.setPosition(position);
        this.collisionSndEft = collisionSndEft;
        this.collisionDamage = collisionDamage;

        setPosition(position);
        maxForce = speed;
        maxSpeed = speed;

        velocity.set(heading);

        velocity.scl(maxForce);
        this.heading.set(heading);

        // we need a target and this will do until set otherwise
        this.target = this;
    }

    public Projectile(Projectile other) {
        this();
        this.name = other.name;
        this.owner = other.owner;
        this.world = other.world;
        this.drawLayer = other.drawLayer;
        this.anchorSprite = other.anchorSprite.cpy();
        this.maxSpeed = other.maxSpeed;
        this.maxTurnRate = other.maxTurnRate;
        this.maxForce = other.maxForce;
        this.health = other.health;
        this.shield = other.shield;
        this.collisionDamage = other.collisionDamage;
        this.anchorSpriteFile = other.anchorSpriteFile;
        this.collisionSndEft = other.collisionSndEft;
        // we need a target and this will do until set otherwise
        this.target = this;
    }

    @Override
    public void collision(int box, ICollidable other) {
        super.collision(box, other);
        if (other != owner) {
            Jukebox.play(collisionSndEft);
            remove = true;
        }
    }

    public Projectile cpy(SpaceObject owner) {
        Projectile newProjectile = new Projectile(this);
        newProjectile.owner = owner;
        return newProjectile;
    }

    public void initProjectile(SpaceObject owner, World world, int drawLayer) {
        this.owner = owner;
        this.world = world;
        this.drawLayer = drawLayer;
    }

    @Override
    public void read(Json json, JsonValue jsonMap) {
        super.read(json, jsonMap);
        collisionSndEft = jsonMap.get("collisionSndEft").asString();
    }

    @Override
    public void write(Json json) {
        super.write(json);
        json.writeValue("collisionSndEft", collisionSndEft);
    }

    public void setWorld(World world) {
        this.world = world;
    }

    @Override
    public void debug() {
        System.out.println("===" + Projectile.class.getName() + " Debug ===");
        System.out.println("collisionSndEft: " + collisionSndEft);
        super.debug();
    }

    public Projectile fireAt(SpaceObject owner, Vector2 position, ITargetable target) {
        Projectile newProjectile = cpy(owner);
        newProjectile.target = target;
        newProjectile.setPosition(position);
        newProjectile.getSteering().setTarget(target);
        newProjectile.getSteering().SeekOn();

        Debuger.print(2, "target", "position = " + target.getPosition(), "origin = " + target.getOrigin());
        //Vector2 angleToTarget = VectorUtilities.vectorToTarget(position, target.getOrigin(), target.getVelocity(), maxSpeed);
        // scale it up so it will fly off the screen if it misses
        //newProjectile.setHeading(angleToTarget);
        //newProjectile.setVelocity(newProjectile.getHeading().nor().scl(10000));
        return newProjectile;
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        if (!target.isValid()) {
            remove = true;
        }
    }

}
