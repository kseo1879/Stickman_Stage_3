package game.model;

import java.io.Serializable;

public interface EnemyFactoryInterface extends Serializable {
    Enemy makeEnemy(String type, double xPos, double spawnHeight);
}
