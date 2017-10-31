package tictactoe.unal.edu.co.androidtic_tac_toe.online.entities;

/**
 * Created by hnino on 30/10/2017.
 */

public class Room {

    public String name;
    public String firstPlayer;
    public String secondPlayer;

    public Room() {
    }

    public Room(String name, String firstPlayer) {
        this.name = name;
        this.firstPlayer = firstPlayer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstPlayer() {
        return firstPlayer;
    }

    public void setFirstPlayer(String firstPlayer) {
        this.firstPlayer = firstPlayer;
    }

    public String getSecondPlayer() {
        return secondPlayer;
    }

    public void setSecondPlayer(String secondPlayer) {
        this.secondPlayer = secondPlayer;
    }
}
