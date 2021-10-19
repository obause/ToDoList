package obause.example.multiactivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import java.util.HashSet;

public class AddTaskActivity extends AppCompatActivity {

    int taskId;
    SharedPreferences sharedPreferences;

    public void saveTask(View view) {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        sharedPreferences = getApplicationContext().getSharedPreferences(
                "obause.example.multiactivities", Context.MODE_PRIVATE
        );

        EditText editText = findViewById(R.id.editText);

        Intent intent = getIntent();

        taskId = intent.getIntExtra("taskId", -1);

        if (taskId != -1) {
            editText.setText(MainActivity.listItems.get(taskId));
        } else {
            MainActivity.listItems.add("");
            taskId = MainActivity.listItems.size() - 1;
        }

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                MainActivity.listItems.set(taskId, String.valueOf(charSequence));
                MainActivity.arrayAdapter.notifyDataSetChanged();

                HashSet<String> set = new HashSet<>(MainActivity.listItems);
                sharedPreferences.edit().putStringSet("tasks", set).apply();

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }
}