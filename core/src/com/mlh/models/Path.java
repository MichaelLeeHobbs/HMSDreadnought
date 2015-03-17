/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mlh.models;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mlh.utilities.VectorUtilities;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import static java.lang.Math.min;

/**
 *
 * @author michael
 */
public class Path {
        private List<Vector2> m_WayPoints = new LinkedList<Vector2>();
    //points to the current waypoint
    private ListIterator<Vector2> curWaypoint;
    private Vector2 cur = null;
    //flag to indicate if the path should be looped
    //(The last waypoint connected to the first)
    boolean m_bLooped;
    
    public Path() {
        m_bLooped = false;
    }

    //constructor for creating a path with initial random waypoints. MinX/Y
    //& MaxX/Y define the bounding box of the path.
    public Path(int NumWaypoints,
            float MinX,
            float MinY,
            float MaxX,
            float MaxY,
            boolean looped) {
        m_bLooped = looped;
        CreateRandomPath(NumWaypoints, MinX, MinY, MaxX, MaxY);
    }

    //returns the current waypoint
    public Vector2 CurrentWaypoint() {
        assert (curWaypoint != null);
        return cur;
    }

    //returns true if the end of the list has been reached
    public boolean Finished() {
        return !(curWaypoint.hasNext());
    }

    //moves the iterator on to the next waypoint in the list
    public void SetNextWaypoint() {
        assert (m_WayPoints.size() > 0);
        
        if (!curWaypoint.hasNext()) {
            if (m_bLooped) {
                curWaypoint = m_WayPoints.listIterator();
            }
        }
        if (curWaypoint.hasNext()) {
            cur = curWaypoint.next();
        }
    }

    //creates a random path which is bound by rectangle described by
    //the min/max values
    public List<Vector2> CreateRandomPath(int NumWaypoints,
            float MinX,
            float MinY,
            float MaxX,
            float MaxY) {
        m_WayPoints.clear();
        
        float midX = (MaxX + MinX) / 2.0f;
        float midY = (MaxY + MinY) / 2.0f;
        
        float smaller = min(midX, midY);
        
        float spacing = MathUtils.PI2 / NumWaypoints;
        
        for (int i = 0; i < NumWaypoints; ++i) {
            float RadialDist = MathUtils.random(smaller * 0.2f, smaller);
            
            Vector2 temp = new Vector2(RadialDist, 0.0f);
            
            VectorUtilities.vec2RotateAroundOrigin(temp, i * spacing);
            
            temp.x += midX;
            temp.y += midY;
            
            m_WayPoints.add(temp);
            
        }
        
        curWaypoint = m_WayPoints.listIterator();
        if (curWaypoint.hasNext()) {
            cur = curWaypoint.next();
        }
        
        return m_WayPoints;
    }
    
    public void LoopOn() {
        m_bLooped = true;
    }

    public void LoopOff() {
        m_bLooped = false;
    }

    //adds a waypoint to the end of the path
  /*
    void AddWayPoint(Vector2 new_point) {
    m_WayPoints.add(new_point);
    } */
    //methods for setting the path with either another Path or a list of vectors
    public void Set(List<Vector2> new_path) {
        m_WayPoints = new_path;
        curWaypoint = m_WayPoints.listIterator();
        cur = curWaypoint.next();
    }

    public void Set(Path path) {
        Set(path.GetPath());
    }
    
    public void Clear() {
        m_WayPoints.clear();
    }
    
    public List<Vector2> GetPath() {
        return m_WayPoints;
    }
}
