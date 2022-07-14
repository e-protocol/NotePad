package com.notes;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import java.util.ArrayList;

public class TableNotes extends MainActivity{
    private static ArrayList<TableRow> notes_list = new ArrayList<>();
    private Activity m_activity;
    public TableNotes(Activity activity)
    {
        m_activity = activity;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void addNote(TableRow newRow) {
        notes_list.add(newRow);
    }

    public void removeNote(int numberRow)
    {
        notes_list.remove(numberRow);
    }

    public int getNoteSize()
    {
        return notes_list.size();
    }

    public void updateNotes()
    {
        TableLayout tableLayout = (TableLayout) this.m_activity.findViewById(R.id.view_table);
        tableLayout.removeAllViews();
        for(int k = 0; k < notes_list.size(); k++)
        {
            View rowView = this.m_activity.getLayoutInflater().inflate(R.layout.table_row,null);

            android.widget.TableRow row = (android.widget.TableRow)rowView.findViewById(R.id.table_row);
            row.setClickable(true);
            int finalK1 = k;
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showNote(finalK1);
                }
            });

            //set date
            TextView date = (TextView)rowView.findViewById(R.id.date);
            date.setText(notes_list.get(k).getDate());

            //set title
            TextView title = (TextView)rowView.findViewById(R.id.title);
            title.setText(notes_list.get(k).getTitle());

            tableLayout.addView(row,k);
        }
    }

    public void editNote(int numberRow)
    {
        View dialogView = this.m_activity.getLayoutInflater().inflate(R.layout.dialog_add_note, null);

        //set note title
        EditText editTitle = (EditText)dialogView.findViewById(R.id.editNoteTitle);
        editTitle.setText(notes_list.get(numberRow).getTitle());

        //set note text
        EditText editText = (EditText)dialogView.findViewById(R.id.editNote);
        editText.setText(notes_list.get(numberRow).getText());

        //MessageBox
        AlertDialog.Builder dialog = new AlertDialog.Builder(this.m_activity);
        dialog.setView(dialogView);
        dialog.setTitle("Edit Note");
        dialog.setCancelable(true);
        dialog.setPositiveButton("Save",
                (dialog1, which) -> {
                    //show dialog
                    TableRow tableRow = writeNote(dialogView);
                    //check input
                    if(checkInput(tableRow.getTitle(),tableRow.getText(),this.m_activity))
                    {
                        //save edit
                        notes_list.get(numberRow).setDate(tableRow.getDate());
                        notes_list.get(numberRow).setTitle(tableRow.getTitle());
                        notes_list.get(numberRow).setText(tableRow.getText());
                        updateNotes();
                        saveData();
                        Toast.makeText(this.m_activity, "Saved", Toast.LENGTH_SHORT).show();
                    }
                }).setNegativeButton("Delete", (dialog12, which) ->{
                    removeNote(numberRow);
                    updateNotes();
                    saveData();
                    Toast.makeText(this.m_activity, "Deleted", Toast.LENGTH_SHORT).show();
                }).setNeutralButton("Cancel", (dialog12, which) ->{
            Toast.makeText(this.m_activity, "Canceled", Toast.LENGTH_SHORT).show();
        });
        dialog.create().show();
    }

    public void showNote(int numberRow)
    {
        String message = notes_list.get(numberRow).getText();
        String title = notes_list.get(numberRow).getDate() + "  " + notes_list.get(numberRow).getTitle();
        View dialogView = this.m_activity.getLayoutInflater().inflate(R.layout.dialog_show_note, null);
        TextView dialogTitle = (TextView)dialogView.findViewById(R.id.dialog_show_title);
        dialogTitle.setText(title);
        TextView dialogText = (TextView)dialogView.findViewById(R.id.dialog_show_text);
        dialogText.setText(message);
        dialogText.setMovementMethod(new ScrollingMovementMethod());

        //MessageBox
        AlertDialog.Builder dialog = new AlertDialog.Builder(this.m_activity);
        dialog.setView(dialogView);
        dialog.setCancelable(true);
        //set button ok
        Button btnOK = (Button)dialogView.findViewById(R.id.button_ok);
        AlertDialog mDialog = dialog.create();
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.cancel();
            }
        });

        //set button
        ImageButton edit_button = (ImageButton)dialogView.findViewById(R.id.button_edit);
        int finalK = numberRow;
        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.cancel();
                editNote(finalK);
            }
        });
        mDialog.show();
    }

    @SuppressLint("CommitPrefEdits")
    public void saveData()
    {
        //save Notes
        if(notes_list.isEmpty()) {
            return;
        }

        SharedPreferences.Editor prefs = MainActivity.getSettings().edit();
        prefs.clear();
        prefs.putBoolean("isSaved",true);
        prefs.putInt("notes_list_size",getNoteSize());
        Gson gson = new Gson(); //create json object
        for(int k = 0; k < notes_list.size(); k++)
        {
            prefs.putString("notes_list_array_" + Integer.valueOf(k).toString(),gson.toJson(notes_list.get(k)));
        }
        prefs.apply();
    }

    public void uploadSave()
    {
        if(MainActivity.getLoadSave())
        {
            SharedPreferences prefs = MainActivity.getSettings();
            int arrayList_size = prefs.getInt("notes_list_size",0);
            Gson gson = new Gson();
            for(int k = 0; k < arrayList_size; k++)
            {
                addNote(gson.fromJson(prefs.getString("notes_list_array_"
                                + Integer.valueOf(k).toString(), null), TableRow.class));
            }
        }
    }
}

