package com.messenger.my.messenger;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class TestQuestions extends Fragment {

    String counterOfFragment;
    Integer counter = 1;
    EditText answer, question, falseAnswer1, falseAnswer2, falseAnswer3, mName;;
    String counterOfTests;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_test_questions, container, false);

        answer = rootView.findViewById(R.id.answer);
        question = rootView.findViewById(R.id.question1);
        falseAnswer1 = rootView.findViewById(R.id.var1);
        falseAnswer2 = rootView.findViewById(R.id.var2);
        falseAnswer3 = rootView.findViewById(R.id.var3);

        Bundle bundle = getArguments();
        counterOfFragment = "0";
        if (bundle != null) {
            counterOfFragment = bundle.getString("Value", "0");
        }

        mName = rootView.findViewById(R.id.name);



        Button button = rootView.findViewById(R.id.upload);
        button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {

                answer = rootView.findViewById(R.id.answer);
                question = rootView.findViewById(R.id.question1);


                // объявляем переменную
                final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();

// сохраняем данные
                mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                        mRef.child("Books").child(counterOfFragment).child("Tests").child(counter.toString()).child("Answer").setValue(answer);
                        String count_of_tests = preferences.getString("counterOfTests", null);
                        mRef.child("Books").child(counterOfFragment).child("Tests").child(counter.toString()).child("Question").setValue(question);
                        if (count_of_tests == null) {
                            mRef.child("Books").child(counterOfFragment).child("Tests").child(counter.toString()).child("counter").setValue(counter);
                            counterOfTests = dataSnapshot.child("Books").child(counterOfFragment).child("Tests").child("counter").getValue(String.class);

                            Integer var = Integer.parseInt(counterOfTests);
                            var++;
                            counterOfTests = Integer.toString(var);
                        }


                        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("counterOfTests", null);
                        editor.apply();

                        String counterOfQuestions = dataSnapshot.child("Books").child(counterOfFragment).child("Tests").child(counterOfTests).child("counter").getValue(String.class);
                        if (counterOfQuestions == null) {
                            counterOfQuestions = "0";
                        }
                        int h = Integer.parseInt(counterOfQuestions);
                        h++;
                        mRef.child("Books").child(counterOfFragment).child("Tests").child(counterOfTests).child(counterOfQuestions).child("Answer").setValue(answer.getText().toString().trim());
                        mRef.child("Books").child(counterOfFragment).child("Tests").child(counterOfTests).child("Name").setValue(mName);
                        mRef.child("Books").child(counterOfFragment).child("Tests").child(counterOfTests).child(counterOfQuestions).child("Description").setValue(question.getText().toString().trim());
                        mRef.child("Books").child(counterOfFragment).child("Tests").child(counterOfTests).child(counterOfQuestions).child("FalseAnswer1").setValue(falseAnswer1.getText().toString().trim());
                        mRef.child("Books").child(counterOfFragment).child("Tests").child(counterOfTests).child(counterOfQuestions).child("FalseAnswer2").setValue(falseAnswer2.getText().toString().trim());
                        mRef.child("Books").child(counterOfFragment).child("Tests").child(counterOfTests).child(counterOfQuestions).child("FalseAnswer3").setValue(falseAnswer3.getText().toString().trim());
                        mRef.child("Books").child(counterOfFragment).child("Tests").child(counterOfTests).child("counter").setValue(Integer.toString(h));
                        Toast.makeText(getContext(), "Тест успешно загружен!", Toast.LENGTH_LONG).show();

                        Toast.makeText(getContext(),"Тест успешно загружен!", Toast.LENGTH_LONG).show();
                        Fragment fragment = new Profile();
                        FragmentTransaction fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
                        fragmentManager.replace(R.id.container2, fragment).commit();

                    }
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
        });




        Button button2 = rootView.findViewById(R.id.next_test);
        button2.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                answer = rootView.findViewById(R.id.answer);
                question = rootView.findViewById(R.id.question1);
                falseAnswer1 = rootView.findViewById(R.id.var1);
                falseAnswer2 = rootView.findViewById(R.id.var2);
                falseAnswer3 = rootView.findViewById(R.id.var3);
                Bundle bundle = getArguments();
                counterOfFragment = "0";
                if (bundle != null) {
                    counterOfFragment = bundle.getString("Value", "0");
                }

                mName = rootView.findViewById(R.id.name);
                        // объявляем переменную
                        final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();


                        // сохраняем данные
                        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                                String count_of_tests = preferences.getString("counterOfTests", null);
                                if (count_of_tests == null) {
                                    counterOfTests = dataSnapshot.child("Books").child(counterOfFragment).child("Tests").child("counter").getValue(String.class);
                                    assert counterOfTests != null;
                                    Integer var = Integer.parseInt(counterOfTests);
                                    var++;
                                    counterOfTests = Integer.toString(var);
                                }


                                preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("counterOfTests", counterOfTests);
                                editor.apply();

                                Toast.makeText(getContext(), counterOfFragment, Toast.LENGTH_SHORT).show();
                                String counterOfQuestions = dataSnapshot.child("Books").child(counterOfFragment).child("Tests").child(counterOfTests).child("counter").getValue(String.class);
                                if (counterOfQuestions == null) {
                                    counterOfQuestions = "0";
                                }
                                int h = Integer.parseInt(counterOfQuestions);
                                h++;
                                mRef.child("Books").child(counterOfFragment).child("Tests").child(counterOfTests).child(counterOfQuestions).child("Answer").setValue(answer.getText().toString().trim());
                                mRef.child("Books").child(counterOfFragment).child("Tests").child(counterOfTests).child("Name").setValue(mName);
                                mRef.child("Books").child(counterOfFragment).child("Tests").child(counterOfTests).child(counterOfQuestions).child("Description").setValue(question.getText().toString().trim());
                                mRef.child("Books").child(counterOfFragment).child("Tests").child(counterOfTests).child(counterOfQuestions).child("FalseAnswer1").setValue(falseAnswer1.getText().toString().trim());
                                mRef.child("Books").child(counterOfFragment).child("Tests").child(counterOfTests).child(counterOfQuestions).child("FalseAnswer2").setValue(falseAnswer2.getText().toString().trim());
                                mRef.child("Books").child(counterOfFragment).child("Tests").child(counterOfTests).child(counterOfQuestions).child("FalseAnswer3").setValue(falseAnswer3.getText().toString().trim());
                                mRef.child("Books").child(counterOfFragment).child("Tests").child(counterOfTests).child("counter").setValue(Integer.toString(h));
                                Toast.makeText(getContext(), "Тест успешно загружен!", Toast.LENGTH_LONG).show();

                                Fragment fragment = new TestQuestions();
                                FragmentTransaction fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
                                fragmentManager.replace(R.id.container2, fragment).commit();
                                Toast.makeText(getContext(), "Тест успешно загружен!", Toast.LENGTH_LONG).show();
                            }

                            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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


        });


        return rootView;
    }




}
