package tictactoe.unal.edu.co.androidtic_tac_toe.online;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import tictactoe.unal.edu.co.androidtic_tac_toe.R;
import tictactoe.unal.edu.co.androidtic_tac_toe.online.business.RoomBusiness;
import tictactoe.unal.edu.co.androidtic_tac_toe.online.entities.Room;

public class CreateGameActivity extends AppCompatActivity {


    private EditText mRoomNameEditText;
    private EditText mFirstPlayerEditText;
    private String mKeyRoom;
    private ProgressDialog mProgressDialog;
    private RoomBusiness mRoomBusiness;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_gamme);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mRoomBusiness = new RoomBusiness();
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.esperando_jugadores));
        mProgressDialog.setCanceledOnTouchOutside(true);
        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mRoomBusiness.removeRoom(mKeyRoom);
                //Snackbar.make(toolbar, R.string.juego_eliminado, Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });
        mRoomNameEditText = (EditText) findViewById(R.id.roomNameEditText);
        mFirstPlayerEditText = (EditText) findViewById(R.id.firstPlayerEditText);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String roomName = mRoomNameEditText.getText().toString().trim();
                String firstPlayer = mFirstPlayerEditText.getText().toString().trim();
                if (roomName.isEmpty() || firstPlayer.isEmpty()) {
                    Snackbar.make(view, R.string.complete_informacion, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                } else {
                    mKeyRoom = mRoomBusiness.writeNewRoom(roomName, firstPlayer, true);
                    listenJoinPlayer();
                    mProgressDialog.show();

                }


            }
        });
    }


    public void listenJoinPlayer() {

        mRoomBusiness.getmDatabase().child(mKeyRoom).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Room room = dataSnapshot.getValue(Room.class);
                if (room.getSecondPlayer() != null && !room.getSecondPlayer().isEmpty()) {
                    mProgressDialog.dismiss();
                    Intent intent = new Intent(CreateGameActivity.this, AndroidTicTacToeOnlineActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
