/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mlh.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.mlh.game.FileManager;
import com.mlh.utilities.Debuger;

import java.util.HashMap;
import java.util.Map.Entry;

/**
 *
 * @author michael
 */
public class AnchorSprite extends Sprite implements Json.Serializable {

    private static final float ZERO_DEG_HEADING = 0;

    private String name;
    private String textureFile;
    private int spriteWidth;
    private int spriteHeight;
    private float spriteRotation;
    private int originX;
    private int originY;
    private final HashMap<String, Vector2> anchorPoints;

    public AnchorSprite() {
        super();
        name = "";
        textureFile = "";
        spriteWidth = 0;
        spriteHeight = 0;
        spriteRotation = 0f;
        originX = 0;
        originY = 0;
        anchorPoints = new HashMap<String, Vector2>();
    }

    public AnchorSprite(String name, String textureFile, int spriteWidth, int spriteHeight, float spriteRotation, int originX, int originY) {
        this(name, textureFile, spriteWidth, spriteHeight, spriteRotation, originX, originY, null);
    }

    public AnchorSprite(String name, String textureFile, int spriteWidth, int spriteHeight, float spriteRotation, int originX, int originY, HashMap<String, Vector2> anchorPoints) {
        super(new Texture(textureFile));

        this.name = name;
        this.textureFile = textureFile;
        this.spriteWidth = spriteWidth;
        this.spriteHeight = spriteHeight;
        this.spriteRotation = spriteRotation;
        this.originX = originX;
        this.originY = originY;

        if (anchorPoints != null) {
            this.anchorPoints = new HashMap<String, Vector2>(anchorPoints);
        } else {
            this.anchorPoints = new HashMap<String, Vector2>();
            this.anchorPoints.put("origin", new Vector2(originX, originY));
        }
        Debuger.print(3, name, "this.anchorPoints", this.anchorPoints.toString());
        initAnchorSprite();
    }

    /**
     * Returns the anchor point at index based on current position and heading.
     *
     * @param name of anchor point
     * @return Vector2 anchor point
     */
    public Vector2 getAnchor(String name) {
        return anchorPoints.get(name).cpy();
    }

    public Vector2 getPosition() {
        return new Vector2(getX(), getY());
    }

    public void setPosition(Vector2 position) {
        setX(position.x);
        setY(position.y);
    }

    public Vector2 getCurrentOrigin() {
        return getPosition().add(anchorPoints.get("origin"));
    }

    public void addAnchor(String name, Vector2 point) {
        anchorPoints.put(name, point);
    }

    public void removeAnchor(String name) {
        anchorPoints.remove(name);
    }

    @Override
    public final void setRotation(float degrees) {
        super.setRotation(spriteRotation + degrees);
    }

    private void initAnchorSprite() {
        setRegion(new Texture(textureFile));
        setSize(spriteWidth, spriteHeight);
        setOrigin(originX, originY);
        this.setRotation(ZERO_DEG_HEADING);
    }

    @Override
    public void write(Json json) {
        json.writeValue("name", name);
        json.writeValue("textureFile", textureFile);
        json.writeValue("spriteWidth", spriteWidth);
        json.writeValue("spriteHeight", spriteHeight);
        json.writeValue("spriteRotation", spriteRotation);
        json.writeValue("originX", originX);
        json.writeValue("originY", originY);
        json.writeValue("anchorPoints", anchorPoints, anchorPoints.getClass());
    }

    @Override
    public void read(Json json, JsonValue jsonMap) {
        name = jsonMap.get("name").asString();
        textureFile = jsonMap.get("textureFile").asString();
        spriteWidth = jsonMap.get("spriteWidth").asInt();
        spriteHeight = jsonMap.get("spriteHeight").asInt();
        spriteRotation = jsonMap.get("spriteRotation").asFloat();
        originX = jsonMap.get("originX").asInt();
        originY = jsonMap.get("originY").asInt();

        String childName = jsonMap.get("anchorPoints").child().name();
        while (!childName.equalsIgnoreCase("#done")) {
            anchorPoints.put(
                    childName,
                    new Vector2(
                            jsonMap.get("anchorPoints").get(childName).get("x").asFloat(),
                            jsonMap.get("anchorPoints").get(childName).get("y").asFloat()
                    )
            );

            if (jsonMap.get("anchorPoints").get(childName).next != null) {
                childName = jsonMap.get("anchorPoints").get(childName).next().name();
            } else {
                childName = "#done";
            }
        }
    }

    public String save() {
        String filePathName = FileManager.generatePathFileName(this);
        FileManager.writeJsonFile(filePathName, this);
        return filePathName;
    }

    public String save(String filePathName) {
        FileManager.writeJsonFile(filePathName, this);
        return filePathName;
    }

    public static AnchorSprite load(String filePathName) {
        Debuger.print(3, "AnchorSprite.load", "load", filePathName);
        Json json = new Json();
        AnchorSprite loadedAnchorSprite = json.fromJson(AnchorSprite.class, Gdx.files.internal(filePathName).read());
        loadedAnchorSprite.initAnchorSprite();
        return loadedAnchorSprite;
    }

    public AnchorSprite cpy() {
        return new AnchorSprite(this);
    }

    public AnchorSprite(AnchorSprite anchorSprite) {
        super(anchorSprite);
        this.name = anchorSprite.name;
        this.textureFile = anchorSprite.textureFile;
        this.spriteWidth = anchorSprite.spriteWidth;
        this.spriteHeight = anchorSprite.spriteHeight;
        this.spriteRotation = anchorSprite.spriteRotation;
        this.originX = anchorSprite.originX;
        this.originY = anchorSprite.originY;
        // todo make sure the hashmap constructors creates new anchorpoints and not just copy the references
        this.anchorPoints = new HashMap<String, Vector2>(anchorSprite.anchorPoints);
    }

    public String getName() {
        return name;
    }

    public void drawAnchorPoints(ShapeRenderer sr) {
        sr.setAutoShapeType(true);
        sr.setColor(Color.RED);

        sr.begin();
        Vector2 temp;
        Vector2 tempPos = getPosition();
        for (Entry<String, Vector2> vec : anchorPoints.entrySet()) {
            temp = vec.getValue().cpy();
            temp.add(tempPos);
            sr.line(temp.x - 2,
                    temp.y,
                    temp.x + 2,
                    temp.y
            );
            sr.line(temp.x,
                    temp.y - 2,
                    temp.x,
                    temp.y + 2
            );

        }
        sr.end();
    }
}
