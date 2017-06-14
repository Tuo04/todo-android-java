package com.microsoft.todoapp.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.microsoft.todoapp.R;
import com.microsoft.todoapp.database.DatabaseHelper;
import com.microsoft.todoapp.exceptions.InvalidValueException;

import java.util.ArrayList;
import com.microsoft.azure.mobile.MobileCenter;
import com.microsoft.azure.mobile.analytics.Analytics;
import com.microsoft.azure.mobile.crashes.Crashes;
public class MainActivity extends AppCompatActivity {

    private DatabaseHelper mHelper;
    private ArrayAdapter<String> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Analytics.trackEvent("add_button");
        Analytics.trackEvent("updateUI");
        Analytics.trackEvent("onClick");
        MobileCenter.start(getApplication(), "d6a7ead3-8e7f-45e8-a143-b282395c6d06",
                Analytics.class, Crashes.class);
        setContentView(R.layout.activity_main);

        findViewById(R.id.add_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.new_task, null);
                final EditText taskEditText = (EditText) layout.findViewById(R.id.task);
                taskEditText.setSingleLine();
                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.add_task_dialog_title)
                        .setView(layout)
                        .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                saveTask(String.valueOf(taskEditText.getText()));
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Call SDK for event
                            }
                        })
                        .create();
                dialog.show();
            }
        });

        mHelper = new DatabaseHelper(this);
        ((ListView) findViewById(R.id.list_todo)).setAdapter(
                mAdapter = new ArrayAdapter<>(this, R.layout.item_todo, R.id.task_title));

        updateUI();
        /*java.util.Map<String, String> properties = new java.util.HashMap<>();
        properties.put("event", "add");*/

    }

    private void saveTask(String task) {
        if (task.isEmpty()) {
            throw new IllegalArgumentException("Task cannot be null or empty.");
        } else if (task.trim().isEmpty()) {
            throw new InvalidValueException("Task cannot be null or empty.");
        }
        mHelper.saveTask(task);
        updateUI();
    }

    public void deleteTask(View view) throws Exception {
        TextView taskTextView = (TextView) ((View) view.getParent()).findViewById(R.id.task_title);
        mHelper.deleteTask(String.valueOf(taskTextView.getText()));
        updateUI();
        throw new Exception();
    }

    private void updateUI() {
        ArrayList<String> taskList = mHelper.getTasks();
        mAdapter.clear();
        mAdapter.addAll(taskList);
        mAdapter.notifyDataSetChanged();
    }
}
