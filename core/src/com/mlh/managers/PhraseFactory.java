/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mlh.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.mlh.models.Phrase;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.badlogic.gdx.math.MathUtils.random;

/**
 *
 * @author michael
 */
public class PhraseFactory {

    // <editor-fold defaultstate="collapsed" desc="fields">
    private final static int HOMEROW = 10;
    private final static int QROW = 20;
    private final static int ZROW = 30;
    private final static int TOPROW = 42;
    private final static int SHIFTTOPROW = 54;
    private final static int SHIFTEXTRA = 57;

    private final static char[] VALID = {
        'A', 'S', 'D', 'F', 'G', ';', 'L', 'K', 'J', 'H', // home row
        'Q', 'W', 'E', 'R', 'T', 'P', 'O', 'I', 'U', 'Y', // q row
        'Z', 'X', 'C', 'V', 'B', '/', '.', ',', 'M', 'N', // z row
        '0', '1', '2', '3', '4', '5', '6', '+', '-', '0', '9', '8', '7', // top row
        '!', '@', '#', '$', '%', '^', '^', '+', '_', ')', '(', '*', '&', // shift top row
        '<', '>', '?' // shift extra
    };

    private static List<String> phraseList;

    private static List<String> top100words;
    private static List<String> homerowWords;
    private static List<String> homerowWords_n;
    private static List<String> homerowWords_b;
    private static List<String> homerowWords_c;
    private static List<String> homerowWords_e;
    private static List<String> homerowWords_i;
    private static List<String> homerowWords_m;
    private static List<String> homerowWords_o;
    private static List<String> homerowWords_p;
    private static List<String> homerowWords_q;
    private static List<String> homerowWords_r;
    private static List<String> homerowWords_t;
    private static List<String> homerowWords_u;
    private static List<String> homerowWords_v;
    private static List<String> homerowWords_w;
    private static List<String> homerowWords_x;
    private static List<String> homerowWords_y;
    private static List<String> homerowWords_z;
    private static List<String> badWords;
// </editor-fold>

