package edu.harding.acornmaze;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
    
    private static final int NAME_DIALOG_ID = 1;
    
    private long mScore;
    
    private SharedPreferences mPrefs;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
    }
    
    public void onStartClick(View view) {
        startActivityForResult(new Intent(this, AcornMazeActivity.class), 0);
    }
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("TEST");
        Toast.makeText(this, R.string.congratulations, Toast.LENGTH_LONG).show();
        if (resultCode == AcornMazeActivity.RESULT_WIN && data != null) {
            mScore = data.getLongExtra("score", -1);
            if (mScore != -1) {
                System.out.println("Stupid wanker!");
                showDialog(NAME_DIALOG_ID);
            }
        }
    }
    
    

    public void onOptionsClick(View view) {
        //TODO: Get a PreferenceActivity together.
        //startActivity(new Intent(this, OptionsActivity.class));
    }
    
    public void onHiscoresClick(View view) {
        startActivity(new Intent(this, HiscoresActivity.class));
    }
    
    private String buildTimeString(long score) {
        //TODO: There really should be an easier way to do this...
        StringBuilder builder = new StringBuilder();
        builder.append(score/60000);
        builder.append(':');
        String secs = String.valueOf((score%60000)/1000);
        if (secs.length() == 1) {
            builder.append('0');
        }
        builder.append(secs);
        builder.append('.');
        String millis = String.valueOf(score%1000);
        if (millis.length() < 3) {
            builder.append('0');
        }
        if (millis.length() < 2) {
            builder.append('0');
        }
        builder.append(millis);
        return builder.toString();
    }
    
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        switch (id) {
        case NAME_DIALOG_ID:
            final EditText input = new EditText(this);
            builder.setMessage(R.string.name_prompt);
            builder.setView(input);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    updateScores(input.getText().toString());
                }
            });
        }
        dialog = builder.create();
        return dialog;
    }
    
    private void updateScores(String name) {
        for (int i = 0; i < 3; i++) {
            String suffix = "time" + String.valueOf(i);
            long hiscore = mPrefs.getLong("score"+suffix+"value", 600000);
            if (mScore <= hiscore) {
                SharedPreferences.Editor editor = mPrefs.edit();
                for (int j = i+1; i < 3; i++) {
                    String oldSuffix = "time" + String.valueOf(j-1);
                    String newSuffix = "time" + String.valueOf(j);
                    editor.putString("name"+newSuffix,
                            mPrefs.getString("name"+oldSuffix,
                                    getString(R.string.hiscores_default_name)));
                    editor.putString("score"+newSuffix,
                            mPrefs.getString("score"+oldSuffix,
                                    getString(R.string.hiscores_default_time)));
                    editor.putLong("name"+newSuffix+"value",
                            mPrefs.getLong("name"+oldSuffix+"value", Long.MAX_VALUE));
                }
                //TODO: Player name goes here!
                editor.putString("name"+suffix, name);
                String scoreString = buildTimeString(mScore);
                editor.putString("score"+suffix, scoreString);
                editor.putLong("score"+suffix+"value", mScore);
                editor.commit();
            }
        }
    }
}
