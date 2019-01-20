package com.messenger.my.messenger;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CheckEmail extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();

    FirebaseUser user;

    private String email;
    private String pass;
    private String name;
    private String mClass;
    private String mKod;

    private EditText mKodET;

    private SharedPreferences preferences;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_email);

        preferences = PreferenceManager.getDefaultSharedPreferences(CheckEmail.this);
        email = preferences.getString("log", null);
        pass = preferences.getString("pass", null);
        name = preferences.getString("name", null);
        mClass = preferences.getString("class", null);


        mKodET = findViewById(R.id.kodForEmail);

        mAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    // sing in
                    Intent intent = new Intent(CheckEmail.this, MainActivity.class);
                    startActivity(intent);

                    Toast.makeText(CheckEmail.this, "gh", Toast.LENGTH_SHORT).show();
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(CheckEmail.this);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("userUid", user.getUid() + "");
                    editor.apply();


                    mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Toast.makeText(CheckEmail.this, user.getUid() + "", Toast.LENGTH_SHORT).show();

                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(CheckEmail.this);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("userUid", user.getUid() + "");
                            editor.apply();

                            String mCount = dataSnapshot.child("Schools").child(mClass).child("counterOfUsers").getValue(String.class);
                            assert mCount != null;
                            int co = Integer.parseInt(mCount);
                            co++;
                            mCount = Integer.toString(co);

                            mRef.child("Schools").child(mClass).child(mCount).child("Profile").child("Name").setValue(name);
                            mRef.child("Schools").child(mClass).child(mCount).child("Profile").child("Email").setValue(email);
                            mRef.child("Schools").child(mClass).child(mCount).child("Uid").setValue(user.getUid() + "");
                            mRef.child("Schools").child(mClass).child("counterOfUsers").setValue(mCount);

                        }
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(CheckEmail.this);
                            builder.setTitle("Error")
                                    .setMessage(databaseError.getMessage())
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
                    });
                }
                else {
                    // sing out
                }
            }
        };

        if(mKod == null) {
            Thread send = new Thread(new Runnable() {
                @Override
                public void run() {
                    sendEmail();
                }
            });
            send.start();
        } else {
            AlertDialog.Builder ad;
            ad = new AlertDialog.Builder(CheckEmail.this);
            ad.setTitle("Информация");  // заголовок
            ad.setMessage("Код уже был отправлен. Введите код или отправте его еще раз"); // сообщение
            ad.setPositiveButton("Отправить еще раз", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int arg1) {
                    Thread send = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            sendEmail();
                        }
                    });
                    send.start();
                }
            });
            ad.setNegativeButton("Закрыть", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int arg1) {
                    dialog.cancel();
                }
            });
            ad.setCancelable(true);
            ad.show();
        }

        mKod = preferences.getString("kodOfEmail", null);

        // вход
        ImageView signIn = findViewById(R.id.singInNew);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(email == null || pass == null || name == null || mKod == null || mClass == null){
                    AlertDialog.Builder builder = new AlertDialog.Builder(CheckEmail.this);
                    builder.setTitle("Error")
                            .setMessage("Ошибка в передачи данных")
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
                else if(mKodET.getText().toString().equals("")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(CheckEmail.this);
                    builder.setTitle("Error")
                            .setMessage("Поле ввода кода не заполненно")
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
                else if(mKodET.getText().toString().trim().equals(mKod)){
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(CheckEmail.this);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("kodOfEmail", null);
                    editor.apply();

                    addUser();


                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CheckEmail.this);
                    builder.setTitle("Error")
                            .setMessage("Неправильный код")
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

    private void createUserInDb (){

        mAuth = FirebaseAuth.getInstance();

        final FirebaseUser user = mAuth.getCurrentUser();
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(user != null) {
                    //Toast.makeText(CheckEmail.this, user.getUid() + "", Toast.LENGTH_SHORT).show();

                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(CheckEmail.this);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("userUid", user.getUid() + "");


                    String mCount = dataSnapshot.child("Schools").child(mClass).child("counterOfUsers").getValue(String.class);
                    assert mCount != null;
                    int co = Integer.parseInt(mCount);
                    co++;
                    mCount = Integer.toString(co);

                    mRef.child("Schools").child(mClass).child(mCount).child("Profile").child("Name").setValue(name);
                    mRef.child("Schools").child(mClass).child(mCount).child("Profile").child("Email").setValue(email);
                    mRef.child("Schools").child(mClass).child(mCount).child("Uid").setValue(user.getUid() + "");
                    mRef.child("Schools").child(mClass).child("counterOfUsers").setValue(mCount);

                    editor.putString("log", null);
                    editor.putString("name", null);
                    editor.putString("pass", null);
                    editor.apply();

                }
                else {
                    Toast.makeText(CheckEmail.this, "Ошибка авторизации", Toast.LENGTH_SHORT).show();
                }
            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CheckEmail.this);
                builder.setTitle("Error")
                        .setMessage(databaseError.getMessage())
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
        });

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // sing in
                    Intent intent = new Intent(CheckEmail.this, MainActivity.class);
                    startActivity(intent);

                    mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Toast.makeText(CheckEmail.this, user.getUid() + "", Toast.LENGTH_SHORT).show();

                            mRef.child(user.getUid()).child("Profile").child("Name").setValue(name);
                            mRef.child(user.getUid()).child("Profile").child("Email").setValue(email);



                        }

                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(CheckEmail.this);
                            builder.setTitle("Error")
                                    .setMessage(databaseError.getMessage())
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
                    });
                } else {
                    // sing out
                }
            }
        };
    }

    private void addUser(){
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        Task<AuthResult> authResultTask = mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(CheckEmail.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(CheckEmail.this, "Регистрация успешна", Toast.LENGTH_LONG).show();

                    createUserInDb();

                    Intent intent = new Intent(CheckEmail.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(CheckEmail.this, "Регистрация провалена", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void sendEmail() {
        // генерим код
        int kod = 1000 + (int) (Math.random() * 9999);
        mKod = Integer.toString(kod);


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(CheckEmail.this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("kodOfEmail", Integer.toString(kod));
        editor.apply();

        String subject = "Подтвердите E-mail адрес";
        String message = "Уважаемый представитель учебного заведения\n\n" +
                "Ваш E-mail адрес был использован для входа в аккаунт представителя учебного заведения в приложении AntiTextBook.\n\n\n" +
                "Код подтверждения:\n\n" + kod + "\n\nВ случае, если вы не входили в учетную запись с данным E-mail адресом, пожалуйста, проигнорируйте письмо.";

        SendMail sm = new SendMail(CheckEmail.this, email, subject, message);
        sm.execute();
    }
}
