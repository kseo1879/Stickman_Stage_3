package game.model;

import java.time.Duration;

public interface GameEngine {

    /**
     * Returns the current level
     * @return The current level
     */
    Level getCurrentLevel();

    /**
     * Starts the current level
     */
    void startLevel();

    // Hero inputs - boolean for success (possibly for sound feedback)
    boolean jump();
    boolean moveLeft();
    boolean moveRight();
    boolean stopMoving();

    void tick();

    /**
     * Resets the current level
     */
    void resetCurrentLevel();

    /**
     * Determines whether or not the current level is finished
     * @return True if the current level is finished, else false
     */
    boolean isFinished();

    /**
     * Returns the duration of the current level
     * @return The duration of the current level
     */
    int getDuration();

    /**
     * Determines whether or not the game is over i.e. the hero has no more lives left
     * @return True if the game is over, else false
     */
    boolean gameOver();

    /**
     * Returns the number of lives the hero has left
     * @return The number of lives the hero has left
     */
    int getLives();

    /**
     * Please refer to the GameEngineImplementation Class for furthur description of each method
     */
    boolean hasNextLevel();

    /**
     * Please refer to the GameEngineImplementation Class for furthur description of each method
     */
    void updateCurrentLevelId();

    /**
     * Please refer to the GameEngineImplementation Class for furthur description of each method
     */
    int getCurrentLevelId();

    /**
     * Please refer to the GameEngineImplementation Class for furthur description of each method
     */
    int getCurrentScore(int currentLevelId);

    /**
     * Please refer to the GameEngineImplementation Class for furthur description of each method
     */
    void save();

    /**
     * Please refer to the GameEngineImplementation Class for furthur description of each method
     */
    void load();

    /**
     * Please refer to the GameEngineImplementation Class for furthur description of each method
     */
    void movingNext(boolean b);

    /**
     * Please refer to the GameEngineImplementation Class for furthur description of each method
     */
    boolean getSave();

    /**
     * Please refer to the GameEngineImplementation Class for furthur description of each method
     */
    void setSave(boolean b);

    /**
     * Please refer to the GameEngineImplementation Class for furthur description of each method
     */
    boolean getLoad();

    /**
     * Please refer to the GameEngineImplementation Class for furthur description of each method
     */
    void setLoad(boolean b);

    /**
     * Please refer to the GameEngineImplementation Class for furthur description of each method
     */
    int lastCalled();

}
