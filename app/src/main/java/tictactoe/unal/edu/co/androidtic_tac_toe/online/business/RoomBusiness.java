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

    private DatabaseReference mDatabase;

    public RoomBusiness() {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("rooms");
    }

    public void writeNewRoom(String name, String player1) {
        String key = mDatabase.child("rooms").push().getKey();
        writeNewRoom(key, name, player1);
    }

    public void writeNewRoom(String roomId, String name, String player1) {
        Room user = new Room(name, player1);
        mDatabase.child("rooms").child(roomId).setValue(user);
    }

    public List<Room> readAllRooms() {
        final List<Room> rooms = new ArrayList<>();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    // TODO: handle the post
                }

                //rooms.add(room);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                // ...
            }
        });
        return rooms;
    }

    public DatabaseReference getmDatabase() {
        return mDatabase;
    }
}
