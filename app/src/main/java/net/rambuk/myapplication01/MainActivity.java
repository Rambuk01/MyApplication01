package net.rambuk.myapplication01;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    public static final String TEXTFILE = "notesquirrel.txt";
    public static final String FILESAVED = "FileSaved"; // Arbitrary String used as a key to save and retrieve a boolean value.
    private String toastSaveText = null;
    private String toastLoadText = null;
    private String errorToast = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addSaveButtonListener();
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        boolean fileSaved = prefs.getBoolean(FILESAVED, false);

        if(fileSaved) {
            loadSavedFile();
        }


    }



    private void loadSavedFile() {

        Button loadBtn = (Button) findViewById(R.id.load);
        loadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    FileInputStream fis = openFileInput(TEXTFILE); // Can only read bytes.
                    DataInputStream dis = new DataInputStream(fis);
                    InputStreamReader ir = new InputStreamReader(dis);
                    BufferedReader reader = new BufferedReader(ir); // Fucking russian doll!

                    EditText editText = (EditText) findViewById(R.id.text);
                    String line;
                    while((line = reader.readLine()) != null) {
                        editText.append(line);
                        editText.append("\n");
                    }
                    fis.close();

                    Context loadContext = getApplicationContext();
                    CharSequence toastLoadText = "Text Loaded";
                    int toastLoadDuration = Toast.LENGTH_LONG;

                    Toast loadToast = Toast.makeText(loadContext, toastLoadText, toastLoadDuration);
                    loadToast.show();

                } catch (Exception e) {
                    Context errorContext = getApplicationContext();
                    CharSequence errorToast = "File cannot be loaded";
                    int errorToastLength = Toast.LENGTH_LONG;
                    Toast showErrorToast = Toast.makeText(errorContext, errorToast, errorToastLength);
                    showErrorToast.show();
                }
            }
        });
    }

    private void addSaveButtonListener() {
        Button saveBtn = (Button) findViewById(R.id.save);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = (EditText) findViewById(R.id.text);
                String text = editText.getText().toString(); // Dont forget to use toString... not sure why

                try {
                    FileOutputStream fos = openFileOutput(TEXTFILE, Context.MODE_PRIVATE); // Only my app can read the text, with the private mode activated.
                    fos.write(text.getBytes());
                    fos.close();

                    editText.setText(null);

                    SharedPreferences prefs = getPreferences(MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean(FILESAVED, true);
                    editor.commit();  //Saves the values that I put. Must be called when do you a put in an editor.

                    Context context = getApplicationContext();
                    CharSequence toastSaveText = "Text Saved";
                    int duration = Toast.LENGTH_LONG;

                    Toast saveToast = Toast.makeText(context, toastSaveText, duration);
                    saveToast.show();

                } catch (Exception e) {
                    Context errorContext = getApplicationContext();
                    CharSequence errorToast = "File cannot be Saved";
                    int errorToastLength = Toast.LENGTH_LONG;
                    Toast showErrorToast = Toast.makeText(errorContext, errorToast, errorToastLength);
                    showErrorToast.show();
                }


            }
        });
    }
}
