package com.messenger.my.messenger;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Objects;


public class TestQuestions extends Fragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_test_questions, container, false);

        final EditText mQu1 = rootView.findViewById(R.id.question1);
        final EditText mQu2 = rootView.findViewById(R.id.question2);
        final EditText mQu1Ans1 = rootView.findViewById(R.id.variant1_1);
        final EditText mQu1Ans2 = rootView.findViewById(R.id.variant1_2);
        final EditText mQu1Ans3 = rootView.findViewById(R.id.variant1_3);
        final EditText mQu2Ans1 = rootView.findViewById(R.id.variant2_1);
        final EditText mQu2Ans2 = rootView.findViewById(R.id.variant2_2);
        final EditText mQu2Ans3 = rootView.findViewById(R.id.variant2_3);

        Button button = rootView.findViewById(R.id.upload);
        button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("Question1", mQu1.getText().toString().trim());
                editor.putString("Question1_1", mQu1Ans1.getText().toString().trim());
                editor.putString("Question1_2", mQu1Ans2.getText().toString().trim());
                editor.putString("Question1_3", mQu1Ans3.getText().toString().trim());
                editor.putString("Question2", mQu2.getText().toString().trim());
                editor.putString("Question2_1", mQu2Ans1.getText().toString().trim());
                editor.putString("Question2_2", mQu2Ans2.getText().toString().trim());
                editor.putString("Question2_3", mQu2Ans3.getText().toString().trim());
                editor.apply();

                Toast.makeText(getContext(), "Тест успешно загружен!", Toast.LENGTH_LONG ).show();
                Fragment fragment = new Profile();
                FragmentTransaction fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
                fragmentManager.replace(R.id.container2, fragment).commit();
            }
        });


        return rootView;
    }
}
