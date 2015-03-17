/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mlh.managers;

import com.badlogic.gdx.math.Intersector;
import com.mlh.interfaces.ICollidable;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author michael
 */
public class CollisionManager {

    private final List<ICollidable> collidables;

    public CollisionManager(ArrayList<ICollidable> collidables) {
        this.collidables = collidables;
    }

    public void update(float dt) {
        // collision checking for a collection of objects with a collection of rectangles
        for (int i = 0; i < collidables.size(); i++) {                                      // first collection
            for (int m = i + 1; m < collidables.size(); m++) {                              // second collection
                breakpoint:                                                                 // break to here if we have a collision
                for (int ir = 0; ir < collidables.get(i).getCollisionBoxes().length; ir++) {                           // first set of rectangles
                    for (int mr = 0; mr < collidables.get(m).getCollisionBoxes().length; mr++) {                       // second set of rectagles
                        boolean collision = Intersector.overlaps(
                                collidables.get(i).getCollisionBoxes()[ir],
                                collidables.get(m).getCollisionBoxes()[mr]
                        );
                        if (collision) {
                            collidables.get(i).collision(ir, collidables.get(m));           // tell i box ir collided with m
                            collidables.get(m).collision(mr, collidables.get(i));           // tell m box mr collided with i
                            break breakpoint;
                        }
                    }
                }
            }
        }
    }
}
