package com.example.team08.tagvirtualgraffiti;

import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class GameplayActivityTest {
    final int ROCK = 1;
    final int PAPER = 2;
    final int SCISSOR = 3;

    @Test
    public void testLoseRockToPaper(){
        int user = ROCK;
        int opponent = PAPER;
        GameplayActivity play = new GameplayActivity();
        boolean result = play.doILose(user, opponent);
        assertTrue(result);
    }

    @Test
    public void testLosePaperToScissors(){
        int user = PAPER;
        int opponent = SCISSOR;
        GameplayActivity play = new GameplayActivity();
        boolean result = play.doILose(user, opponent);
        assertTrue(result);
    }

    @Test
    public void testLoseScissorsToRock(){
        int user = SCISSOR;
        int opponent = ROCK;
        GameplayActivity play = new GameplayActivity();
        boolean result = play.doILose(user, opponent);
        assertTrue(result);
    }

    @Test
    public void testTieRock(){
        int user = ROCK;
        int opponent = ROCK;
        GameplayActivity play = new GameplayActivity();
        boolean result = play.doILose(user, opponent);
        assertFalse(result);
    }

    @Test
    public void testTiePaper(){
        int user = PAPER;
        int opponent = PAPER;
        GameplayActivity play = new GameplayActivity();
        boolean result = play.doILose(user, opponent);
        assertFalse(result);
    }

    @Test
    public void testTieScissor(){
        int user = SCISSOR;
        int opponent = SCISSOR;
        GameplayActivity play = new GameplayActivity();
        boolean result = play.doILose(user, opponent);
        assertFalse(result);
    }
}
