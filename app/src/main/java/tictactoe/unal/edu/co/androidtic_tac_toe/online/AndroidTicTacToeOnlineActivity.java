package tictactoe.unal.edu.co.androidtic_tac_toe.online;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import tictactoe.unal.edu.co.androidtic_tac_toe.BoardView;
import tictactoe.unal.edu.co.androidtic_tac_toe.R;
import tictactoe.unal.edu.co.androidtic_tac_toe.SettingsActivity;
import tictactoe.unal.edu.co.androidtic_tac_toe.TicTacToeGame;
import tictactoe.unal.edu.co.androidtic_tac_toe.online.business.GameBusiness;
import tictactoe.unal.edu.co.androidtic_tac_toe.online.business.RoomBusiness;
import tictactoe.unal.edu.co.androidtic_tac_toe.online.entities.Room;

import static tictactoe.unal.edu.co.androidtic_tac_toe.online.business.GameBusiness.GAME_KEY_REFERENCE;
import static tictactoe.unal.edu.co.androidtic_tac_toe.online.business.RoomBusiness.GO_FIRST;

public class AndroidTicTacToeOnlineActivity extends AppCompatActivity {


    private static final String TAG = "ONLINE_GAME";
    // Represents the internal state of the game
    private TicTacToeGame mGame;
    private boolean mGameOver;
    private boolean mGoFirst;
    private boolean activeUser;
    private int ties;
    private int mHumanGamesWon;
    private int mAndroidGamesWon;
    private int mTiesGames;
    // Buttons making up the board
    private BoardView mBoardView;
    // Various text displayed
    private TextView mInfoTextView;
    private TextView mHumanTextView;
    private TextView mTiesTextView;
    private TextView mAndroidTextView;
    private TextView mFirstPlayerTextView;
    private TextView mSecondPlayerTextView;
    static final int DIALOG_DIFFICULTY_ID = 0;
    static final int DIALOG_QUIT_ID = 1;
    MediaPlayer mHumanMediaPlayer;
    MediaPlayer mComputerMediaPlayer;
    private SharedPreferences mPrefs;
    private boolean mSoundOn;
    private DatabaseReference mDatabaseReference;
    private char mIdentificador;
    private char mIdentificadorOponente;
    private RoomBusiness mRoomBusiness;
    private String mKeyRoom;
    private int mLastMovement = -1;
    private ValueEventListener mValueEventListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_android_tic_tac_toe);
        mRoomBusiness = new RoomBusiness();
        mGame = new TicTacToeGame();
        mBoardView = (BoardView) findViewById(R.id.board);
        mBoardView.setGame(mGame);
        mBoardView.setOnTouchListener(mTouchListener);
        mInfoTextView = (TextView) findViewById(R.id.information);
        mHumanTextView = (TextView) findViewById(R.id.tvHuman);
        mTiesTextView = (TextView) findViewById(R.id.tvTies);
        mAndroidTextView = (TextView) findViewById(R.id.tvAndroid);
        mFirstPlayerTextView = (TextView) findViewById(R.id.tvFirstPlayerHuman);
        mSecondPlayerTextView = (TextView) findViewById(R.id.tvSecondPlayer);
        mFirstPlayerTextView.setText("You:");
        mSecondPlayerTextView.setText("Opponent:");
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        // Restore the scores
        mHumanGamesWon = mPrefs.getInt("mHumanWins", 0);
        mAndroidGamesWon = mPrefs.getInt("mComputerWins", 0);
        mTiesGames = mPrefs.getInt("mTies", 0);

        mSoundOn = mPrefs.getBoolean("sound", true);
        String difficultyLevel = mPrefs.getString("difficulty_level",
                getResources().getString(R.string.difficulty_harder));
        if (difficultyLevel.equals(getResources().getString(R.string.difficulty_easy)))
            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Easy);
        else if (difficultyLevel.equals(getResources().getString(R.string.difficulty_harder)))
            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Harder);
        else
            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Expert);

        traerExtra();
        if (savedInstanceState == null) {
            startNewGame();
        } else {
            // Restore the game's state
            mGame.setBoardState(savedInstanceState.getCharArray("board"));
            mGameOver = savedInstanceState.getBoolean("mGameOver");
            mInfoTextView.setText(savedInstanceState.getCharSequence("info"));
            mHumanGamesWon = savedInstanceState.getInt("mHumanWins");
            mAndroidGamesWon = savedInstanceState.getInt("mComputerWins");
            mTiesGames = savedInstanceState.getInt("mTies");
            mGoFirst = savedInstanceState.getBoolean("mGoFirst");
            activeUser = savedInstanceState.getBoolean("mActive");
        }
        updateBoard();

        mDatabaseReference = mRoomBusiness.getmDatabase().child(mKeyRoom);

        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Room room = dataSnapshot.getValue(Room.class);
                if (room.getLastMovement() >= 0 && room.getLastMovement() <= 8) {
                    if (room.getLastMovement() == mLastMovement) {
                        //DONOTHING
                    } else {
                        setMove(mIdentificadorOponente, room.getLastMovement());
                        activeUser = true;
                        int winner = mGame.checkForWinner();
                        checkForWinnerOpponent(winner);
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };


    }


    private void traerExtra() {
        mGoFirst = getIntent().getBooleanExtra(GO_FIRST, false);
        mKeyRoom = getIntent().getStringExtra(GAME_KEY_REFERENCE);
    }

    private void updateBoard() {
        mAndroidTextView.setText(String.valueOf(mAndroidGamesWon));
        mHumanTextView.setText(String.valueOf(mHumanGamesWon));
        mTiesTextView.setText(String.valueOf(mTiesGames));
    }

    private void startNewGame() {


        mGame.clearBoard();
        mBoardView.invalidate();

        if (mGoFirst) {
            mIdentificador = TicTacToeGame.HUMAN_PLAYER;
            activeUser = true;
            mInfoTextView.setText(R.string.you_go_first);
        } else {
            mIdentificador = TicTacToeGame.COMPUTER_PLAYER;
            activeUser = false;
            mInfoTextView.setText(R.string.opponent_go_first);
        }
        mGameOver = false;

        if (mIdentificador == mGame.HUMAN_PLAYER) {
            mIdentificadorOponente = mGame.COMPUTER_PLAYER;
        } else {
            mIdentificadorOponente = mGame.HUMAN_PLAYER;
        }

    }


    private boolean setMove(char player, int location) {
        if (mGame.setMove(player, location)) {
            if (mSoundOn) {
                if (player == TicTacToeGame.HUMAN_PLAYER) {
                    mHumanMediaPlayer.start();    // Play the sound effect
                } else {
                    mComputerMediaPlayer.start();    // Play the sound effect
                }
            }
            mBoardView.invalidate();   // Redraw the board
            return true;
        }
        return false;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu_online, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_game:
                startNewGame();
                return true;
            case R.id.settings:
                startActivityForResult(new Intent(this, SettingsActivity.class), 0);
                return true;
            case R.id.quit:
                mHumanGamesWon = 0;
                mAndroidGamesWon = 0;
                mTiesGames = 0;
                updateBoard();
                startNewGame();
                //showDialog(DIALOG_QUIT_ID);
                return true;

        }
        return false;

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        switch (id) {
            case DIALOG_DIFFICULTY_ID:

                builder.setTitle(R.string.difficulty_choose);

                final CharSequence[] levels = {
                        getResources().getString(R.string.difficulty_easy),
                        getResources().getString(R.string.difficulty_harder),
                        getResources().getString(R.string.difficulty_expert)};

                // TODO: Set selected, an integer (0 to n-1), for the Difficulty dialog.
                // selected is the radio button that should be selected.
                int selected = 2;

                builder.setSingleChoiceItems(levels, selected,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                dialog.dismiss();   // Close dialog
                                // TODO: Set the diff level of mGame based on which item was selected.
                                switch (item) {
                                    case 0:
                                        mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Easy);
                                        break;
                                    case 1:
                                        mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Harder);
                                        break;
                                    case 2:
                                        mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Expert);
                                        break;
                                    default:
                                        break;
                                }

                                // Display the selected difficulty level
                                Toast.makeText(getApplicationContext(), levels[item],
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                dialog = builder.create();

                break;

            case DIALOG_QUIT_ID:
                // Create the quit confirmation dialog

                builder.setMessage(R.string.quit_question)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                AndroidTicTacToeOnlineActivity.this.finish();
                            }
                        })
                        .setNegativeButton(R.string.no, null);
                dialog = builder.create();

                break;

        }

        return dialog;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == RESULT_CANCELED) {
            // Apply potentially new settings

            mSoundOn = mPrefs.getBoolean("sound", true);

            String difficultyLevel = mPrefs.getString("difficulty_level",
                    getResources().getString(R.string.difficulty_harder));

            if (difficultyLevel.equals(getResources().getString(R.string.difficulty_easy)))
                mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Easy);
            else if (difficultyLevel.equals(getResources().getString(R.string.difficulty_harder)))
                mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Harder);
            else
                mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Expert);
        }
    }


    // Listen for touches on the board
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            if (activeUser) {
                // Determine which cell was touched
                int col = (int) event.getX() / mBoardView.getBoardCellWidth();
                int row = (int) event.getY() / mBoardView.getBoardCellHeight();
                int pos = row * 3 + col;


                if (!mGameOver && setMove(mIdentificador, pos)) {
                    mLastMovement = pos;
                    mDatabaseReference.child("lastMovement").setValue(pos);
                    activeUser = false;
                    // If no winner yet, let the computer make a move
                    int winner = mGame.checkForWinner();
                    checkForWinnerUser(winner);


                }
            }


            // So we aren't notified of continued events when finger is moved
            return false;
        }
    };

    private void checkForWinnerUser(int winner) {

        if (winner == 0)
            mInfoTextView.setText(R.string.opponent_turn);
        else if (winner == 1) {
            mGameOver = true;
            mInfoTextView.setText(R.string.result_tie);
            mTiesGames++;
            updateBoard();
        } else if (winner == 2 && mIdentificador == TicTacToeGame.HUMAN_PLAYER) {
            mGameOver = true;
            String defaultMessage = getResources().getString(R.string.result_human_wins);
            mInfoTextView.setText(mPrefs.getString("victory_message", defaultMessage));
            mHumanGamesWon++;
            updateBoard();
        } else if (winner == 3 && mIdentificador == TicTacToeGame.COMPUTER_PLAYER) {
            mGameOver = true;
            String defaultMessage = getResources().getString(R.string.result_human_wins);
            mInfoTextView.setText(mPrefs.getString("victory_message", defaultMessage));
            mHumanGamesWon++;
            updateBoard();
        } else {
            mGameOver = true;
            mInfoTextView.setText(R.string.result_opponent_wins);
            mAndroidGamesWon++;
            updateBoard();
        }
    }

    private void checkForWinnerOpponent(int winner) {

        if (winner == 0)
            mInfoTextView.setText(R.string.your_turn);
        else if (winner == 1) {
            mGameOver = true;
            mInfoTextView.setText(R.string.result_tie);
            mTiesGames++;
            updateBoard();
        } else if (winner == 2 && mIdentificadorOponente == TicTacToeGame.HUMAN_PLAYER) {
            mGameOver = true;
            String defaultMessage = getResources().getString(R.string.result_opponent_wins);
            mInfoTextView.setText(defaultMessage);
            mAndroidGamesWon++;
            updateBoard();
        } else if (winner == 3 && mIdentificadorOponente == TicTacToeGame.COMPUTER_PLAYER) {
            mGameOver = true;
            String defaultMessage = getResources().getString(R.string.result_opponent_wins);
            mInfoTextView.setText(defaultMessage);
            mAndroidGamesWon++;
            updateBoard();
        } else {
            mGameOver = true;
            mInfoTextView.setText(R.string.result_human_wins);
            mHumanGamesWon++;
            updateBoard();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        mHumanMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.dog2);
        mComputerMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.cat2);
        mDatabaseReference.addValueEventListener(mValueEventListener);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mHumanMediaPlayer.release();
        mComputerMediaPlayer.release();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putCharArray("board", mGame.getBoardState());
        outState.putBoolean("mGameOver", mGameOver);
        outState.putInt("mHumanWins", mHumanGamesWon);
        outState.putInt("mComputerWins", mAndroidGamesWon);
        outState.putInt("mTies", mTiesGames);
        outState.putCharSequence("info", mInfoTextView.getText());
        outState.putBoolean("mGoFirst", mGoFirst);
        outState.putBoolean("mActive", activeUser);
    }


    @Override
    protected void onStop() {
        super.onStop();

        // Save the current scores
        SharedPreferences.Editor ed = mPrefs.edit();
        ed.putInt("mHumanWins", mHumanGamesWon);
        ed.putInt("mComputerWins", mAndroidGamesWon);
        ed.putInt("mTies", mTiesGames);
        ed.commit();

        mDatabaseReference.removeEventListener(mValueEventListener);
    }


}
