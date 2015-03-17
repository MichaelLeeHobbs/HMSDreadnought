/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mlh.models;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.mlh.game.FileManager;
import com.mlh.interfaces.ICollidable;
import com.mlh.interfaces.IDrawable;
import com.mlh.interfaces.ITargetable;
import com.mlh.utilities.Debuger;

/**
 *
 * @author michael
 */
public abstract class SpaceObject extends Vehicle implements ICollidable, IDrawable, ITargetable, Json.Serializable {

    // saved fields
    //protected float maxTurnRate;
    //protected float maxSpeed;
    //protected float maxForce;
    protected float health;
    protected float shield;
    protected int collisionDamage;
    protected String anchorSpriteFile;
    // protected string name - from entity class

    // fields
    protected int drawLayer;
    protected boolean disabled;

    // generated fields
    protected AnchorSprite anchorSprite;

    // debuging fields
    protected boolean drawAnchor = false;
    protected boolean drawBoundingBox = false;

    public SpaceObject() {
        super();
    }

    private SpaceObject(
            String name,
            Object parent,
            World world,
            int boundryResponce,
            Vector2 position,
            Vector2 velocity,
            Vector2 heading,
            float maxTurnRate,
            float maxSpeed,
            float maxForce,
            float health,
            float shield,
            int collisionDamage,
            AnchorSprite anchorSprite) {

        this.name = name;
        this.owner = parent;
        this.world = world;
        this.boundryResponce = boundryResponce;
        this.position.set(position);
        this.velocity.set(velocity);
        this.heading.set(heading);
        this.maxTurnRate = maxTurnRate;
        this.maxSpeed = maxSpeed;
        this.maxForce = maxForce;
        this.health = health;
        this.shield = shield;
        this.collisionDamage = collisionDamage;
        this.anchorSprite = anchorSprite;
        this.anchorSpriteFile = FileManager.generatePathFileName(anchorSprite);
    }

    public SpaceObject(String name,
            Object parent,
            World world,
            int drawLayer) {
        this();
        this.name = name;
        this.owner = parent;
        this.world = world;
        this.drawLayer = drawLayer;
    }

    public void initSpaceObject(Object parent, World world, int drawLayer) {
        super.init(name, parent, world);
        this.drawLayer = drawLayer;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        // update sprite
        anchorSprite.setPosition(getPosition());
        anchorSprite.setRotation(heading.angleRad() * MathUtils.radiansToDegrees);
        Debuger.print(0, name, "position", position.toString());

    }

    @Override
    public void draw(ShapeRenderer sr, SpriteBatch sb) {
        sb.begin();
        anchorSprite.draw(sb);
        sb.end();
        if (drawAnchor) {
            drawAnchor(sr);
        }
        if (drawBoundingBox) {
            drawBoundingBox(sr);
        }
    }

    private void drawBoundingBox(ShapeRenderer sr) {
        sr.setAutoShapeType(true);
        sr.setColor(Color.RED);

        sr.begin();
        sr.rect(anchorSprite.getBoundingRectangle().x,
                anchorSprite.getBoundingRectangle().y,
                anchorSprite.getBoundingRectangle().width,
                anchorSprite.getBoundingRectangle().height);
        sr.end();
    }

    private void drawAnchor(ShapeRenderer sr) {
        anchorSprite.drawAnchorPoints(sr);

        sr.setAutoShapeType(true);
        sr.setColor(Color.GREEN);

        sr.begin();
        sr.line(anchorSprite.getPosition().x - 5,
                anchorSprite.getPosition().y,
                anchorSprite.getPosition().x + 5,
                anchorSprite.getPosition().y);
        sr.line(anchorSprite.getPosition().x,
                anchorSprite.getPosition().y - 5,
                anchorSprite.getPosition().x,
                anchorSprite.getPosition().y + 5);
        sr.end();

    }

    @Override
    public Rectangle[] getCollisionBoxes() {
        return new Rectangle[]{anchorSprite.getBoundingRectangle()};
    }

    @Override
    public int getCollisionValue() {
        return collisionDamage;
    }

