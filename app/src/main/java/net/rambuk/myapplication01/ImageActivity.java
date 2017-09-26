package net.rambuk.myapplication01;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class ImageActivity extends AppCompatActivity {
    private static final String DEBUGTAG = "JWP";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);




        setFullScreen();
        addTouchListener();
        showPromt();
    }

    private void showPromt() {
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
        image.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Coordinates...
                float x = event.getX();
                float y = event.getY();

                String message = String.format("Coordinates: (%.2f, %.2f)", x, y);
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                Log.d(DEBUGTAG, message);

                return false;
            }
        });
    }

    public void setFullScreen() {
        View mDecorView = getWindow().getDecorView();
        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }


}
