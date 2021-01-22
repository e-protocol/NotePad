package com.notes;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private TableNotes tableNotes;
    private static SharedPreferences mSettings;
    public static final String APP_PREFERENCES = "Settings";
    private static boolean m_loadSave = false;

    //setters and getters
    public static SharedPreferences getSettings() { return mSettings; }
    public static boolean getLoadSave() { return m_loadSave; }
    public static void setLoadSave (boolean loadSave) { m_loadSave = loadSave; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton button_add_note = (ImageButton)findViewById(R.id.button_add_note);
        button_add_note.setOnClickListener(this);
        tableNotes = new TableNotes(this);

        mSettings = getSharedPreferences(APP_PREFERENCES,Context.MODE_PRIVATE);
        //check save
        if(mSettings.contains("isSaved"))
            m_loadSave = true;

        //upload Save
        if(m_loadSave)
            tableNotes.uploadSave();
        tableNotes.updateNotes();
    }

    @Override
    public void onClick(View v) {
        View dialogView = this.getLayoutInflater().inflate(R.layout.dialog_add_note, null);

        //set default title
        int index = tableNotes.getNoteSize() + 1;
        String title = "Note " + index;
        EditText editTitle = (EditText)dialogView.findViewById(R.id.editNoteTitle);
        editTitle.setText(title);

        //MessageBox
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setView(dialogView);
        dialog.setTitle("Create Note");
        dialog.setCancelable(true);
        dialog.setPositiveButton("OK",
                (dialog1, which) -> {

                    //show dialog
                    TableRow tableRow = writeNote(dialogView);
                    //check input
                    if(checkInput(tableRow.getTitle(),tableRow.getText(),this))
                    {
                        tableNotes.addNote(tableRow);
                        tableNotes.updateNotes();
                        tableNotes.saveData();
                        Toast.makeText(getApplicationContext(), "Added", Toast.LENGTH_SHORT).show();
                    }
                }).setNegativeButton("Cancel", (dialog12, which) ->
                Toast.makeText(getApplicationContext(), "Canceled", Toast.LENGTH_SHORT).show());
        dialog.create().show();
    }

    public TableRow writeNote(View dialogView)
    {
        //get note title
        EditText editTitle = (EditText)dialogView.findViewById(R.id.editNoteTitle);
        String inputTitle = editTitle.getText().toString();

        //get note text
        EditText editText = (EditText)dialogView.findViewById(R.id.editNote);
        String inputText = editText.getText().toString();

        //get note date
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yy HH:mm", Locale.getDefault());
        String currentDateTime = format.format(new Date());

        TableRow tablerow = new TableRow(currentDateTime,inputTitle,inputText);
        return tablerow;
    }

    public boolean checkInput(String inputTitle,String inputText, Context context)
    {
        if(inputTitle.isEmpty() || inputText.isEmpty())
        {
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setTitle("Warning!");
            dialog.setMessage("Note has empty fields!");
            dialog.setPositiveButton("OK",((dialog1, which) -> {}));
            dialog.create().show();
            return false;
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        tableNotes.updateNotes();
    }

}
