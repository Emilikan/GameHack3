package com.messenger.my.messenger;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.Objects;


public class Map extends android.support.v4.app.Fragment {
    private ArrayList<String> mStudentsFiz = new ArrayList<>();
    private ArrayList<String> mStudentsBio = new ArrayList<>();
    private ArrayList<String> mStudentsInf = new ArrayList<>();
    private ArrayList<String> mStudentsMath = new ArrayList<>();
    private ListView PhysStudents;
    private ListView BioStudents;
    private ListView InfStudents;
    private ListView MathStudents;
    private DatabaseReference mRef;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View RootView = inflater.inflate(R.layout.fragment_map, container, false);
        PhysStudents = RootView.findViewById(R.id.fiz);
        BioStudents = RootView.findViewById(R.id.bio);
        InfStudents = RootView.findViewById(R.id.inf);
        MathStudents = RootView.findViewById(R.id.math);

        mRef = FirebaseDatabase.getInstance().getReference();
        mRef.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                String clas = preferences.getString("userClass", "");

                assert clas != null;
                int student = Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("Schools").child(clas).child("counterOfUsers").getValue(String.class)));


                for (int t = 0; t <= student; t++) {
                    if(dataSnapshot.child("Schools").child(clas).child(Integer.toString(t)).child("Points").child("Physics").getValue(String.class) != null) {
                        if (!(Objects.requireNonNull(dataSnapshot.child("Schools").child(clas).child(Integer.toString(t)).child("Points").child("Physics").getValue(String.class)).equals("0"))) {
                            mStudentsFiz.add(dataSnapshot.child("Schools").child(clas).child(String.valueOf(t)).child("Profile").child("Name").getValue(String.class) + " " + dataSnapshot.child("Schools").child(clas).child(String.valueOf(t)).child("Points").child("Physics").getValue(String.class));
                        }
                    }

                    if(dataSnapshot.child("Schools").child(clas).child(Integer.toString(t)).child("Points").child("Bio").getValue(String.class) != null) {

                        if (!(Objects.requireNonNull(dataSnapshot.child("Schools").child(clas).child(Integer.toString(t)).child("Points").child("Bio").getValue(String.class)).equals("0")
                                && Objects.requireNonNull(dataSnapshot.child("Schools").child(clas).child(Integer.toString(t)).child("Points").child("Bio").getValue(String.class)) != null)) {
                            mStudentsBio.add(dataSnapshot.child("Schools").child(clas).child(String.valueOf(t)).child("Profile").child("Name").getValue(String.class) + " " + dataSnapshot.child("Schools").child(clas).child(String.valueOf(t)).child("Points").child("Bio").getValue(String.class));
                        }
                    }

                    if(dataSnapshot.child("Schools").child(clas).child(Integer.toString(t)).child("Points").child("Math").getValue(String.class) != null) {
                        if (!(Objects.requireNonNull(dataSnapshot.child("Schools").child(clas).child(Integer.toString(t)).child("Points").child("Math").getValue(String.class)).equals("0")
                                && Objects.requireNonNull(dataSnapshot.child("Schools").child(clas).child(Integer.toString(t)).child("Points").child("Math").getValue(String.class)) != null)) {
                            mStudentsMath.add(dataSnapshot.child("Schools").child(clas).child(String.valueOf(t)).child("Profile").child("Name").getValue(String.class) + " " + dataSnapshot.child("Schools").child(clas).child(String.valueOf(t)).child("Points").child("Math").getValue(String.class));
                        }
                    }
                    if(dataSnapshot.child("Schools").child(clas).child(Integer.toString(t)).child("Points").child("Informatics").getValue(String.class) != null) {
                        if (!(Objects.requireNonNull(dataSnapshot.child("Schools").child(clas).child(Integer.toString(t)).child("Points").child("Informatics").getValue(String.class)).equals("0"))) {
                            mStudentsInf.add(dataSnapshot.child("Schools").child(clas).child(String.valueOf(t)).child("Profile").child("Name").getValue(String.class) + " " + dataSnapshot.child("Schools").child(clas).child(String.valueOf(t)).child("Points").child("Informatics").getValue(String.class));
                        }
                    }
                }


                updateUI();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Error" + databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        return RootView;
    }

    public void updateUI() {
        if (getActivity() != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.list_text_view2, mStudentsFiz);
            PhysStudents.setAdapter(adapter);
            ArrayAdapter<String> adapter1 = new ArrayAdapter<>(getActivity(), R.layout.list_text_view2, mStudentsBio);
            BioStudents.setAdapter(adapter1);
            ArrayAdapter<String> adapter2 = new ArrayAdapter<>(getActivity(), R.layout.list_text_view2, mStudentsInf);
            InfStudents.setAdapter(adapter2);
            ArrayAdapter<String> adapter3 = new ArrayAdapter<>(getActivity(), R.layout.list_text_view2, mStudentsMath);
            MathStudents.setAdapter(adapter3);


        }
    }
}

