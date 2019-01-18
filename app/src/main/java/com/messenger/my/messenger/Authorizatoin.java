package com.messenger.my.messenger;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import java.util.concurrent.Executor;

public class Authorizatoin extends AppCompatActivity {

    EditText log;
    EditText pass;

    String mLog;
    String mPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorizatoin);

        log = findViewById(R.id.login);
        pass = findViewById(R.id.password);

        Button signIn = findViewById(R.id.singIn); // кнопка входа
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLog = log.getText().toString().trim();
                mPass = pass.getText().toString().trim();

                if ("".equals(mLog) || "".equals(mPass)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Authorizatoin.this);
                    builder.setTitle("Error")
                            .setMessage("Одно из полей не заполненно. Пожалуйста, заполните все поля и повторите отправку")
                            .setCancelable(false)
                            .setNegativeButton("Ок, закрыть",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    singIn();
                }

            }
        });

        Button signUp = findViewById(R.id.registration); // кнопка регистрации
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Authorizatoin.this, Registration.class);
                startActivity(intent);
            }
        });

    }

    private  void  singIn () {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(mLog, mPass).addOnCompleteListener(Authorizatoin.this, new OnCompleteListener<AuthResult>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    Toast.makeText(Authorizatoin.this, "Авторизация успешна", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(Authorizatoin.this, MainActivity.class);
                    startActivity(intent);

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Authorizatoin.this);
                    builder.setTitle("Error")
                            .setMessage("Авторизация провалена")
                            .setCancelable(false)
                            .setNegativeButton("Ок, закрыть",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        });


    }

}
