package game.model;

import java.io.Serializable;

public interface EntityFactoryInterface extends Serializable {
    Entity makeEntity(String type, double xPos, double yPos);
}
