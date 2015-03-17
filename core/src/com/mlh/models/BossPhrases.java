/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mlh.models;

import com.badlogic.gdx.math.MathUtils;

import java.util.ArrayList;

/**
 *
 * @author michael
 */
public class BossPhrases {

    private static final ArrayList<ArrayList<String>> bossPhrases;
    public static ArrayList<String> getRandomPhrase(){
        return bossPhrases.get(MathUtils.random(0, bossPhrases.size() - 1));
    }

    static {
        bossPhrases = new ArrayList<ArrayList<String>>();
        ArrayList<String> phraseList;

        // phrase 1
        phraseList = new ArrayList<String>();
        phraseList.add("You always pass failure");
        phraseList.add("on the way to success.");
        phraseList.add("that's why pencils have erasers.");
        bossPhrases.add(phraseList);

        // phrase 2
        phraseList = new ArrayList<String>();
        phraseList.add("Don't ever let somebody tell you");
        phraseList.add("you cant do something.");
        phraseList.add("You got a dream?");
        phraseList.add("You got to protect it.");
        phraseList.add("If people can't do something themselves");
        phraseList.add("they gonna tell you, you can't do it.");
        phraseList.add("If you want something, go get it. Period.");
        bossPhrases.add(phraseList);

        // phrase 3
        phraseList = new ArrayList<String>();
        phraseList.add("Never give in.");
        phraseList.add("Never, never, never.");
        phraseList.add("In nothing, great or small");
        phraseList.add("large or petty");
        phraseList.add("never give in");
        phraseList.add("except on the conviction of honour");
        phraseList.add("and good sense.");
        phraseList.add("Never yield to force");
        phraseList.add("never yield to the apparently");
        phraseList.add("overwhelming might of the enemy.");
        bossPhrases.add(phraseList);

        // phrase 4
        phraseList = new ArrayList<String>();
        phraseList.add("This kid's gonna be the");
        phraseList.add("best kid in the world.");
        phraseList.add("This kid's gonna be somebody");
        phraseList.add("better than somebody that I ever knew.");
        phraseList.add("What we do in life, echoes into eternity.");
        bossPhrases.add(phraseList);

        // phrase 5
        phraseList = new ArrayList<String>();
        phraseList.add("Don't let what you can't do");
        phraseList.add("stop you from doing what you can do.");
        phraseList.add("We all can dance");
        phraseList.add("when we find music we love.");
        bossPhrases.add(phraseList);

        // phrase 6
        phraseList = new ArrayList<String>();
        phraseList.add("The more you give away");
        phraseList.add("the more happy you become.");
        phraseList.add("I think I can. I know I can.");
        bossPhrases.add(phraseList);

        // phrase 7
        phraseList = new ArrayList<String>();
        phraseList.add("Whether you think you can");
        phraseList.add("or you think you can't");
        phraseList.add("you're right.");
        bossPhrases.add(phraseList);

        // phrase 8
        phraseList = new ArrayList<String>();
        phraseList.add("Anyone who stops learning is old");
        phraseList.add("whether at twenty or eighty.");
        phraseList.add("Anyone who keeps learning");
        phraseList.add("stays young.");
        bossPhrases.add(phraseList);

        // phrase 9
        phraseList = new ArrayList<String>();
        phraseList.add("Failure is simply an opportunity");
        phraseList.add("to learn.");
        phraseList.add("We learn best");
        phraseList.add("when we fail.");
        bossPhrases.add(phraseList);

        // phrase 10
        phraseList = new ArrayList<String>();
        phraseList.add("You can't build a reputation");
        phraseList.add("on what you are going to do.");
        phraseList.add("Don't find fault, find a remedy.");
        phraseList.add("Anybody can complain.");
        bossPhrases.add(phraseList);

        // phrase 11
        phraseList = new ArrayList<String>();
        phraseList.add("The only real mistake");
        phraseList.add("is the one from which");
        phraseList.add("we learn nothing.");
        bossPhrases.add(phraseList);

        // phrase 12
        phraseList = new ArrayList<String>();
        phraseList.add("The man who thinks he can");
        phraseList.add("and the man who thinks he can't");
        phraseList.add("are both right.");
        phraseList.add("Which one are you?");
        bossPhrases.add(phraseList);

        // phrase 13
        phraseList = new ArrayList<String>();
        phraseList.add("Vision without execution");
        phraseList.add("is just hallucination.");
        phraseList.add("Chop your own wood");
        phraseList.add("and it will warm you twice.");
        bossPhrases.add(phraseList);

        // phrase 14
        phraseList = new ArrayList<String>();
        phraseList.add("Coming together is the beginning.");
        phraseList.add("Keeping together is progress.");
        phraseList.add("Working together is success.");
        bossPhrases.add(phraseList);

        // phrase 15
        phraseList = new ArrayList<String>();
        phraseList.add("Quality means doing it right");
        phraseList.add("when no one is looking.");
        phraseList.add("Nothing is particularly hard");
        phraseList.add("if you divide it into small jobs.");
        bossPhrases.add(phraseList);

        // phrase 16
        phraseList = new ArrayList<String>();
        phraseList.add("One of the greatest discoveries");
        phraseList.add("a person can make is to find");
        phraseList.add("they can do what they were");
        phraseList.add("afraid they couldn't do.");
        bossPhrases.add(phraseList);

        // phrase 17
        phraseList = new ArrayList<String>();
        phraseList.add("Most people spend more time");
        phraseList.add("and energy going around problems");
        phraseList.add(" than in trying to solve them.");
        bossPhrases.add(phraseList);

        // phrase 18
        phraseList = new ArrayList<String>();
        phraseList.add("One of the greatest discoveries");
        phraseList.add("a person can make is to find");
        phraseList.add("they can do what they were");
        phraseList.add("afraid they couldn't do.");
        bossPhrases.add(phraseList);

        // phrase 19
        phraseList = new ArrayList<String>();
        phraseList.add("It's not what happens to you");
        phraseList.add("but how you react to it that matters.");
        phraseList.add("Being kind is never wasted.");
        bossPhrases.add(phraseList);

        // phrase 20
        phraseList = new ArrayList<String>();
        phraseList.add("When you know better you do better.");
        phraseList.add("Do what you can");
        phraseList.add("with what you have");
        phraseList.add("where you are.");
        bossPhrases.add(phraseList);

        // phrase 21
        phraseList = new ArrayList<String>();
        phraseList.add("Never waste a minute thinking");
        phraseList.add("of anyone you don't like.");
        phraseList.add("Only surround yourself with");
        phraseList.add("people who will lift you higher.");
        bossPhrases.add(phraseList);

        // phrase 22
        phraseList = new ArrayList<String>();
        phraseList.add("Why fit in");
        phraseList.add("when you were born to stand out?");
        phraseList.add("Anything is possible.");
        phraseList.add("Anything can be.");
        bossPhrases.add(phraseList);

        // phrase 22
        phraseList = new ArrayList<String>();
        phraseList.add("There is a voice inside of you");
        phraseList.add("that whispers all day long");
        phraseList.add("I feel this is right for me");
        phraseList.add("I know that this is wrong.");
        phraseList.add("Listen!");
        bossPhrases.add(phraseList);

        // phrase 23
        phraseList = new ArrayList<String>();
        phraseList.add("The more that you read");
        phraseList.add("the more things you will know.");
        phraseList.add("The more that you learn");
        phraseList.add("the more places you'll go.");
        bossPhrases.add(phraseList);

        // phrase 24
        phraseList = new ArrayList<String>();
        phraseList.add("You are where you are");
        phraseList.add("because of who you are");
        phraseList.add("and if you want to");
        phraseList.add("get somewhere else");
        phraseList.add("you have to change");
        phraseList.add("who you are.");
        bossPhrases.add(phraseList);

    }

}
