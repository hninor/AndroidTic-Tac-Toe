package tictactoe.unal.edu.co.androidtic_tac_toe.online;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tictactoe.unal.edu.co.androidtic_tac_toe.R;
import tictactoe.unal.edu.co.androidtic_tac_toe.online.business.RoomBusiness;
import tictactoe.unal.edu.co.androidtic_tac_toe.online.entities.Room;

public class JoinGameActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Room> roomList = new ArrayList<>();
    private List<String> mKeyList = new ArrayList<>();
    private int mSelectedPosition;
    private RoomBusiness mRoomBusiness;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_game);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mRoomBusiness = new RoomBusiness();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(JoinGameActivity.this, CreateGameActivity.class));
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);


        readAllRooms();




    }

    private void mostrarCuadroDialogo() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Unirse");
        builder1.setMessage("¿Desea unirse a está partida?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Si",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        mRoomBusiness.getmDatabase().child(mKeyList.get(mSelectedPosition)).child("secondPlayer").setValue("Anonymous");
                        mRoomBusiness.getmDatabase().child(mKeyList.get(mSelectedPosition)).child("activo").setValue(false);
                        Intent intent = new Intent(JoinGameActivity.this, AndroidTicTacToeOnlineActivity.class);
                        startActivity(intent);
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public void readAllRooms() {

        mRoomBusiness.getmDatabase().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                roomList.clear();
                mKeyList.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Room room = postSnapshot.getValue(Room.class);
                    if (room.isActivo()) {
                        roomList.add(room);
                        mKeyList.add(postSnapshot.getKey());
                    }

                }
                // specify an adapter (see also next example)
                mAdapter = new MyAdapter(roomList);
                mAdapter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSelectedPosition = mRecyclerView.getChildAdapterPosition(v);
                        mostrarCuadroDialogo();

                    }
                });
                mRecyclerView.setAdapter(mAdapter);
                if (roomList.size() > 0) {
                    Room lastRoomCreated = roomList.get(roomList.size() - 1);
                    Snackbar.make(mRecyclerView, "El usuario " + lastRoomCreated.getFirstPlayer() + " ha creado una partida", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                // ...
            }
        });

    }

    public List<Room> getRoomList() {
        return roomList;
    }

}
