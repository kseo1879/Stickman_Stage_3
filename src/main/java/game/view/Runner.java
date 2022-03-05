package game.view;

import game.model.Block;
import game.model.Entity;
import game.model.GameEngine;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

class Runner {

    private GameEngine model;
    private Pane pane;
    private List<EntityView> entityViews;
    private BackgroundDrawer backgroundDrawer;
    private ImageView[] health = new ImageView[3];
    private Text lives;
    private Timeline timeline;
    private double xViewportOffset = 0.0;
    private double width;
    private double height;
    Text t = null;
    private Instant start;
    private java.time.Duration interval;
    private Text[] scoreText;
    private Text[] saveLoadText;
    private Text[] LoadText;
    private int tickCount = 0;


    Runner(GameEngine model, Pane pane, double width, double height) {
        this.model = model;
        this.pane = pane;
        this.width = width;
        this.height = height;
        this.entityViews = new ArrayList<>();
        this.health[0] = new ImageView(new Image("heart.png"));
        this.health[1] = new ImageView(new Image("heart.png"));
        this.health[2] = new ImageView(new Image("heart.png"));
        for (int i = health.length - 1; i >= 0; i--) {
            this.health[i].setFitHeight(30);
            this.health[i].setFitWidth(30);
            this.health[i].setY(10);
            this.health[i].setX(width - 40 - i * 40);
        }
        this.lives = new Text(10, 20, "lives remaining: " + model.getLives());
        this.lives.setFont(Font.font ("Chalkboard SE", FontPosture.ITALIC, 20));
        this.lives.setFill(Paint.valueOf("BLACK"));
        this.lives.setX(width -  this.lives.getLayoutBounds().getWidth() - 150);
        this.lives.setY(30);
        this.pane.getChildren().add(lives);
        this.backgroundDrawer = new ParallaxBackground();
        this.backgroundDrawer.draw(model, pane);
        scoreText = new Text[4];
        saveLoadText = new Text[1];
        LoadText = new Text[1];
        addHealth();
    }

