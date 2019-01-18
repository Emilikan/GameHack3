package com.messenger.my.messenger;

import android.content.Context;
import android.support.v4.app.FragmentActivity;

import java.util.ArrayList;

/**
 * Класс, который используется для создания RecycleView. Принимает различные переменные. Имеет несколько конструкторов для того, чтобы из разных мест можно было пользоваться одним классом
 */

public class BookForRecycle {
    private String author;
    private String subject;
    private String describing;
    private String classOfBook;
    private String imageForBook;
    private ArrayList<Integer> arrayList;
    private Context context;
    private FragmentActivity activity;
    private String nameOfSchool;
    private Boolean isAdmin = false;
    private Boolean isBook;

    BookForRecycle(String author, String subject, String describing, String classOfBook, String image,
                   ArrayList<Integer> arrayList, Context context, FragmentActivity activity, String nameOfSchool, Boolean isBook){
        this.author = author;
        this.arrayList = arrayList;
        this.subject = subject;
        this.describing = describing;
        this.classOfBook = classOfBook;
        this.imageForBook = image;
        this.context = context;
        this.activity = activity;
        this.isBook = isBook;
        this.nameOfSchool = nameOfSchool;
    }
    BookForRecycle(String author, String subject, String describing, String classOfBook, String image,
                   ArrayList<Integer> arrayList, Context context, FragmentActivity activity, Boolean isBook, Boolean isAdmin){
        this.author = author;
        this.arrayList = arrayList;
        this.subject = subject;
        this.describing = describing;
        this.classOfBook = classOfBook;
        this.imageForBook = image;
        this.context = context;
        this.activity = activity;
        this.isAdmin = isAdmin;
        this.isBook = isBook;
    }
    BookForRecycle(String author, String subject, String describing, String classOfBook, String image,
                   ArrayList<Integer> arrayList, Context context, FragmentActivity activity, Boolean isBook){
        this.author = author;
        this.arrayList = arrayList;
        this.subject = subject;
        this.describing = describing;
        this.classOfBook = classOfBook;
        this.imageForBook = image;
        this.context = context;
        this.activity = activity;
        this.isBook = isBook;
    }

    Boolean getIsBook(){
        return isBook;
    }

    Boolean getIsAdmin(){
        return isAdmin;
    }

    String getNameOfSchool(){
        return nameOfSchool;
    }

    public FragmentActivity getActivity(){
        return activity;
    }

    public Context getContext(){
        return context;
    }

    ArrayList<Integer> getArrayList(){
        return arrayList;
    }

    String getAuthor() {
        return author;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    String getDescribing() {
        return describing;
    }

    String getClassOfBook() {
        return classOfBook;
    }

    public String getImageForBook() {
        return imageForBook;
    }

    public void setImageForBook(String imageForBook) {
        this.imageForBook = imageForBook;
    }
}
