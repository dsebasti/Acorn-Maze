package edu.harding.acornmaze;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;

//TODO: There is too much hardcoding in this class (ex. 3 rows in the table.);
public class HiscoresActivity extends Activity {
    
    private static final String TIME_MODE = "time";
    private static final String POINTS_MODE = "points";
    
    private SharedPreferences mPrefs;
    private String mMode = "time";
    
    private TextView[] mNames = new TextView[3];
    private TextView[] mScores = new TextView[3];

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    public void onResume() {
        super.onResume();
        setContentView(R.layout.hiscores);
        mNames[0] = (TextView) this.findViewById(R.id.hiscores_name1);
        mNames[1] = (TextView) this.findViewById(R.id.hiscores_name2);
        mNames[2] = (TextView) this.findViewById(R.id.hiscores_name3);
        mScores[0] = (TextView) this.findViewById(R.id.hiscores_score1);
        mScores[1] = (TextView) this.findViewById(R.id.hiscores_score2);
        mScores[2] = (TextView) this.findViewById(R.id.hiscores_score3);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        setupTable();
    }
    
    private void setupTable() {
        String defaultScore;
        if (mMode.equals(TIME_MODE)) {
            defaultScore = getString(R.string.hiscores_default_time);
        } else {
            defaultScore = getString(R.string.hiscores_default_points);
        }
        for (int i = 0; i < 3; i++) {
            String suffix = mMode + String.valueOf(i);
            System.out.println(mPrefs.getString("name"+suffix,
                    getString(R.string.hiscores_default_name))+"x");
            mNames[i].setText(mPrefs.getString("name"+suffix,
                    getString(R.string.hiscores_default_name)));
            System.out.println(mPrefs.getString("score"+suffix, defaultScore+"x"));
            mScores[i].setText(mPrefs.getString("score"+suffix, defaultScore));
            System.out.println(mPrefs.getLong("score"+suffix+"value", -2));
        }
    }
    
    public void onResetClick(View view) {
        String defaultScore;
        if (mMode.equals(TIME_MODE)) {
            defaultScore = getString(R.string.hiscores_default_time);
        } else {
            defaultScore = getString(R.string.hiscores_default_points);
        }
        SharedPreferences.Editor editor = PreferenceManager
                .getDefaultSharedPreferences(this).edit();
        for (int i = 0; i < 3; i++) {
            String suffix = mMode + String.valueOf(i);
            mNames[i].setText(getString(R.string.hiscores_default_name));
            mScores[i].setText(defaultScore);
            editor.putString("name"+suffix, getString(R.string.hiscores_default_name));
            editor.putString("score"+suffix, defaultScore);
            //This needs to change based on the mode
            editor.putLong("score"+suffix+"value", 600000);
        }
        editor.commit();
    }
}
