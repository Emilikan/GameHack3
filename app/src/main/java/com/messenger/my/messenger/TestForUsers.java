package com.messenger.my.messenger;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class TestForUsers extends Fragment {

    private TextView question;
    private RadioButton radioButton1;
    private RadioButton radioButton2;
    private RadioButton radioButton3;
    private RadioButton radioButton4;
    private String mAnswer;

    private String numberBook;
    private String numberTest;
    private String numberQuestion;
    private String subject;

    private String answTrue = "";
    private String dbAnsv1 = "";
    private String dbAnsv2 = "";
    private String dbAnsv3 = "";

    private String mClass;
    private String mNumber;

    private DatabaseReference mRef;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_test_for_users, container, false);

        question = rootView.findViewById(R.id.textView2);
        radioButton1 = rootView.findViewById(R.id.radioButton);
        radioButton2 = rootView.findViewById(R.id.radioButton2);
        radioButton3 = rootView.findViewById(R.id.radioButton3);
        radioButton4 = rootView.findViewById(R.id.radioButton4);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        numberBook = preferences.getString("thisBook", "0");
        numberTest = preferences.getString("thisTest", "0");
        numberQuestion = preferences.getString("thisQuestion", "0");
        mClass = preferences.getString("userClass", "10a");
        mNumber = preferences.getString("userNumber", "0");
        subject = preferences.getString("thisSubj", "Physics");

        final RadioGroup radioGroup = rootView.findViewById(R.id.fffdf);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case -1:
                        mAnswer = "";
                        Toast.makeText(getActivity(), "Ничего не выбранно", Toast.LENGTH_LONG).show();
                        break;
                    case R.id.radioButton:
                        mAnswer = radioButton1.getText().toString();
                        break;
                    case R.id.radioButton2:
                        mAnswer = radioButton2.getText().toString();
                        break;
                    case R.id.radioButton3:
                        mAnswer = radioButton3.getText().toString();
                        break;
                    case R.id.radioButton4:
                        mAnswer = radioButton4.getText().toString();
                        break;
                }
            }
        });

        mRef = FirebaseDatabase.getInstance().getReference();
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                question.setText(dataSnapshot.child("Books").child(numberBook).child("Tests").child(numberTest).child(numberQuestion).child("Description").getValue(String.class));

                radioButton1.setText(dataSnapshot.child("Books").child(numberBook).child("Tests").child(numberTest).child(numberQuestion).child("Answer").getValue(String.class));
                radioButton2.setText(dataSnapshot.child("Books").child(numberBook).child("Tests").child(numberTest).child(numberQuestion).child("FalseAnswer1").getValue(String.class));
                radioButton3.setText(dataSnapshot.child("Books").child(numberBook).child("Tests").child(numberTest).child(numberQuestion).child("FalseAnswer2").getValue(String.class));
                radioButton4.setText(dataSnapshot.child("Books").child(numberBook).child("Tests").child(numberTest).child(numberQuestion).child("FalseAnswer3").getValue(String.class));

                answTrue = dataSnapshot.child("Books").child(numberBook).child("Tests").child(numberTest).child(numberQuestion).child("Answer").getValue(String.class);
                dbAnsv1 = dataSnapshot.child("Books").child(numberBook).child("Tests").child(numberTest).child(numberQuestion).child("FalseAnswer1").getValue(String.class);
                dbAnsv2 = dataSnapshot.child("Books").child(numberBook).child("Tests").child(numberTest).child(numberQuestion).child("FalseAnswer2").getValue(String.class);
                dbAnsv3 = dataSnapshot.child("Books").child(numberBook).child("Tests").child(numberTest).child(numberQuestion).child("FalseAnswer3").getValue(String.class);
            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                AlertDialog.Builder ad;
                ad = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
                ad.setTitle("Error");  // заголовок
                ad.setMessage("Ошибка: " + databaseError.getMessage() + "\n Проблемы на серверной части. Можете сообщить в службу поддержки"); // сообщение
                ad.setPositiveButton("Служба поддержки", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        Fragment fragment = new Send();
                        FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
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
        });

        Button next = rootView.findViewById(R.id.button);
        next.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                if (mAnswer.equals("")) {
                    AlertDialog.Builder ad;
                    ad = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
                    ad.setTitle("Error");  // заголовок
                    ad.setMessage("Пожалуйста, выберите ответ"); // сообщение
                    ad.setPositiveButton("Служба поддержки", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int arg1) {
                            Fragment fragment = new Send();
                            FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
                        }
                    });
                    ad.setNegativeButton("Закрыть", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int arg1) {
                            dialog.cancel();
                        }
                    });
                    ad.setCancelable(true);
                    ad.show();
                } else if (mAnswer.equals(answTrue)) {
                    mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String co = dataSnapshot.child("Schools").child(mClass).child(mNumber).child("Points").child(subject).getValue(String.class);
                            int intCo;
                            if (co != null) {
                                intCo = Integer.parseInt(co);
                            } else {
                                intCo = 0;
                            }
                            intCo += 5;
                            mRef.child("Schools").child(mClass).child(mNumber).child("Points").child(subject).setValue(Integer.toString(intCo));

                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                            SharedPreferences.Editor editor = preferences.edit();
                            int k = Integer.parseInt(numberQuestion);
                            k++;
                            editor.putString("thisQuestion", Integer.toString(k));
                            editor.apply();

                            String co1 = dataSnapshot.child("Books").child(numberBook).child("Tests").child(numberTest).child("counter").getValue(String.class);
                            Toast.makeText(getContext(), "Правильный ответ", Toast.LENGTH_SHORT).show();
                            if (!co1.equals(numberQuestion)) {
                                Fragment fragment = new TestForUsers();
                                FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
                                fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
                            } else {
                                Fragment fragment = new Battles();
                                FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
                                fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                } else {
                    mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String co1 = dataSnapshot.child("Books").child(numberBook).child("Tests").child(numberTest).child("counter").getValue(String.class);


                            String co = dataSnapshot.child("Schools").child(mClass).child(mNumber).child("Points").child(subject).getValue(String.class);
                            int intCo;
                            if (co != null) {
                                intCo = Integer.parseInt(co);
                                intCo -= 5;
                            } else {
                                intCo = 0;
                            }
                            mRef.child("Schools").child(mClass).child(mNumber).child("Points").child(subject).setValue(Integer.toString(intCo));

                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                            SharedPreferences.Editor editor = preferences.edit();
                            int k = Integer.parseInt(numberQuestion);
                            k++;
                            editor.putString("thisQuestion", Integer.toString(k));
                            editor.apply();
                            Toast.makeText(getContext(), "Неправильный ответ", Toast.LENGTH_SHORT).show();
                            if (!co1.equals(numberQuestion)) {
                                Fragment fragment = new TestForUsers();
                                FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
                                fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
                            } else {
                                Fragment fragment = new Battles();
                                FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
                                fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }
        });

        return rootView;
    }
}
