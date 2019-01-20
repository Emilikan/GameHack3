package com.messenger.my.messenger;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

/**
 * Класс для генерирования RecycleView на основе данных объекта класса BookForRecycle
 */

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView authorView;
        TextView subjectView;
        TextView describingView;
        TextView classView;
        String counterOfFragment;
        LinearLayout linearLayout;

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        ViewHolder(final View view){
            super(view);

            imageView = view.findViewById(R.id.imageForBook);

            linearLayout = view.findViewById(R.id.listText);
            authorView = view.findViewById(R.id.authorMain);
            subjectView = view.findViewById(R.id.subjectMain);
            describingView = view.findViewById(R.id.describingMain);
            classView = view.findViewById(R.id.classMain);
        }
    }

    private LayoutInflater inflater;
    private List<BookForRecycle> books;
    private ProgressBar progressBar;

    DataAdapter(Context context, List<BookForRecycle> books) {
        this.books = books;
        this.inflater = LayoutInflater.from(context);
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @NonNull
    public DataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycle_view, parent, false);
        progressBar = view.findViewById(R.id.progressBarInRecycle);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final DataAdapter.ViewHolder viewHolder, final int i) {

        final BookForRecycle book = books.get(i);
        if(book.getIsBook()) {
            viewHolder.counterOfFragment = book.getArrayList().get(i) + "";
            viewHolder.authorView.setText(book.getAuthor());
            viewHolder.describingView.setText(book.getDescribing());
            viewHolder.subjectView.setText(book.getSubject());
            viewHolder.classView.setText(book.getClassOfBook());

            // смотрим, откуда запрос (с админки или нет)
            if (!book.getIsAdmin()) {
                DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
                mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child("Books").child(Integer.toString(book.getArrayList().get(i))).child("Icon").getValue(String.class) != null) {

                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            StorageReference storageRef = storage.getReferenceFromUrl(Objects.requireNonNull(dataSnapshot.child("Books").child(Integer.toString(book.getArrayList().get(i))).child("Icon").getValue(String.class)));
                            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    progressBar.setVisibility(ProgressBar.INVISIBLE);
                                    Picasso.with(book.getContext()).load(uri).into(viewHolder.imageView);
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                // обработчик нажатия
                viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onClick(View v) {
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(book.getContext());
                        Boolean bool = preferences.getBoolean("DownloadingOfBook", false);
                        if(!bool) {


                            Fragment fragment = new AboutBook();
                            FragmentTransaction fragmentManager = book.getActivity().getSupportFragmentManager().beginTransaction();
                            fragmentManager.replace(R.id.container, fragment).commit();


                            Bundle bundle = new Bundle();
                            String valueOfReplace = Integer.toString(book.getArrayList().get(viewHolder.getAdapterPosition()));
                            bundle.putString("Value", valueOfReplace);
                            fragment.setArguments(bundle);
                        }

                        else{
                            Fragment fragment = new Test();
                            FragmentTransaction fragmentManager = book.getActivity().getSupportFragmentManager().beginTransaction();
                            fragmentManager.replace(R.id.container2, fragment).commit();

                            Bundle bundle = new Bundle();
                            String valueOfReplace = Integer.toString(book.getArrayList().get(viewHolder.getAdapterPosition()));
                            bundle.putString("Value", valueOfReplace);
                            fragment.setArguments(bundle);

                        }
                    }
                });
            } else {
                DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
                mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference storageRef = storage.getReferenceFromUrl(Objects.requireNonNull(dataSnapshot.child("forChecking").child("Books").child(Integer.toString(book.getArrayList()
                                .get(i))).child("Icon").getValue(String.class)));
                        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Picasso.with(book.getContext()).load(uri).into(viewHolder.imageView);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                // обработчик нажатия
                viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onClick(View v) {
                    }
                });
            }
        }
        else {
            viewHolder.authorView.setText(book.getAuthor());
            viewHolder.describingView.setText(book.getDescribing());
            viewHolder.subjectView.setText(book.getSubject());
            viewHolder.classView.setText(book.getClassOfBook());
        }

    }

    @Override
    public int getItemCount() {
        return books.size();
    }

}
