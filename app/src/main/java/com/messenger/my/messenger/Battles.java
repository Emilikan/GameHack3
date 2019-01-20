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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class Battles extends Fragment {
    private Spinner spinnerBooks;
    private DatabaseReference mRef;
    private ArrayList<String> books = new ArrayList<>();
    private ArrayList<String> tests = new ArrayList<>();
    private ArrayList<String> testsNumberBook = new ArrayList<>();
    private ArrayList<String> testsNumberTest = new ArrayList<>();
    String classOf;

    private ListView lll;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_battles, container, false);

        spinnerBooks = rootView.findViewById(R.id.spinnerSubjectInBattles);
        lll = rootView.findViewById(R.id.lll);

        lll.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("thisBook", testsNumberBook.get(position));
                editor.putString("thisTest", testsNumberTest.get(position));
                editor.putString("thisQuestion", "0");
                editor.putString("thisSubj", books.get(position));
                editor.apply();

                Fragment fragment = new TestForUsers();
                FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();

            }
        });



        mRef = FirebaseDatabase.getInstance().getReference();
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String co = dataSnapshot.child("counter").getValue(String.class);
                int intCo = Integer.parseInt(co);
                intCo++;
                for (int i = 0; i < intCo; i++){
                    books.add(dataSnapshot.child("Books").child(Integer.toString(i)).child("Subject").getValue(String.class));
                }

                ArrayAdapter<String> adapter5 = new ArrayAdapter<>(Objects.requireNonNull(getContext()), android.R.layout.simple_spinner_item, books);
                adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerBooks.setAdapter(adapter5);

                AdapterView.OnItemSelectedListener itemSelectedListenerForClass = new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        classOf = (String)parent.getItemAtPosition(position);
                        sortingBook1();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                };
                spinnerBooks.setOnItemSelectedListener(itemSelectedListenerForClass);



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


        return rootView;
    }

    public void updateUI() {
        if (getActivity() != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.list_text_view, tests);
            lll.setAdapter(adapter);
        }
    }

    private void sortingBook1 (){
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String co = dataSnapshot.child("counter").getValue(String.class);
                int intCo = Integer.parseInt(co);
                intCo++;
                for (int i = 0; i < intCo; i++){
                    if(dataSnapshot.child("Books").child(Integer.toString(i)).child("Subject").getValue(String.class).equals(classOf)){
                        String co1 = dataSnapshot.child("Books").child(Integer.toString(i)).child("Tests").child("counter").getValue(String.class);
                        int intCo1 = Integer.parseInt(co1);
                        intCo1++;
                        for(int dd = 0; dd < intCo1; dd++){
                            tests.add(dataSnapshot.child("Books").child(Integer.toString(i)).child("Tests").child(Integer.toString(dd)).child("Name").getValue(String.class));
                            testsNumberBook.add(Integer.toString(i));
                            testsNumberTest.add(Integer.toString(dd));
                        }
                    }
                }


                updateUI();
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
    }
}
