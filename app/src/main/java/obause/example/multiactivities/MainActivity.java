package obause.example.multiactivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    EditText taskNameEditText;

    static ArrayList<String> listItems = new ArrayList<String>();
    //public ArrayList<String> listItems = TasksSingleton.getInstance().getTasks();
    static ArrayAdapter<String> arrayAdapter;

    TinyDB tinyDB;

    SharedPreferences sharedPreferences;
    TextView textView;

    public void setLanguage(String language) {
        if (language == "german") {
            textView.setText("Deine Aufgaben");
        }
        else if (language == "english") {
            textView.setText("Your Tasks");
        }

        sharedPreferences.edit().putString("language", language).apply();
    }

    public void addTask(View view) {
        Intent intent = new Intent(getApplicationContext(), AddTaskActivity.class);
        intent.putExtra("name","test");
        startActivity(intent);
    }

    public void updateList () {
        if (arrayAdapter == null) {
            arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listItems);
            listView.setAdapter(arrayAdapter);
        } else {
            arrayAdapter.notifyDataSetChanged();
        }
    }

    public static void writeTask (DBHelper dbHelper, String name, String status, Integer priority) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("status", status);
        values.put("priority", priority);
        //values.put("projectId", projectId);
        db.insert("tasks", null, values);
    }

    protected void readTasks (DBHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM tasks", null);

        int nameIndex = cursor.getColumnIndex("name");
        while (cursor.moveToNext()) {
            listItems.add(cursor.getString(nameIndex));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView2);
        listView = findViewById(R.id.listView);

        //myDatabase.execSQL("INSERT INTO tasks (name, status, priority) VALUES ('Aufgabe1', 'Backlog', 1)");
        //myDatabase.execSQL("INSERT INTO tasks (name, status, priority) VALUES ('Aufgabe2', 'In Progress', 3)");

        DBHelper dbHelper = new DBHelper(this);
        //writeDatabase(dbHelper, textView);
        readTasks(dbHelper);


        sharedPreferences = this.getSharedPreferences("obause.example.multiactivities", Context.MODE_PRIVATE);
        String language = sharedPreferences.getString("language", "ERROR");

        /*HashSet<String> set = (HashSet<String>) sharedPreferences.getStringSet("tasks", null);

        if (set == null) {
            listItems.add("Beispieltask");
        } else {
            listItems = new ArrayList(set);
        }*/

        Log.i("listItems onCreate", listItems.toString());

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listItems);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), AddTaskActivity.class);
                intent.putExtra("taskId",i);
                //intent.putExtra("tasks", listItems);
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                final int itemToDelete = i;

                new android.app.AlertDialog.Builder(MainActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Eintrag löschen")
                        .setMessage("Möchtest du diesen Eintrag wirklich löschen?")
                        .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                listItems.remove(itemToDelete);
                                arrayAdapter.notifyDataSetChanged();
                                HashSet<String> set = new HashSet<>(listItems);
                                sharedPreferences.edit().putStringSet("tasks", set).apply();
                            }
                        })
                        .setNegativeButton("Nein", null)
                        .show();

                return true;
            }
        });

        if (language.equals("ERROR")) {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setTitle("Sprache auswählen")
                    .setMessage("Bitte wähle die Sprache aus")
                    .setPositiveButton("Deutsch", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(MainActivity.this, "Deutsch wurde ausgewählt", Toast.LENGTH_SHORT).show();
                            setLanguage("german");
                        }
                    })
                    .setNegativeButton("English", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(MainActivity.this, "English was selected", Toast.LENGTH_SHORT).show();
                            setLanguage("english");
                        }
                    })
                    .show();

        } else {
            setLanguage(language);
        }


        /*SharedPreferences sharedPreferences = this.getSharedPreferences("obause.example.multiactivities", Context.MODE_PRIVATE);

        sharedPreferences.edit().putString("name", "Ole").apply();
        sharedPreferences.edit().clear().commit();

        sharedPreferences.getString("name", "Nicht gefunden");*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        menuInflater.inflate(R.menu.add_task_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.info:
                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setTitle("Ole Bause")
                        .setMessage("Diese App wurde von Ole Bause entwickelt.")
                        .setPositiveButton("Cool!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(MainActivity.this, "Danke!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Nicht Cool...", null)
                        .show();
                return true;
            case R.id.english:
                setLanguage("english");
                return true;
            case R.id.german:
                setLanguage("german");
                return true;
            case R.id.add_task:
                addTask(listView);
                return true;
            default:
                return false;
        }
    }
}