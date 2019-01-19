package com.messenger.my.messenger;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static java.lang.String.valueOf;

public class AboutBook extends Fragment {

    private TextView mPart2;
    private TextView mAuthor2;
    private TextView mProject2;
    private TextView mClass2;
    private TextView mYear2;
    private TextView mDescribing;
    private ImageView imageView;

    public String counterOfFragment;
    private Uri pdfFilePath = null;

    private ProgressBar progressBar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_about_book, container, false);


        progressBar = rootView.findViewById(R.id.progressBarInAboutBook);
        progressBar.setVisibility(ProgressBar.VISIBLE);

        if(!isOnline(Objects.requireNonNull(getContext()))){
            AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
            builder.setTitle("Warning")
                    .setMessage("Нет доступа в интернет. Проверьте наличие связи")
                    .setCancelable(false)
                    .setNegativeButton("Ок, закрыть",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();

                                    Fragment fragment = new Chats();
                                    FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
                                    fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
                                    Toast.makeText(getActivity(), "Нет книг", Toast.LENGTH_SHORT).show();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        }
        else {
            // подписываем пользователя на тему (для получаения push уведомлений)
            FirebaseMessaging.getInstance().subscribeToTopic("ForAllUsers1");

            // получаем значение о том, на какую книгу мы перешли (из Bundle)
            Bundle bundle = getArguments();
            counterOfFragment = "0";
            if (bundle != null) {
                counterOfFragment = bundle.getString("Value", "0");
            }

            imageView = rootView.findViewById(R.id.imageView3);
            mPart2 = rootView.findViewById(R.id.Part2);
            mAuthor2 = rootView.findViewById(R.id.Author2);
            mProject2 = rootView.findViewById(R.id.Project2);
            mDescribing = rootView.findViewById(R.id.describingAboutBook);
            mClass2 = rootView.findViewById(R.id.Class2);
            mYear2 = rootView.findViewById(R.id.Year2);
            Button upload2 = rootView.findViewById(R.id.upload2);
            ImageView back = rootView.findViewById(R.id.back2);

            // кнопка скачать книгу
            upload2.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onClick(View v) {

                    // скачивание книги в отдельном потоке
                    Thread download = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final Context context = getContext();
                            final String contFrag = counterOfFragment;

                            final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();

                            // устанавливаем большее значение в топе загрузок
                            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String dbCounter = dataSnapshot.child("Books").child(contFrag).child("TopDownloads").getValue(String.class);
                                    assert dbCounter != null;
                                    int intCounter = Integer.parseInt(dbCounter);
                                    intCounter++;
                                    String stringCounter = Integer.toString(intCounter);
                                    mRef.child("Books").child(contFrag).child("TopDownloads").setValue(stringCounter);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    AlertDialog.Builder ad;
                                    ad = new AlertDialog.Builder(context);
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

                            // получаем данные только 1 раз (не следит за изменениями)
                            // это сделано, чтобы не вылетало, когда в бд добавляются книги
                            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    StorageReference islandRef = FirebaseStorage.getInstance().getReferenceFromUrl(Objects.requireNonNull(dataSnapshot.child("Books").child(contFrag).child("Pdf").getValue(String.class)));

                                    String nameOfFileInTelephone = dataSnapshot.child("Books").child(contFrag).child("Author").getValue(String.class) + " " + dataSnapshot.child("Books").child(contFrag).child("Describing").getValue(String.class) + " " + dataSnapshot.child("Books").child(contFrag).child("Class").getValue(String.class)
                                            + " " + dataSnapshot.child("Books").child(contFrag).child("Subject").getValue(String.class) + " " + dataSnapshot.child("Books").child(contFrag).child("Part").getValue(String.class)
                                            + " " + dataSnapshot.child("Books").child(contFrag).child("Year").getValue(String.class);

                                    final File localFile = saveFile(Objects.requireNonNull(getContext()).getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/" + nameOfFileInTelephone + ".pdf");

                                    assert localFile != null;
                                    final ProgressDialog progressDialog = new ProgressDialog(context);
                                    progressDialog.setTitle("Downloading");
                                    progressDialog.show();
                                    islandRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                            progressDialog.dismiss();

                                            // в переменной pdfFilePath хранится Uri скаченного файла. Передавать его в Home.class, записывать его в файл настроек или SharedPreferences
                                            if (localFile.toURI() != null) {
                                                pdfFilePath = Uri.parse(localFile.toURI() + "");

                                                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                                                SharedPreferences.Editor editor = preferences.edit();
                                                editor.putString("URI", valueOf(pdfFilePath));
                                                editor.apply();
                                            }

                                            // тут у нас проверка на то, ушел ли пользователь с активити. Если не ушел, то мы сразу открываем книгу, если ушел, то оповещаем его
                                            if(getActivity() != null) {
                                                Fragment fragment = new Home();
                                                FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
                                                fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
                                                Toast.makeText(context, "Файл скачан", Toast.LENGTH_LONG).show();
                                            }
                                            else {
                                                //* написать код для открытия сразу библиотеки
                                                AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(context));
                                                builder.setTitle("Информация")
                                                        .setMessage("Файл скачан")
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
                                    }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                                        @Override
                                        public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                            // получаем проценты загрузки
                                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                            // отображаем диалог с процентами
                                            progressDialog.setMessage("Downloaded " + ((int) progress) + "%...");
                                        }
                                    });

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    AlertDialog.Builder ad;
                                    ad = new AlertDialog.Builder(context);
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
                    });
                    download.start();
                }

            });

            // изображение-кнопка назад
            back.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onClick(View view) {
                    Fragment fragment = null;
                    Class fragmentClass;
                    fragmentClass = DownloadFromCloud.class;
                    try {
                        fragment = (Fragment) fragmentClass.newInstance();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
                    assert fragment != null;
                    fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
                }
            });

            changeText();
        }
        return rootView;
    }

    // проверям наличие интернета
    private static boolean isOnline (Context context)
    {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    // изменяем текст на тот, который получили из бд
    @SuppressLint("WrongViewCast")
    public void changeText() {
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
        mRef.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                if(dataSnapshot.child("Books").child(counterOfFragment).child("Icon").getValue(String.class) != null) {


                    StorageReference storageRef = storage.getReferenceFromUrl(Objects.requireNonNull(dataSnapshot.child("Books").child(counterOfFragment).child("Icon").getValue(String.class)));
                    storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // проверка на то, есть ли активность еще эта для того, чтобы прога не вылетала, если еще что-то не успело загрузиться, а мы уже вышли из активити
                            if (getActivity() != null) {
                                Picasso.with(getContext()).load(uri).into(imageView);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast toast = Toast.makeText(getContext(), "Ошибка!", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    });
                }

                mPart2.setText("Часть: " + dataSnapshot.child("Books").child(counterOfFragment).child("Part").getValue(String.class));
                mAuthor2.setText("Автор: " +dataSnapshot.child("Books").child(counterOfFragment).child("Author").getValue(String.class));
                mProject2.setText("Предмет: " +dataSnapshot.child("Books").child(counterOfFragment).child("Subject").getValue(String.class));
                mClass2.setText("Класс: " + dataSnapshot.child("Books").child(counterOfFragment).child("Class").getValue(String.class));
                mYear2.setText("Год: " + dataSnapshot.child("Books").child(counterOfFragment).child("Year").getValue(String.class));
                mDescribing.setText("Описание: " + dataSnapshot.child("Books").child(counterOfFragment).child("Describing").getValue(String.class));

                progressBar.setVisibility(ProgressBar.INVISIBLE); // убираем прогресс бар
            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // создаем диологовое окно с ошибкой
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

    // метод создания пустого файла
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public File saveFile (String filePath)
    {
        //Создание объекта файла.
        File fileHandle = new File(filePath);
        try
        {
            //Если нет директорий в пути, то они будут созданы:
            if (!fileHandle.getParentFile().exists())
                fileHandle.getParentFile().mkdirs();
            //Если файл существует, то он будет перезаписан:
            fileHandle.createNewFile();
            return fileHandle;
        }
        catch (IOException e)
        {
            AlertDialog.Builder ad;
            ad = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
            ad.setTitle("Error");  // заголовок
            ad.setMessage("Ошибка: " + e.getMessage() + "\n Проблемы в создании файла. Можете сообщить в службу поддержки"); // сообщение
            ad.setPositiveButton("Служба поддержки", new DialogInterface.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
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
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return fileHandle;
    }

}