    @Override
    public void collision(int box, ICollidable other) {
        if (this.getParent() != other && this != other.getParent()) {
            if (other.getType().equals(this.getType())) {
                return;
            }
            int damage = other.getCollisionValue();
            Debuger.print(3, this.name, "collided with", other.getName() + " damage: " + damage);
            if (damage > shield) {
                damage -= shield;
                shield = 0;
                if (damage >= health) {
                    health = 0;
                    disabled = true;
                } else {
                    health -= damage;
                }
            } else {
                shield -= damage;
            }
            Debuger.print(3, this.name, "shield: " + shield, "health: " + health);

        }
    }

    @Override
    public boolean isValid() {
        return !remove;
    }

    @Override
    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        this.health = health;
    }

    @Override
    public int getDrawLayer() {
        return drawLayer;
    }

    public void setDrawLayer(int drawLayer) {
        this.drawLayer = drawLayer;
    }

    @Override
    public Vector2 getOrigin() {
        return anchorSprite.getCurrentOrigin();
    }

    public Vector2 getAnchor(String name) {
        return anchorSprite.getAnchor(name);
    }

    @Override
    public float getShield() {
        return shield;
    }

    public void setShield(float shield) {
        this.shield = shield;
    }

    @Override
    public void setHeading(Vector2 heading) {
        super.setHeading(heading);
        anchorSprite.setRotation(this.heading.angle());
    }

    @Override
    public void write(Json json) {
        json.writeValue("name", name);
        json.writeValue("mass", mass);
        json.writeValue("maxTurnRate", maxTurnRate);
        json.writeValue("maxSpeed", maxSpeed);
        json.writeValue("maxForce", maxForce);
        json.writeValue("health", health);
        json.writeValue("shield", shield);
        json.writeValue("collisionDamage", collisionDamage);
        json.writeValue("anchorSpriteFile", anchorSpriteFile);
    }

    @Override
    public void read(Json json, JsonValue jsonMap) {
        name = jsonMap.get("name").asString();
        mass = jsonMap.get("mass").asFloat();
        maxTurnRate = jsonMap.get("maxTurnRate").asFloat();
        maxSpeed = jsonMap.get("maxSpeed").asFloat();
        maxForce = jsonMap.get("maxForce").asFloat();
        health = jsonMap.get("health").asFloat();
        shield = jsonMap.get("shield").asFloat();
        collisionDamage = jsonMap.get("collisionDamage").asInt();
        anchorSpriteFile = jsonMap.get("anchorSpriteFile").asString();
    }

    public String save() {
        String filePathName = FileManager.generatePathFileName(this);
        FileManager.writeJsonFile(filePathName, this);
        anchorSprite.save();
        return filePathName;
    }

    public String save(String filePathName) {
        FileManager.writeJsonFile(filePathName, this);
        anchorSprite.save();
        return filePathName;
    }

    public AnchorSprite getAnchorSprite() {
        return anchorSprite.cpy();
    }

    public void setAnchorSprite(AnchorSprite anchorSprite) {
        this.anchorSprite = anchorSprite.cpy();
        this.anchorSpriteFile = FileManager.generatePathFileName(anchorSprite);
        Debuger.print(3, name, "set anchorSpriteFile", this.anchorSpriteFile);
    }

    public String getAnchorSpriteFile() {
        return anchorSpriteFile;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void debug() {

        System.out.println("===" + SpaceObject.class.getName() + " Debug ===");
        System.out.println("name: " + name);
        System.out.println("owner: " + owner.toString());
        System.out.println("world: " + world);
        System.out.println("boundryResponce: " + boundryResponce);
        System.out.println("position: " + position);
        System.out.println("velocity: " + velocity);
        System.out.println("heading: " + heading);
        System.out.println("origin: " + getOrigin());
        System.out.println("maxTurnRate: " + maxTurnRate);
        System.out.println("maxSpeed: " + maxSpeed);
        System.out.println("maxForce: " + maxForce);
        System.out.println("health: " + health);
        System.out.println("shield: " + shield);
        System.out.println("collisionDamage: " + collisionDamage);
        System.out.println("anchorSprite: " + anchorSprite);

    }
}
