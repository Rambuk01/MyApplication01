package net.rambuk.myapplication01;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;

public class ImageActivity extends AppCompatActivity implements PointCollecterListener {

    private static final int POINT_CLOSENESS = 80;
    public static final String PASSWORD_SET = "PASSWORD_SET";
    private PointCollector pointCollector = new PointCollector();
    private Database db = new Database(this);
    public static final String DEBUGTAG = "JWP";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);



        onWindowFocusChanged(true);
        addTouchListener();

        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        Boolean passpointsSet = prefs.getBoolean(PASSWORD_SET, false);
        if(!passpointsSet) {
            showSetPasspointsPromt();
        }

        pointCollector.setListener(this);
    }

    public void passpointReset() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        preferences.edit().remove(PASSWORD_SET).commit();
    }
    private void showSetPasspointsPromt() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked OK.
            }
        });

        builder.setMessage(R.string.dialog_message_sequence);
        builder.setTitle(R.string.create_passpoint_sequence);
        AlertDialog dlg = builder.create();
        dlg.show();
    }

    private void addTouchListener() {
        ImageView image = (ImageView) findViewById(R.id.touch_image);
        image.setOnTouchListener(pointCollector);
    }

    private void savePasspoints(final List<Point> points) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.storing_data);
        final AlertDialog dlg = builder.create();
        dlg.show();

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {}

                db.storePoints(points);
                Log.d(DEBUGTAG, "Points saved: " + points.size());
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                // Setting preference, that we have stored a password.
                SharedPreferences prefs = getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(PASSWORD_SET, true);
                editor.commit();
                // clear the array of points and dismiss the dialog.
                pointCollector.clear();
                dlg.dismiss();
            }
        };
        task.execute();
    }

    private void verifyPasspoints(final List<Point> touchedPoints) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.checking_passpoints);
        final AlertDialog dlg = builder.create();
        dlg.show();

        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                List<Point> savedPoints = db.getPoints();
                Log.d(DEBUGTAG, "Loaded points: " + savedPoints.size());
                if(savedPoints.size() != PointCollector.NUM_POINTS || touchedPoints.size() != PointCollector.NUM_POINTS) {
                    return false;
                }
                for(int i = 0; i < PointCollector.NUM_POINTS; i++) {
                    Point savedPoint = savedPoints.get(i);
                    Point touchedPoint = touchedPoints.get(i);

                    int xDiff = savedPoint.x - touchedPoint.x;
                    int yDiff = savedPoint.y - touchedPoint.y;

                    int distSquared = xDiff * xDiff + yDiff * yDiff;

                    if(distSquared > POINT_CLOSENESS*POINT_CLOSENESS) {
                        return false;
                    }
                }

                return true;
            }

            @Override
            protected void onPostExecute(Boolean pass) {
                Log.d(DEBUGTAG, "Verify task returned: " + pass);

                dlg.dismiss();
                pointCollector.clear();

                if(pass == true) {
                    Intent i = new Intent(ImageActivity.this, MainActivity.class);
                    startActivity(i);
                } else {
                    Toast.makeText(ImageActivity.this, "Acces Denied", Toast.LENGTH_SHORT).show();
                }
            }
        };
        task.execute();
        Log.d(DEBUGTAG, "Verify here!");
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);}
    }

    @Override
    public void pointsCollected(final List<Point> points) {
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        Boolean passpointsSet = prefs.getBoolean(PASSWORD_SET, false);

        if(!passpointsSet) {
            Log.d(DEBUGTAG, "Saving passpoints...");
            Toast.makeText(this, R.string.saving_passpoints, Toast.LENGTH_LONG).show();
            savePasspoints(points);
        } else {
            Log.d(DEBUGTAG, "Verifying passpoints...");
            Toast.makeText(this, "Verifying passpoints...", Toast.LENGTH_SHORT).show();
            verifyPasspoints(points);
        }
    }
}
