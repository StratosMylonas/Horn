package com.stratos.horn;

import android.content.Intent;
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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.ProfilePictureView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    SQLiteDatabase database;
    GroupListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);          //will hide the title
        Objects.requireNonNull(getSupportActionBar()).hide();   //hide the title bar

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

        if (!isLoggedIn){
            Intent intent = new Intent(this, LogInActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            String userId = accessToken.getUserId();

            mDatabase = FirebaseDatabase.getInstance().getReference();

            //initialise local database, if not exists
            database = openOrCreateDatabase("escape_control", MODE_PRIVATE, null);
            database.execSQL("CREATE TABLE IF NOT EXISTS Groups(id INT, group_name VARCHAR(20));");

            ArrayList<String> groupNames = getAllGroups();

            adapter = new GroupListAdapter(this, groupNames);
            ListView groupList = findViewById(R.id.groupListView);
            groupList.setAdapter(adapter);

            groupList.setOnItemLongClickListener((parent, view, position, id) -> {
                Object o = groupList.getItemAtPosition(position);
                String str = (String) o;

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                        .setMessage("Do you want to remove?")
                        .setCancelable(true)
                        .setPositiveButton("Yes", (dialog, which) -> {
                            database.execSQL("DELETE FROM groups WHERE group_name = '" + str + "'");
                            resetActivity();
                            dialog.dismiss();
                        })
                        .setNegativeButton("No", (dialog, which) -> dialog.dismiss());

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                return false;
            });

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
                builder.setNegativeButton("Cancel", (dialogInterface, i) -> database.close());

                AlertDialog dialog = builder.create();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            });

            ProfilePictureView profilePictureView = findViewById(R.id.friendProfilePicture);
            profilePictureView.setProfileId(userId);

            profilePictureView.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                        .setMessage("Do you want to Log Out?")
                        .setCancelable(true)
                        .setPositiveButton("Yes", ((dialog, which) -> {
                            LoginManager.getInstance().logOut();
                            resetActivity();
                            dialog.dismiss();
                        }))
                        .setNegativeButton("No", (((dialog, which) -> dialog.dismiss())));
                builder.show();
            });
        }
    }

    public ArrayList<String> getAllGroups() {
        ArrayList<String> groupNames = new ArrayList<>();
        try {
            String query = "SELECT group_name FROM Groups";
            Cursor cursor = database.rawQuery(query, null);
            while (cursor.moveToNext()) {
                groupNames.add(cursor.getString(cursor.getColumnIndex("group_name")));
            }
            cursor.close();
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return groupNames;
    }

    private void sendDialogDataToActivity(String data, SQLiteDatabase database){
        if (data.length() > 0) {
            database.execSQL("INSERT INTO Groups (group_name) VALUES ('" + data + "')");
            resetActivity();
        }
        else
            Toast.makeText(this, "Please enter a group name", Toast.LENGTH_SHORT).show();
    }

    private void resetActivity(){
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, (arg0, arg1) -> {
                    finish();
                    System.exit(0);}).create().show();
    }
}