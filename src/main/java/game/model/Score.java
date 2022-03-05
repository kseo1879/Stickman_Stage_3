package game.model;

import java.util.HashMap;
import java.util.Map;

/**
 * This class will keep of the score of the current number of level added to the game engine
 */
public class Score {

    /**
     * This score is the score when the hero kills an enemy
     */
    private static int KILL_ENEMy_SCORE = 100;

    /**
     * This is the map for the (Key)currentLevel ID and the (Value)score
     */
    private Map<Integer, Integer> score;

    /**
     * This will construct the score class
     */
    public Score(){
        score = new HashMap<>();
    }

    /**
     * This initialize will happen in the GameEngine Class
     */
    public void initialize(int levelIndex){
        score.put(levelIndex,0);
    }

    /**
     * When this is called it will add point to the current level
     */
    public void enemyIsKilled(int levelIndex){
        int temp = score.get(levelIndex);
        score.put(levelIndex, temp + KILL_ENEMy_SCORE);
    }

    /**
     * The score will be deducted when the target time has passed form the gameEngine duration
     */
    public void updateNegScore(int levelIndex, int negativeScore){
        int temp = score.get(levelIndex) + negativeScore;
        score.put(levelIndex, temp);
    }

    /**
     * Returns the current score of the current class to display in the runner class
     */
    public int getCurrentScore(int levelIndex){
        if(score.get(levelIndex) < 0){
            return 0;
        }else{
            return score.get(levelIndex);
        }
    }

    /**
     * This will reset the score to zero
     */
    public void resetScore(int levelIndex){
        score.put(levelIndex, 0);
    }

    /**
     * This will load the score when the loading is called.
     */
    public void loadScore(int levelIndex, int score){
        this.score.remove(levelIndex);
        this.score.put(levelIndex,score);
    }

}