    static {
        phraseList = new ArrayList<String>();
        // todo deal with file not found
        try {
            load();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PhraseFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static Phrase randomPhrase(int length) {
        phrasePacker(generateRandomPhrase(length, 0, VALID.length - 1));
        return getPhrase();
    }

    public static Phrase homerow(int length) {
        phrasePacker(generateRandomPhrase(length, 0, HOMEROW - 1));
        return getPhrase();
    }

    public static Phrase homerowPlusQrow(int length) {
        phrasePacker(generateRandomPhrase(length, 0, QROW - 1));
        return getPhrase();
    }

    public static Phrase homerowPlusQZrow(int length) {
        phrasePacker(generateRandomPhrase(length, 0, ZROW - 1));
        return getPhrase();
    }

    public static Phrase homerowPlusQZToprow(int length) {
        phrasePacker(generateRandomPhrase(length, 0, TOPROW - 1));
        return getPhrase();
    }

    public static Phrase phraseTop100word() {
        phrasePacker(generatePhraseString(0, top100words));
        return getPhrase();
    }

    public static Phrase phrase(int length, String letters) {
        return generatePhrase(length, letters, 1);
    }

    // todo<optimize> this might need to be optimized
    // quick and it works but should find a way to do this without
    // creating a new list for each call
    public static Phrase generatePhrase(int length, String letters, int wordCount) {
        // process letters
        List<List<String>> phraseLists = new ArrayList<List<String>>();
        phraseLists.add(homerowWords);
        letters = letters.toUpperCase();

        if (!letters.isEmpty()) {
            for (int i = 0; i < letters.length(); i++) {
                switch (letters.charAt(i)) {
                    case 'Q':
                        phraseLists.add(homerowWords_q);
                        break;
                    case 'W':
                        phraseLists.add(homerowWords_w);
                        break;
                    case 'E':
                        phraseLists.add(homerowWords_e);
                        break;
                    case 'R':
                        phraseLists.add(homerowWords_r);
                        break;
                    case 'T':
                        phraseLists.add(homerowWords_t);
                        break;
                    case 'Y':
                        phraseLists.add(homerowWords_y);
                        break;
                    case 'U':
                        phraseLists.add(homerowWords_u);
                        break;
                    case 'I':
                        phraseLists.add(homerowWords_i);
                        break;
                    case 'O':
                        phraseLists.add(homerowWords_o);
                        break;
                    case 'P':
                        phraseLists.add(homerowWords_p);
                        break;
                    case 'Z':
                        phraseLists.add(homerowWords_z);
                        break;
                    case 'X':
                        phraseLists.add(homerowWords_x);
                        break;
                    case 'C':
                        phraseLists.add(homerowWords_c);
                        break;
                    case 'V':
                        phraseLists.add(homerowWords_v);
                        break;
                    case 'B':
                        phraseLists.add(homerowWords_b);
                        break;
                    case 'N':
                        phraseLists.add(homerowWords_n);
                        break;
                    case 'M':
                        phraseLists.add(homerowWords_m);
                        break;
                }
            }
        }
       
        StringBuilder result = new StringBuilder();
        for (int i = 1; i <= wordCount; i++) {
            result.append(
                    generatePhraseString(
                            length, 
                            phraseLists.get(
                                    random(0, phraseLists.size() - 1))));
            if (i != wordCount) {
                result.append(" ");
            }
        }
        phrasePacker(result);
        return getPhrase();
    }

    private static String generatePhraseString(int length, List<String> wordList) {
        // no word short than 3 and 0 means any size word
        // some list do not contain words short than 3
        // this would lead to endless loop
        if (length < 3) {
            length = 3;
        } else if (length == 0){
            length = 255;
        }
        String result = wordList.get(random(0, wordList.size() - 1));
        if (result.length() > length || length == 0) {
            return generatePhraseString(length, wordList);
        }

        if (badWords.contains(result)) {
            return generatePhraseString(length, wordList);
        }

        return result;
    }

    // todo use properties
    private static void load() throws FileNotFoundException {
        top100words = loadPhraseList("core/assets/phrases/top100words.txt");
        homerowWords = loadPhraseList("core/assets/phrases/homerow.txt");
        homerowWords_n = loadPhraseList("core/assets/phrases/homerow+n.txt");
        homerowWords_b = loadPhraseList("core/assets/phrases/homerow+b.txt");
        homerowWords_c = loadPhraseList("core/assets/phrases/homerow+c.txt");
        homerowWords_e = loadPhraseList("core/assets/phrases/homerow+e.txt");
        homerowWords_i = loadPhraseList("core/assets/phrases/homerow+i.txt");
        homerowWords_m = loadPhraseList("core/assets/phrases/homerow+m.txt");
        homerowWords_o = loadPhraseList("core/assets/phrases/homerow+o.txt");
        homerowWords_p = loadPhraseList("core/assets/phrases/homerow+p.txt");
        homerowWords_q = loadPhraseList("core/assets/phrases/homerow+q.txt");
        homerowWords_r = loadPhraseList("core/assets/phrases/homerow+r.txt");
        homerowWords_t = loadPhraseList("core/assets/phrases/homerow+t.txt");
        homerowWords_u = loadPhraseList("core/assets/phrases/homerow+u.txt");
        homerowWords_v = loadPhraseList("core/assets/phrases/homerow+v.txt");
        homerowWords_w = loadPhraseList("core/assets/phrases/homerow+w.txt");
        homerowWords_x = loadPhraseList("core/assets/phrases/homerow+x.txt");
        homerowWords_y = loadPhraseList("core/assets/phrases/homerow+y.txt");
        homerowWords_z = loadPhraseList("core/assets/phrases/homerow+z.txt");
        List<String> phoneticAlphabet = loadPhraseList("core/assets/phrases/phoneticAlphabet.txt");
        badWords = loadPhraseList("core/assets/phrases/badwords.txt");
    }

    private static List<String> loadPhraseList(String filePath) {
        List<String> list = new ArrayList<String>();
            FileHandle handle = Gdx.files.internal(filePath);
            Scanner s = new Scanner(handle.read());
            while (s.hasNext()) {
                list.add(s.next());
            }
            s.close();
        return list;
    }

    private static String generateRandomPhrase(int length, int start, int end) {
        StringBuilder result = new StringBuilder();
        for (int i = start; i < length; i++) {
            result.append(VALID[random(start, end)]);
        }
        
        if (badWords.contains(result.toString())) {
            return generateRandomPhrase(length, start, end);
        }
        return result.toString();
    }

    // todo this is here for future features
    private static void phrasePacker(StringBuilder phrase) {
        phraseList.add(phrase.toString());
    }

    // todo this is here for future features
    private static void phrasePacker(String phrase) {
        phraseList.add(phrase);
    }

    private static Phrase getPhrase() {
        Phrase phrase = new Phrase(phraseList);
        phraseList.clear();
        return phrase;
    }
}
