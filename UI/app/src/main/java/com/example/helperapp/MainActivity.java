package com.example.helperapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    EditText username;
    EditText password;
    CardView login;
    TextView register;
    TextView invalidLogin;
    ProgressBar progressBar;
    String sUser;
    String sPass;
    FirebaseAuth fAuth;
    int loginFlag=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        initComponent();

    }

    private void initComponent() {
        username = (EditText)findViewById(R.id.editText);
        password = (EditText)findViewById(R.id.editText2);
        login = (CardView) findViewById(R.id.cardView);
        register = (TextView)findViewById(R.id.textView2);
        invalidLogin = (TextView)findViewById(R.id.textViewError);
        invalidLogin.setTextColor(Color.RED);
        progressBar = (ProgressBar) findViewById(R.id.progressBarLog);
        fAuth = FirebaseAuth.getInstance();
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                if (checkLogin()) {
                    actionLogin();
                }
                else{
                    showLoginError();
                }

            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Register.class);
                startActivity(intent);
            }
        });

    }

    private void showLoginError() {
        progressBar.setVisibility(View.GONE);
        String err;
        if (loginFlag == 1) {
            err = "Please fill in all boxes";
            invalidLogin.setText(err);
        }
    }
    private void getLoginData() {
        sUser = username.getText().toString().trim();
        sPass = password.getText().toString().trim();
    }

    private boolean checkLogin() {
        getLoginData();
        if (TextUtils.isEmpty(sUser) || TextUtils.isEmpty(sPass))
        {
            loginFlag = 1;
            return false;
        }
        return true;
    }

    private void actionLogin() {
        fAuth.signInWithEmailAndPassword(sUser, sPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, "Login successfully", Toast.LENGTH_LONG).show();
                    Intent mapIntent = new Intent(getApplicationContext(), MapsActivity.class);
                    startActivity(mapIntent);
                }
                else{
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }


}