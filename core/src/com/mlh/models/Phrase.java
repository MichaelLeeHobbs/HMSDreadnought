/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mlh.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author michael
 */
public class Phrase {

    private String[] words;
    private List<String> sentences;
    private int typed;

    private int currentSentence;
    private int currentWord;
    private boolean done;
    public static final char NULL_CHAR = 0;
    public static final char SPACE_CHAR = 32;
    public static final String SPACE_STR = " ";
    public static final char ESCAPE_CHAR = 27;
    public static final char BACKSPACE_CHAR = 8;

    public static final int NOT_VALID_CHAR = 0;
    public static final int VALID_CHAR = 1;
    public static final int PHRASE_DONE = 2;
    public static final int WORD_DONE = 3;
    public static final int LINE_DONE = 4;

    public Phrase(Phrase phrase) {
        this(phrase.getSentences());
    }

    public Phrase(List<String> phraseList) {
        sentences = new ArrayList<String>(phraseList);
        reset();
    }

    public final void reset() {
        done = false;
        typed = 0;
        currentSentence = 0;
        currentWord = 0;
        words = generateWords(sentences.get(currentSentence).toLowerCase());
    }

    private boolean nextLine() {
        currentSentence++;
        if (sentences.size() > currentSentence) {
            typed = 0;
            words = generateWords(sentences.get(currentSentence).toLowerCase());
            return true;
        }
        return false;
    }

    public void clearTyped() {
        typed = 0;
    }

    private static String[] generateWords(String sentence) {
        return new String[]{sentence};
        
        //return sentence.split(SPACE_STR);
    }

    public int checkChar(char c) {
        int result = NOT_VALID_CHAR;
        if (currentWord < words.length
                && typed < words[currentWord].length()
                && words[currentWord].charAt(typed) == c) {
            
            result = VALID_CHAR;
            typed++;
            //if (peek() == SPACE_CHAR || peek() == NULL_CHAR) {
            if (peek() == NULL_CHAR) {
                result = WORD_DONE;
                if (currentWord < words.length -1){
                    currentWord++;
                } else {
                    result = LINE_DONE;
                    if (!nextLine()) {
                        result = PHRASE_DONE;
                        done = true;
                    }
                }
            }
        }

        return result;
    }

    private char peek() {
        if (words[currentWord].length() > typed) {
            return words[currentWord].charAt(typed);
        }
        return NULL_CHAR;
    }

    public String[] getWords() {
        return words.clone();
    }

    public List<String> getSentences() {
        return Collections.unmodifiableList(sentences);
    }

    public int getTyped() {
        return typed;
    }

    public int getCurrentSentence() {
        return currentSentence;
    }

    public int getCurrentWord() {
        return currentWord;
    }

    public boolean isDone() {
        return done;
    }
    
    public char getFirstLetter(){
        return words[0].charAt(0);
    }
    
    public int getLength(){
        int length = 0;
        for (String s : sentences) {
            length += s.length();
        }
        return length;
    }
}
