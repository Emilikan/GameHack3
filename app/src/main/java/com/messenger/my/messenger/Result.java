package com.messenger.my.messenger;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Result extends Fragment {
    private DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
    private ArrayList<String> arr = new ArrayList<>();
    private ArrayList<String> allClass = new ArrayList<>();
    private String[] array = new String[3];

    private EditText mDate;

    private TextView dateBD;
    private TextView winnBd;

    private int summ1 = 0;
    private int summ2 = 0;
    private int summ3 = 0;

    private int[] summIn = new int[3];

    private ArrayList<Integer> mSumm= new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_result, container, false);
        mDate = rootView.findViewById(R.id.mDate);
        dateBD = rootView.findViewById(R.id.dateBD);
        winnBd = rootView.findViewById(R.id.winBD);

        Button stopBattles = rootView.findViewById(R.id.stopBtl);
        stopBattles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        array[0] = "10a";
                        array[1] = "10b";
                        array[2] = "10c";
                        summIn[0] = 0;
                        summIn[1] = 0;
                        summIn[2] = 0;
                        String cou = dataSnapshot.child("Schools").child("classCounter").getValue(String.class);
                        int f = Integer.parseInt(cou);
                        f++;

                        for (int i = 0; i < f; i++){
                            allClass.add(dataSnapshot.child("Schools").child("AllClass").child("i").getValue(String.class));
                        }

                        for(int i = 0; i < f; i++){

                            String cou2 = dataSnapshot.child("Schools").child(array[i]).child("counterOfUsers").getValue(String.class);
                            int f2 = Integer.parseInt(cou2);
                            f2++;


                            for(int d = 0; d < f2; d++){
                                String mPointBio = dataSnapshot.child("Schools").child(array[i]).child(d+"").child("Points").child("Bio").getValue(String.class);
                                String mPointInf = dataSnapshot.child("Schools").child(array[i]).child(d+"").child("Points").child("Informatics").getValue(String.class);
                                String mPointMath = dataSnapshot.child("Schools").child(array[i]).child(d+"").child("Points").child("Math").getValue(String.class);
                                String mPointPhysics = dataSnapshot.child("Schools").child(array[i]).child(d+"").child("Points").child("Physics").getValue(String.class);

                                int mIntPB = 0;
                                int mIntInf = 0;
                                int mIntMath = 0;
                                int mIntPhysics = 0;

                                if(mPointBio != null) {
                                    mIntPB = Integer.parseInt(mPointBio);
                                }
                                if(mPointInf != null) {
                                    mIntInf = Integer.parseInt(mPointInf);
                                }
                                if(mPointMath != null) {
                                    mIntMath = Integer.parseInt(mPointMath);
                                }
                                if(mPointPhysics != null) {
                                    mIntPhysics = Integer.parseInt(mPointPhysics);
                                }

                                //mSumm.set(i, mSumm.get(i) + mIntInf + mIntMath + mIntPB + mIntPhysics);

                                summIn[i] = summIn[i] + mIntInf + mIntMath + mIntPB + mIntPhysics;
                            }

                        }

                        /*for (int i = 0; i < f; i++){

                        }

                        Collections.sort(mSumm);
                        Collections.reverse(mSumm);

                        for (int i = 0; i < f; i++){
                            mRef.child("Schools").child("Win").child("Date").setValue(mDate.getText().toString().trim());
                            mRef.child("Schools").child("Win").child("Team" + i).setValue();
                            mRef.child("Schools").child("Win").child("Team2").setValue(null);
                            mRef.child("Schools").child("Win").child("Team3").setValue(null);
                        }
                        */

                        summ1 = summIn[0];
                        summ2 = summIn[1];
                        summ3 = summIn[2];

                        if(summ1 >= summ2 && summ2 >= summ3){
                            mRef.child("Schools").child("Win").child("Date").setValue(mDate.getText().toString().trim());
                            mRef.child("Schools").child("Win").child("Team").setValue("10a");
                            mRef.child("Schools").child("Win").child("Team2").setValue("10b");
                            mRef.child("Schools").child("Win").child("Team3").setValue("10c");
                        } else if(summ1 >= summ3 && summ3 >= summ2){
                            mRef.child("Schools").child("Win").child("Date").setValue(mDate.getText().toString().trim());
                            mRef.child("Schools").child("Win").child("Team").setValue("10a");
                            mRef.child("Schools").child("Win").child("Team2").setValue("10c");
                            mRef.child("Schools").child("Win").child("Team3").setValue("10b");
                        } else if(summ2 >= summ1 && summ1 >= summ3){
                            mRef.child("Schools").child("Win").child("Date").setValue(mDate.getText().toString().trim());
                            mRef.child("Schools").child("Win").child("Team").setValue("10b");
                            mRef.child("Schools").child("Win").child("Team2").setValue("10a");
                            mRef.child("Schools").child("Win").child("Team3").setValue("10c");
                        } else if(summ2 >= summ3 && summ3 >= summ1){
                            mRef.child("Schools").child("Win").child("Date").setValue(mDate.getText().toString().trim());
                            mRef.child("Schools").child("Win").child("Team").setValue("10b");
                            mRef.child("Schools").child("Win").child("Team2").setValue("10c");
                            mRef.child("Schools").child("Win").child("Team3").setValue("10a");
                        } else if(summ3 >= summ1 && summ1 >= summ2){
                            mRef.child("Schools").child("Win").child("Date").setValue(mDate.getText().toString().trim());
                            mRef.child("Schools").child("Win").child("Team").setValue("10c");
                            mRef.child("Schools").child("Win").child("Team2").setValue("10a");
                            mRef.child("Schools").child("Win").child("Team3").setValue("10b");
                        } else {
                            mRef.child("Schools").child("Win").child("Date").setValue(mDate.getText().toString().trim());
                            mRef.child("Schools").child("Win").child("Team").setValue("10c");
                            mRef.child("Schools").child("Win").child("Team2").setValue("10b");
                            mRef.child("Schools").child("Win").child("Team3").setValue("10a");
                        }

                        dateBD.setText(dataSnapshot.child("Schools").child("Win").child("Date").getValue(String.class));
                        winnBd.setText(dataSnapshot.child("Schools").child("Win").child("Team").getValue(String.class));

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
