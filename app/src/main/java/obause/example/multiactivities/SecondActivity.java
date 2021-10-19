package obause.example.multiactivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class SecondActivity extends AppCompatActivity {

    TinyDB tinyDB;
    Intent intent;
    String name;


    public void goBack(View view) {
        finish();
    }

    public void delete(View view) {
        tinyDB.remove("tasks");
        ArrayList<String> listItems = intent.getStringArrayListExtra("tasks");
        listItems.remove(name);
        tinyDB.putListString("tasks", listItems);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        tinyDB = new TinyDB(this);

        TextView textView = findViewById(R.id.textView);

        intent = getIntent();

        name = intent.getStringExtra("name");
        textView.setText("Hi\n" + name + "!");
    }
}