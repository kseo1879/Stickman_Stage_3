package game.model;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class GameEngineImplementation implements GameEngine {

    /**
     * The height of the game engine
     */
    private double height;

    /**
     * The current level
     */
    private Level currentLevel;

    /**
     * Map of all the levels
     */
    private Map<Integer, Level> levels;

    /**
     * Used to create distinct level id's for each level
     */
    private int levelId;

    public static int maxLevelId = 3;

    /**
     * Level id of the current level
     */
    private int currentLevelId;

    /**
     * Json path to the level configuration file
     */
    private String jsonPath;

    /**
     * Used to keep track of how long it takes the user to complete the game
     */
    private Instant start;
    private Instant savedStart;

    /**
     * Used to keep track of how long it takes the user to complete the game
     */
    private Duration interval;

    /**
     * This duration is the saved interval for the currently saved level
     */
    private Duration SavedInterval;

    /**
     * The number of lives the hero has
     */
    private int lives;

    /**
     * This will keep track of the score of the current level
     */
    private Score score;

    /**
     * This integer is updated when the target time has passed.
     */
    private int negativeScore = 0;

    /**
     * This temperary negative score is used when the user loads the saved level more than once.
     */
    int tempNegativeScore = 0;

    /**
     * Saved Score of the current level
     */
    private int saveScore = 0;

    /**
     * Savedscore when the user loads the saved level more than twive
     */
    private int saveScoreDup = 0;

    /**
     * This is calculated based on the savedDuration
     */
    int addingTime = 0;

    /**
     * Saved Level from the user
     */
    private Level savedLevel;

    /**
     * This level is used when the user loads the level more than once.
     */
    private Level duplicateSaved;

    /**
     * This is the saved Level ID
     */
    private int saveLevelId;

    /**
     * Boolean movingNext is when the second or third timeline is called from the runner class
     */
    private boolean movingNext = false;

    /**
     * Lived of the current saved vertion
     */
    private int savedLives = 0;

    /**
     * Used when the loading function is called twice.
     */
    private int savedLivesDup = 0;

    /**
     * Boolean variable is the current state is being saved by the user
     */
    private boolean saving = false;

    /**
     * Boolean variable when the current sate is loading the level.
     */
    private boolean loading = false;

    /**
     * Last Called Int is when save() is last called by the user it will be 1
     * When the last called is load() the integer will change to 2
     */
    private int lastCalledInt = 0;

    /**
     * Creates the game engine using the specified json configuration file and height
     * @param height The height of the game engine's window
     */
    public GameEngineImplementation(double height) {
        this.height = height;
        this.levels = new HashMap<>();
        this.levelId = 1;
        this.currentLevelId = 1;
        this.lives = 3;
        this. score = new Score();
        createLevels();
        startLevel();
    }

    /**
     * this will set the jsonPath regarding to the level
     */
    public void JSONLevelString(int index){
        String level1 = "level_1.json";
        String level2 = "level_2.json";
        String level3 = "level_3.json";

        if(index ==1){
            this.jsonPath = level1;
        }else if(index ==2){
            this.jsonPath = level2;
        }else if(index ==3){
            this.jsonPath = level3;
        }
    }

    /**
     * Creates the levels associated with the json file
     */
    public void createLevels() {
        for(int i = 1 ; i <= maxLevelId ; i ++){
            JSONLevelString(levelId);
            LevelBuilder levelBuilder = new LevelBuilder(this.jsonPath);
            LevelDirector levelDirector = new LevelDirector(levelBuilder);
            levelDirector.buildLevel();
            this.levels.put(this.levelId, levelDirector.getLevel());
            score.initialize(levelId);
            levelId += 1;
        }
    }

    /**
     * When the user calls load for the previous level the updates that happend to the next levels will reset
     */
    public void resetNextLevels(int currentLevelId){
        for(int i = currentLevelId+1; i <= maxLevelId; i++){
            levels.remove(i);
            JSONLevelString(i);
            LevelBuilder levelBuilder = new LevelBuilder(this.jsonPath);
            LevelDirector levelDirector = new LevelDirector(levelBuilder);
            levelDirector.buildLevel();
            this.levels.put(i, levelDirector.getLevel());
            score.initialize(i);
            levelId += 1;
        }
    }

    /**
     * It will start the level
     */
    @Override
    public void startLevel() {
        this.currentLevel = levels.get(currentLevelId);
        start = Instant.now();
    }

    /**
     * It will duplicate the saved level to insure that user can call laoding twice
     */
    public void duplicateSavedLevel(){
        duplicateSaved = currentLevel.save();
    }



    @Override
    public Level getCurrentLevel() {
        return this.currentLevel;
    }

    @Override
    public boolean jump() {
        return this.currentLevel.jump();
    }

    @Override
    public boolean moveLeft() {
        return this.currentLevel.moveLeft();
    }

    @Override
    public boolean moveRight() {
        return this.currentLevel.moveRight();
    }

    @Override
    public boolean stopMoving() {
        return this.currentLevel.stopMoving();
    }

    /**
     * Updated functions are that whenn tick is called the duration is going to be calculated adding the addingtime
     * Addingtime as mentioned is the time added when the user loads the saved level it will add the duration
     * It will also update the score of the currentLevel in the Score Class
     */
    @Override
    public void tick() {
        this.currentLevel.tick();
        interval = Duration.between(start, Instant.now());
        if(this.currentLevel.enemyDead()){
            score.enemyIsKilled(this.currentLevelId);
        }
        if((int)interval.getSeconds() + addingTime > currentLevel.getTargetTime()){
            tempNegativeScore = (int)currentLevel.getTargetTime() - ((int)interval.getSeconds() + addingTime) ;
            if(tempNegativeScore != negativeScore){
                score.updateNegScore(currentLevelId, -1);
                negativeScore = tempNegativeScore;
            }
        }
    }


    @Override
    public void resetCurrentLevel() {
        this.lives--;
        if (this.lives == 0) {
            return;
        }
        JSONLevelString(currentLevelId);
        LevelBuilder levelBuilder = new LevelBuilder(this.jsonPath);
        LevelDirector levelDirector = new LevelDirector(levelBuilder);
        levelDirector.buildLevel();
        this.levels.put(this.currentLevelId, levelDirector.getLevel());
        startLevel();
        addingTime = 0;
        score.resetScore(currentLevelId);
    }

    @Override
    public boolean isFinished() {
        return currentLevel.isFinished();
    }

    @Override
    public int getDuration() {
        if(addingTime !=0){
            return addingTime + (int)interval.toSeconds();
        }else{
            return (int)interval.toSeconds();
        }
    }

    @Override
    public boolean gameOver() {
        return this.lives == 0;
    }

    @Override
    public int getLives() {
        return this.lives;
    }

    @Override
    public boolean hasNextLevel(){
        return currentLevelId < maxLevelId;
    }

    /**
     * Returns if the user is saving
     */
    @Override
    public boolean getSave() {
        return saving;
    }

    /**
     * This will be set the saving to falls from the Runner class after certain amount of time to insure that
     * screen doesn't print out saving function
     */
    @Override
    public void setSave(boolean b) {
        saving = b;
    }

    /**
     * Same methodology as getSave
     */
    @Override
    public boolean getLoad() {
        return loading;
    }

    /**
     * Same methodology as setSave
     */
    @Override
    public void setLoad(boolean b) {
        loading = b;
    }

    @Override
    public int lastCalled() {
        return lastCalledInt;
    }

    /**
     * Returns if the level is in transition or if the gameover is true
     */
    @Override
    public void movingNext(boolean b){
        movingNext = b;
    }


    /**
     * This will the called when the level changes.
     */
    @Override
    public void updateCurrentLevelId(){
        currentLevelId +=1;
        tempNegativeScore = 0;
        negativeScore = 0;
        addingTime = 0;
        savedStart = null;
    }

    /**
     * Returns the current score of the current level to the runner class to display them on the screen
     */
    @Override
    public int getCurrentScore(int currentLevelId){
        return score.getCurrentScore(currentLevelId);
    }

    @Override
    public int getCurrentLevelId(){
        return this.currentLevelId;
    }

    /**
     * The level is going to be saved using this function includeing the information that was in the current gaming
     * state such as lives, duration.
     */
    @Override
    public void save(){
        if(!movingNext){
            saving = true;
            lastCalledInt = 1;
            if(savedStart == null){
                SavedInterval = Duration.between(start, Instant.now());
                currentLevel.saveTime((int)SavedInterval.toSeconds());
            }else{
                SavedInterval = Duration.between(savedStart, Instant.now());
                int a = currentLevel.getTime() + (int)SavedInterval.toSeconds();
                currentLevel.saveTime(a);
            }
            this.savedLevel = currentLevel.save();
            this.saveLevelId = currentLevelId;
            savedLives = lives;
            saveScore = score.getCurrentScore(saveLevelId);
            duplicateSavedLevel();
            savedLivesDup = lives;
            saveScoreDup = saveScore;
        }
    }

    /**
     * This method will load the level based on the saved level
     */
    @Override
    public void load(){
        if(!movingNext){
            if(savedLevel == null && duplicateSaved !=null){
                savedLevel = duplicateSaved.save();
                savedLives = savedLivesDup;
                saveScore = saveScoreDup;
            }
            if(savedLevel != null){
                loading = true;
                lastCalledInt = 2;
                this.currentLevel = savedLevel;
                currentLevelId = saveLevelId;
                lives = savedLives;
                resetNextLevels(currentLevelId);
                levels.remove(saveLevelId);
                levels.put(saveLevelId, currentLevel);
                score.loadScore(saveLevelId,saveScore);
                if(addingTime > currentLevel.getTargetTime()){
                    negativeScore = 1;
                    tempNegativeScore = 0;
                }else{
                    negativeScore = 0;
                    tempNegativeScore = 0;
                }
                saveScore = 0;
                savedLives = 0;
                savedStart = Instant.now();
                startLevel();
                stopMoving();
                addingTime = getCurrentLevel().getTime();
            }
            savedLevel = null;
        }
    }
}

