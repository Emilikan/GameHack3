package com.messenger.my.messenger;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Registration extends AppCompatActivity {

    EditText mName;
    EditText mLog;
    EditText mPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mName = findViewById(R.id.namess);
        mLog = findViewById(R.id.emailss);
        mPass = findViewById(R.id.passNewss);

        // продолжить
        Button next = findViewById(R.id.createNewUserss);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // сохраняем значения для след активити
                if(mPass.getText().toString().equals("") || mLog.getText().toString().equals("") || mName.getText().toString().equals("")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(Registration.this);
                    builder.setTitle("Error")
                            .setMessage("Поля ввода не заполненно")
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
                else {
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Registration.this);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("log", mLog.getText().toString().trim());
                    editor.putString("name", mName.getText().toString().trim());
                    editor.putString("pass", mPass.getText().toString().trim());
                    editor.apply();

                    Intent intent = new Intent(Registration.this, CheckEmail.class);
                    startActivity(intent);
                }
            }
        });



    }
}
