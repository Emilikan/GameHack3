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

import java.util.Objects;


public class Test extends Fragment {

    EditText mName, mSub, mPage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_test, container, false);
        Bundle bundle = getArguments();
        String counterOfFragment = "0";
        if (bundle != null) {
            counterOfFragment = bundle.getString("Value", "0");
        }

        mName = rootView.findViewById(R.id.name);
        mPage = rootView.findViewById(R.id.page);
        mSub = rootView.findViewById(R.id.subject);



        Button button = rootView.findViewById(R.id.next_test);
        button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("TeachersTestName", mName.getText().toString().trim());
                editor.putString("TeachersTestPage", mPage.getText().toString().trim());
                editor.putString("TeachersTestSubject", mSub.getText().toString().trim());
                editor.apply();

                Fragment fragment = new TestQuestions();
                FragmentTransaction fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
                fragmentManager.replace(R.id.container2, fragment).commit();
            }
        });
        return rootView;
    }


}
