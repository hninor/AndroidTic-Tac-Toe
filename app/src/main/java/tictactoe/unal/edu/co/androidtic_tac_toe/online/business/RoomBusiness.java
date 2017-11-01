package tictactoe.unal.edu.co.androidtic_tac_toe.online.business;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tictactoe.unal.edu.co.androidtic_tac_toe.online.entities.Room;

/**
 * Created by hnino on 30/10/2017.
 */

public class RoomBusiness {

    public static final String ROOM_REFERENCE = "rooms";
    public static final String GO_FIRST = "goFirst";
    //public static final String GAME_REFERENCE = "games";
    private DatabaseReference mDatabase;


    public RoomBusiness() {
        mDatabase = FirebaseDatabase.getInstance().getReference(ROOM_REFERENCE);
    }

    public String writeNewRoom(String name, String player1, boolean activo) {
        String key = mDatabase.push().getKey();
        writeNewRoom(key, name, player1, activo);
        return key;
    }

    public void writeNewRoom(String roomId, String name, String player1, boolean activo) {
        Room user = new Room(name, player1, activo);
        mDatabase.child(roomId).setValue(user);
    }

    public void removeRoom(String roomId) {
        mDatabase.child(roomId).removeValue();
    }


    public DatabaseReference getmDatabase() {
        return mDatabase;
    }


}