    void run() {
        timeline = new Timeline(new KeyFrame(Duration.millis(17),
                t -> this.draw()));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    /**
     * This is a seperate timeline when Game engine's next level is called
     * When called, it will be seperated from the draw() method and used to display next level with score achieved
     * by the player.
     */
    void secondTimeLine(){
        start = Instant.now();
        timeline = new Timeline(new KeyFrame(Duration.millis(17),
                t -> this.secondTimeLineDraw()));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    void secondTimeLineDraw(){
        pane.getChildren().remove(saveLoadText[0]);
        pane.getChildren().remove(LoadText[0]);
        tickCount = 300;
        interval = java.time.Duration.between(start, Instant.now());
        if((int) interval.getSeconds() == 3){
            pane.getChildren().remove(t);
            t = drawScreen("Your Score was: " + model.getCurrentScore(model.getCurrentLevelId()));
            timeline.play();

        }else if((int) interval.getSeconds() == 6){
            pane.getChildren().remove(t);
            t = drawScreen("Next Level");
            timeline.play();
        }else if((int) interval.getSeconds() == 8){
            pane.getChildren().remove(t);
            timeline.stop();
            run();
            model.movingNext(false);
            model.updateCurrentLevelId();
            model.startLevel();
            drawLives();
            addHealth();
            pane.getChildren().add(lives);
            return;
        }

    }
    /**
     * This is another seperate timeline from run and secondtimeline
     * This is called when the game is called.
     * It will call endString() method which will display the score achived by the player
     */
    void thirdTimeLine(){
        start = Instant.now();
        timeline = new Timeline(new KeyFrame(Duration.millis(17),
                t -> this.thirdTimeLineDraw()));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }


    void thirdTimeLineDraw(){
        pane.getChildren().remove(saveLoadText[0]);
        pane.getChildren().remove(LoadText[0]);
        tickCount = 300;
        interval = java.time.Duration.between(start, Instant.now());
        if((int) interval.getSeconds() == 3){
            pane.getChildren().remove(t);
            String s = endStringGen();
            t = drawScreen(s);
        }

    }

    private void draw() {
        model.tick();

        if (model.isFinished()) {
            model.movingNext(true);
            if(model.hasNextLevel()){
                removeScore();
                t = drawScreen("Congratulations!\nYou Won in " + model.getDuration() + "s and\nhad "
                        + model.getLives() + " lives remaining!");
                secondTimeLine();
                return;
            }else{
                t = drawScreen("You Are A Winner!\n Congratulations!\nYou Won in " + model.getDuration() + "s and\nhad "
                        + model.getLives() + " lives remaining!");
                removeScore();
                thirdTimeLine();
                return;
            }
        } else if(model.gameOver()) {
            removeScore();
            drawScreen("You lose!");
            return;
        }

        drawLives();
        updateScore();
        updateHealth();
        updateload();
        updateSave();


        List<Entity> entities = model.getCurrentLevel().getEntities();

        for (EntityView entityView: entityViews) {
            entityView.markForDelete();
        }

        double heroXPos = model.getCurrentLevel().getHeroX();
        heroXPos -= xViewportOffset;

        if (heroXPos < GameWindow.getViewportMargin()) {
            if (xViewportOffset >= 0) { // Don't go further left than the start of the level
                xViewportOffset -= GameWindow.getViewportMargin() - heroXPos;
                if (xViewportOffset < 0) {
                    xViewportOffset = 0;
                }
            }
        } else if (heroXPos > width -  GameWindow.getViewportMargin()) {
            xViewportOffset += heroXPos - (width - GameWindow.getViewportMargin());
        }

        backgroundDrawer.update(xViewportOffset);

        for (Entity entity: entities) {
            boolean notFound = true;
            for (EntityView view: entityViews) {
                if (view.matchesEntity(entity)) {
                    notFound = false;
                    view.update(xViewportOffset);
                    break;
                }
            }
            if (notFound) {
                EntityView entityView = new EntityViewImpl(entity);
                entityViews.add(entityView);
                pane.getChildren().add(entityView.getNode());
            }
        }

        for (EntityView entityView: entityViews) {
            if (entityView.isMarkedForDelete()) {
                pane.getChildren().remove(entityView.getNode());
            }
        }
        entityViews.removeIf(EntityView::isMarkedForDelete);

    }

    /**
     * Draws the finish screen with the specified message in the center
     * @param message The message to be displayed in the center
     */

    private void updateload(){
        this.pane.getChildren().remove(LoadText[0]);
        if(model.getLoad() && model.lastCalled() == 2){
            if(model.getSave()){
                this.pane.getChildren().remove(saveLoadText[0]);
                model.setSave(false);
                tickCount = 0;
            }
            if(tickCount < 180){
                LoadText[0] = new Text(10, 30, "LEVEL" + model.getCurrentLevelId() + " LOADED");
                LoadText[0].setFont(Font.font ("Chalkboard SE", FontPosture.ITALIC, 20));
                LoadText[0].setFill(Paint.valueOf("BLACK"));
                LoadText[0].setX(410);
                LoadText[0].setY(30);
                this.pane.getChildren().add(LoadText[0]);
                tickCount ++;
            }else{
                tickCount = 0;
                this.pane.getChildren().remove(LoadText[0]);
                model.setLoad(false);
            }
        }
    }

    private void updateSave(){
        this.pane.getChildren().remove(saveLoadText[0]);
        if(model.getSave() && model.lastCalled() == 1){
            if(model.getLoad()){
                this.pane.getChildren().remove(LoadText[0]);
                model.setLoad(false);
                tickCount = 0;
            }
            if(tickCount < 180){
                saveLoadText[0] = new Text(10, 30, "LEVEL" + model.getCurrentLevelId() + " SAVED");
                saveLoadText[0].setFont(Font.font ("Chalkboard SE", FontPosture.ITALIC, 20));
                saveLoadText[0].setFill(Paint.valueOf("BLACK"));
                saveLoadText[0].setX(410);
                saveLoadText[0].setY(30);
                this.pane.getChildren().add(saveLoadText[0]);
                tickCount ++;
            }else {
                tickCount = 0;
                this.pane.getChildren().remove(saveLoadText[0]);
                model.setSave(false);
            }
        }
    }

    private void updateHealth(){
        for(int i = 0; i < 3; i ++){
            pane.getChildren().remove(health[i]);
        }
        for(int i = 0; i < model.getCurrentLevel().getHeroHealth(); i ++){
            pane.getChildren().add(health[i]);
        }
        if (model.getCurrentLevel().getHeroHealth() == 0) {
            pane.getChildren().remove(health[0]);
            addHealth();
            model.resetCurrentLevel();
        }
    }

    private Text drawScreen(String message) {
        for (EntityView entityView: entityViews) {
            pane.getChildren().remove(entityView.getNode());
        }
        for (ImageView life: this.health) {
            pane.getChildren().remove(life);
        }
        pane.getChildren().remove(lives);
        Text t = new Text(10, 20, message);
        t.setFont(Font.font ("Chalkboard SE", FontPosture.ITALIC, 60));
        t.setFill(Paint.valueOf("BLACK"));
        t.setLayoutX((width - t.getLayoutBounds().getWidth()) / 2.0);
        t.setLayoutY((height - t.getLayoutBounds().getHeight()) / 2.0);
        t.setTextAlignment(TextAlignment.CENTER);
        pane.getChildren().add(t);
        timeline.stop();
        return t;
    }

    /**
     * Adds the hero's health to the view
     */
    private void addHealth() {
        for (ImageView life: health) {
            pane.getChildren().add(life);
        }
    }

    /**
     * Adds the number of lives the hero has to the view
     */
    private void drawLives() {
        this.lives.setText("lives remaining: " + model.getLives());
    }

    /**
     * UPdate the score on the screen
     */
    private void updateScore(){
        this.pane.getChildren().remove(scoreText[0]);
        this.pane.getChildren().remove(scoreText[1]);
        this.pane.getChildren().remove(scoreText[2]);
        this.pane.getChildren().remove(scoreText[3]);
        for(int i = 1 ; i < model.getCurrentLevelId(); i ++){
            if(model.getCurrentLevelId() != 1){
                scoreText[i] = new Text(10, 30, "Level" + (model.getCurrentLevelId() - i) + ": " + model.getCurrentScore(model.getCurrentLevelId() - i));
                scoreText[i].setFont(Font.font ("Chalkboard SE", FontPosture.ITALIC, 20));
                scoreText[i].setFill(Paint.valueOf("BLACK"));
                scoreText[i].setX(30);
                scoreText[i].setY(30 + ((20) * i));
                this.pane.getChildren().add(scoreText[i]);
            }
        }
        scoreText[0] = new Text(10, 30, "Level" + model.getCurrentLevelId() + ": " + model.getCurrentScore(model.getCurrentLevelId()));
        scoreText[0].setFont(Font.font ("Chalkboard SE", FontPosture.ITALIC, 20));
        scoreText[0].setFill(Paint.valueOf("BLACK"));
        scoreText[0].setX(30);
        scoreText[0].setY(30);
        this.pane.getChildren().add(scoreText[0]);
    }

    /**
     * Remove the score text for update
     */
    public void removeScore(){
        for(int i = 0; i < model.getCurrentLevelId(); i ++){
            pane.getChildren().remove(scoreText[i]);
        }
    }

    /**
     * Called once when the game is over
     */
    public String endStringGen(){
        String s = "!!SCORE BOARD!!\n";
        for(int i = 0; i < model.getCurrentLevelId() ; i ++){
            s += "Level" + (i+1) + ": " + model.getCurrentScore(i + 1) + "\n";
        }
        return s;
    }

}
