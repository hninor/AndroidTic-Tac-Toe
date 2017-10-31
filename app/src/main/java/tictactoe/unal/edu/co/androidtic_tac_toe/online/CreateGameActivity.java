package tictactoe.unal.edu.co.androidtic_tac_toe.online;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import tictactoe.unal.edu.co.androidtic_tac_toe.R;
import tictactoe.unal.edu.co.androidtic_tac_toe.online.business.RoomBusiness;

public class CreateGameActivity extends AppCompatActivity {


    private EditText mRoomNameEditText;
    private EditText mFirstPlayerEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_gamme);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRoomNameEditText = (EditText) findViewById(R.id.roomNameEditText);
        mFirstPlayerEditText = (EditText) findViewById(R.id.firstPlayerEditText);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String roomName = mRoomNameEditText.getText().toString().trim();
                String firstPlayer = mFirstPlayerEditText.getText().toString().trim();
                RoomBusiness roomBusiness = new RoomBusiness();
                if (roomName.isEmpty() || firstPlayer.isEmpty()) {
                    Snackbar.make(view, R.string.complete_informacion, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                } else {
                    roomBusiness.writeNewRoom(roomName, firstPlayer);
                    Snackbar.make(view, R.string.juego_creado, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }


            }
        });
    }

}
