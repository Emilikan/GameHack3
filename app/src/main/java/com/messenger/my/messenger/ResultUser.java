package com.messenger.my.messenger;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ResultUser extends Fragment {

    TextView number1;
    TextView number2;
    TextView number3;
    TextView date;

    private DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_result_user, container, false);
        number1 = rootView.findViewById(R.id.number1);
        number2 = rootView.findViewById(R.id.number2);
        number3 = rootView.findViewById(R.id.number3);
        date = rootView.findViewById(R.id.dateMMM);

        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                number1.setText("Первое место: " + dataSnapshot.child("Schools").child("Win").child("Team").getValue(String.class));
                number2.setText("Второе место: " + dataSnapshot.child("Schools").child("Win").child("Team2").getValue(String.class));
                number3.setText("Третье место: " + dataSnapshot.child("Schools").child("Win").child("Team3").getValue(String.class));
                date.setText("Дата: " + dataSnapshot.child("Schools").child("Win").child("Date").getValue(String.class));
            }

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


        return rootView;
    }
}
