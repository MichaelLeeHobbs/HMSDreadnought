/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mlh.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author michael
 */
public class FontWriter {

    private static final BitmapFont font;
    private static final List<String> phraseList;
    private static final List<Vector2> phrasePos;
    private static final List<Color> phraseColor;

    static {
        phraseList = new ArrayList<String>();
        phrasePos = new ArrayList<Vector2>();
        phraseColor = new ArrayList<Color>();

        // set font
        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(
                Gdx.files.internal("core/assets/fonts/Hyperspace Bold.ttf")
        );
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 18;
        font = gen.generateFont(parameter);
    }

    public static void write(
            Vector2 position,
            String phrase,
            int nextPos) {
        String temp;
        float positionMod = 0;

        if (nextPos > 0) {
            temp = phrase.substring(0, nextPos);
            phraseList.add(temp);
            phrasePos.add(position.cpy());
            phraseColor.add(Color.GREEN);
            positionMod = font.getBounds(temp).width;
        }

        temp = phrase.substring(nextPos, phrase.length());
        phraseList.add(temp);
        position.x += positionMod;
        phrasePos.add(position.cpy());
        phraseColor.add(Color.WHITE);
        
    }

    public static void draw(SpriteBatch sb) {
        sb.begin();
        for (int i = 0; i < phraseList.size(); i++) {
            font.setColor(phraseColor.get(i));
            font.draw(sb, phraseList.get(i), phrasePos.get(i).x, phrasePos.get(i).y);
        }
        sb.end();
        phraseList.clear();
        phrasePos.clear();
        phraseColor.clear();
        
    }

    public static void dispose() {
        font.dispose();
    }

}
