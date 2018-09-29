package com.nikitajain.nkj;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;

import activity.LoginActivity;
import helper.SQLiteHandler;
import helper.SessionManager;

public class MainActivity extends AppCompatActivity {

    private TextView name;
    private TextView email;
    private Button logout;

    private SQLiteHandler db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name=(TextView)findViewById(R.id.name);
        email=(TextView)findViewById(R.id.email);
        logout=(Button) findViewById(R.id.btnLogout);

        db=new SQLiteHandler(getApplicationContext());

        session=new SessionManager(getApplicationContext());

        if(!session.isLoggedIn()){
            logoutUser();
        }
        HashMap<String,String> user=db.getUserDetails();

        String uname=user.get("name");
        String uemail=user.get("email");

        name.setText(uname);
        email.setText(uemail);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });


    }

    private void logoutUser(){
        session.setLogin(false);
        db.deleteUsers();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
