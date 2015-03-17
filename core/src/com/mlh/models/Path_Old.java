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

import java.util.ArrayList;

/**
 *
 * @author michael
 */
public class Path_Old implements Json.Serializable {

    private int currentWayPoint;
    private final ArrayList<Vector2> path;
    private boolean loop = false;
    private boolean cycle = false;
    private int direction = 1;
    private boolean done;
    // need name to support FileManager otherwise not needed
    // FileManager uses name to autogenerate file name
    private String name;

    public Path_Old() {
        currentWayPoint = -1;
        done = false;
        path = new ArrayList<Vector2>();
    }

    public void addWayPoint(Vector2 vector) {
        path.add(vector.cpy());
    }

    public void removeWayPoint(int index) {
        path.remove(index);
    }

    public Vector2 getWayPoint(int index) {
        if (index >= 0 && index < path.size()) {
            return path.get(index).cpy();
        }
        return null;
    }

    public Vector2 getCurrentWayPoint() {
        if (currentWayPoint < 0) {
            return null;
        }
        return path.get(currentWayPoint).cpy();
    }

    public Vector2 getNextWayPoint() {
        Vector2 wayPoint = null;
        if (currentWayPoint + direction < path.size() && currentWayPoint + direction > -1) {
            currentWayPoint += direction;
            wayPoint = path.get(currentWayPoint).cpy();
        } else if (loop) {
            if (direction > 0) {
                currentWayPoint = -1;
            } else {
                currentWayPoint = path.size();
            }
            wayPoint = getNextWayPoint();
        } else if (cycle) {
            direction *= -1;
            wayPoint = getNextWayPoint();
        }

        if (currentWayPoint + 1 == path.size() && (!loop || !cycle)) {
            done = true;
        }
        return wayPoint;
    }

    public boolean isLoop() {
        return loop;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
        if (loop) {
            cycle = false;
        }
    }

    public boolean isCycle() {
        return cycle;
    }

    public void setCycle(boolean cycle) {
        this.cycle = cycle;
        if (cycle) {
            loop = false;
        }
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        if (direction > 0) {
            this.direction = 1;
        } else {
            this.direction = -1;
        }
    }

    @Override
    public void write(Json json) {
        json.writeValue("name", name);
        json.writeValue("loop", loop);
        json.writeValue("cycle", cycle);
        json.writeValue("path", path);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        name = jsonData.get("name").asString();
        loop = jsonData.get("loop").asBoolean();
        cycle = jsonData.get("cycle").asBoolean();
        JsonValue wayPoint = jsonData.get("path").child();

        while (wayPoint != null) {
            path.add(new Vector2(wayPoint.get("x").asFloat(), wayPoint.get("y").asFloat()));
            wayPoint = wayPoint.next();
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

    public static Path_Old load(String filePathName) {
        Json json = new Json();
        return json.fromJson(Path_Old.class, Gdx.files.internal(filePathName).read());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDone() {
        return done;
    }

    public void reset() {
        currentWayPoint = -1;
        done = false;
    }

    public Path_Old(Path_Old other) {
        this();
        loop = other.loop;
        cycle = other.cycle;

        for (Vector2 vec : other.path) {
            path.add(vec.cpy());
        }
    }

    public Path_Old cpy(Path_Old other) {
        return new Path_Old(other);
    }
    
}
