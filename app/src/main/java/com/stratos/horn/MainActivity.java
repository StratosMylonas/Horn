package com.stratos.horn;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    String[] groupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);          //will hide the title
        Objects.requireNonNull(getSupportActionBar()).hide();   //hide the title bar

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialise local database, if not exists
        SQLiteDatabase database = openOrCreateDatabase("escape_control", MODE_PRIVATE, null);
        database.execSQL("CREATE TABLE IF NOT EXISTS Groups(id INT, group_name VARCHAR(20));");

        Cursor cursor = database.rawQuery("SELECT group_name FROM Groups", null);
        if (cursor.moveToFirst()){
            groupName = cursor.getColumnNames();
            GroupListAdapter adapter = new GroupListAdapter(this, groupName);
            ListView groupList = findViewById(R.id.groupListView);
            groupList.setAdapter(adapter);
        }
        else{
            Toast.makeText(this, "Create your first group of friends", Toast.LENGTH_LONG).show();
        }

        ImageButton imageButton = findViewById(R.id.addNewGroup);
        imageButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            final View customLayout = getLayoutInflater().inflate(R.layout.add_new_group_layout, null);
            final EditText editText = customLayout.findViewById(R.id.editTextGroupName);

            builder.setView(customLayout);
            builder.setCancelable(true);
            builder.setPositiveButton("OK", (dialogInterface, i) -> {
                sendDialogDataToActivity(editText.getText().toString(), database);
                database.close();
            });
            builder.setNegativeButton("Cancel", (dialogInterface, i) -> {
                database.close();
            });

            AlertDialog dialog = builder.create();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });

        cursor.close();
    }

    private void sendDialogDataToActivity(String data, SQLiteDatabase database){
        if (data.length() > 0)
            database.execSQL("INSERT INTO Groups (group_name) VALUES ('" + data + "')");
        else
            Toast.makeText(this, "Please enter a group name", Toast.LENGTH_SHORT).show();
    }
}