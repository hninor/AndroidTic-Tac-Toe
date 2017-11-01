package tictactoe.unal.edu.co.androidtic_tac_toe.online.entities;

/**
 * Created by hnino on 30/10/2017.
 */

public class Room {

    private String name;
    private String firstPlayer;
    private String secondPlayer;
    private boolean activo;
    private int lastMovement;

    public Room() {
    }

    public Room(String name, String firstPlayer) {
        this.name = name;
        this.firstPlayer = firstPlayer;
    }

    public Room(String name, String firstPlayer, boolean activo) {
        this.name = name;
        this.firstPlayer = firstPlayer;
        this.activo = activo;
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


    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public int getLastMovement() {
        return lastMovement;
    }

    public void setLastMovement(int lastMovement) {
        this.lastMovement = lastMovement;
    }
}
