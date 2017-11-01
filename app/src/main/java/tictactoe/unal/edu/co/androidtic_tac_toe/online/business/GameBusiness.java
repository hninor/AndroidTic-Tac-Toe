package tictactoe.unal.edu.co.androidtic_tac_toe.online.business;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import tictactoe.unal.edu.co.androidtic_tac_toe.online.entities.Game;
import tictactoe.unal.edu.co.androidtic_tac_toe.online.entities.Room;

/**
 * Created by hnino on 30/10/2017.
 */

public class GameBusiness {

    public static final String GAME_REFERENCE = "games";
    public static final String GAME_KEY_REFERENCE = "gameKey";
    private DatabaseReference mDatabase;


    public GameBusiness() {
        mDatabase = FirebaseDatabase.getInstance().getReference(GAME_REFERENCE);
    }

    public String writeNewGame(int lastMovement) {
        String key = mDatabase.push().getKey();
        writeNewGame(key, lastMovement);
        return key;
    }

    public void writeNewGame(String gameId, int lastMovement) {
        Game game = new Game(lastMovement);
        mDatabase.child(gameId).setValue(game);
    }

    public void removeRoom(String roomId) {
        mDatabase.child(roomId).removeValue();
    }


    public DatabaseReference getmDatabase() {
        return mDatabase;
    }


}
