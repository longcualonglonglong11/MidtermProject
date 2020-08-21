package com.apcs2.midtermmoblie;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Register extends AppCompatActivity {
    EditText name;
    EditText email;
    EditText pass;
    EditText confPass;
    TextView error;
    CardView done;
    String s_name;
    String s_email;
    String s_pass;
    String c_pass;
    int flag = 0;
    ProgressBar progressBar;
    TextView has_account;
    protected FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();
        name = (EditText)findViewById(R.id.editTextName);
        email = (EditText)findViewById(R.id.editTextUserName);
        pass = (EditText)findViewById(R.id.editTextPass);
        confPass = (EditText)findViewById(R.id.editTextRePass);
        error = (TextView) findViewById(R.id.textViewRegErr);
        error.setTextColor(Color.RED);
        done = (CardView) findViewById(R.id.cardViewDone);
        has_account = (TextView) findViewById(R.id.textView_hasAccount);
        progressBar =(ProgressBar) findViewById(R.id.progressBarReg);
//        if (mFirebaseAuth.getCurrentUser() != null)
//        {
//            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//            startActivity(intent);
//        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getData();
                if (checkReg())
                {
                    createAccount();

                }
                else
                {
                    showError();
                }
            }
        });
        has_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void createAccount() {
        progressBar.setVisibility(View.VISIBLE);
        mFirebaseAuth.createUserWithEmailAndPassword(s_email, s_pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(Register.this, "Register successfully", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
                else
                {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(Register.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    private void getData() {
        s_name = name.getText().toString().trim();
        s_email = email.getText().toString().trim();
        s_pass = pass.getText().toString().trim();
        c_pass = confPass.getText().toString().trim();
    }

    private void showError() {
        String err;
        switch (flag)
        {
            case 1:
                err = "Please fill in all boxes";
                error.setText(err);
                break;
            case 2:
                err = "Password must be longer than 6 characters";
                error.setText(err);
                break;
            case 3:
                err = "Password not match";
                error.setText(err);
                break;
        }

    }

    private boolean checkReg() {
        if (TextUtils.isEmpty(s_name) || TextUtils.isEmpty(s_email) || TextUtils.isEmpty(s_pass) || TextUtils.isEmpty(c_pass))
        {
            flag = 1;
            return false;
        }
        if (s_pass.length() <7)
        {
            flag =2;
            return false;
        }
        if (!s_pass.equals(c_pass))
        {
            flag = 2;
            return false;
        }
        return true;
    }


}