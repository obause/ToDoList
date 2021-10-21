package obause.example.multiactivities;

import static obause.example.multiactivities.MainActivity.listItems;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.util.HashSet;

public class AddTaskActivity extends AppCompatActivity {

    int taskId;
    SharedPreferences sharedPreferences;

    EditText nameEditText;
    EditText statusEditText;
    EditText prioEditText;

    public void saveTask(View view) {
        DBHelper dbHelper = new DBHelper(this);
        String name = nameEditText.getText().toString();
        String status = statusEditText.getText().toString();
        int priority = Integer.parseInt(prioEditText.getText().toString());
        MainActivity.writeTask(dbHelper, name, status, priority);
        finish();
    }

    protected void readTaskData (DBHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM tasks WHERE ID = " + taskId + " LIMIT 1", null);

        Log.i("read", "DB gelesen");

        int statusIndex = cursor.getColumnIndex("status");
        Log.i("statusIndex", Integer.toString(statusIndex));
        int prioIndex = cursor.getColumnIndex("priority");
        Log.i("prioIndex", Integer.toString(prioIndex));
        while (cursor.moveToNext()) {
            statusEditText.setText(cursor.getString(statusIndex));
            Log.i("Status", cursor.getString(statusIndex));
            //prioEditText.setText(cursor.getInt(3));
            Log.i("Status", cursor.getString(statusIndex));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        sharedPreferences = getApplicationContext().getSharedPreferences(
                "obause.example.multiactivities", Context.MODE_PRIVATE
        );

        nameEditText = findViewById(R.id.nameEditText);
        statusEditText = findViewById(R.id.statusEditText);
        prioEditText = findViewById(R.id.prioEditText);

        Intent intent = getIntent();

        taskId = intent.getIntExtra("taskId", -1);

        if (taskId != -1) {
            nameEditText.setText(listItems.get(taskId));
        } else {
            listItems.add("");
            taskId = listItems.size() - 1;
        }

        DBHelper dbHelper = new DBHelper(this);
        readTaskData(dbHelper);

        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                listItems.set(taskId, String.valueOf(charSequence));
                MainActivity.arrayAdapter.notifyDataSetChanged();

                HashSet<String> set = new HashSet<>(listItems);
                sharedPreferences.edit().putStringSet("tasks", set).apply();

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }
}