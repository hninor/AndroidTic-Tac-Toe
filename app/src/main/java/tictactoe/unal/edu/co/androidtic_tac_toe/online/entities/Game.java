package tictactoe.unal.edu.co.androidtic_tac_toe.online.entities;

/**
 * Created by hnino on 31/10/2017.
 */

public class Game {

    int lastMovement;

    public Game() {
    }

    public Game(int lastMovement) {
        this.lastMovement = lastMovement;
    }

    public int getLastMovement() {
        return lastMovement;
    }

    public void setLastMovement(int lastMovement) {
        this.lastMovement = lastMovement;
    }
}
