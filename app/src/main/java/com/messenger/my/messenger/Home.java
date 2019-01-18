package com.messenger.my.messenger;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnDrawListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.barteksc.pdfviewer.util.FitPolicy;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Objects;

public class Home extends Fragment {

    public int width;
    public int height;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    private Paint mBitmapPaint;
    Context context;
    private Paint circlePaint;
    private Path circlePath;
    private Paint mPaint;

    private int thisPageInt;

    private PDFView pdfView;

    private String uriOfPdf;

    private int color = Color.GREEN;
    private int thisWidth;
    private int thisHeight;

    private Boolean isReady = true;
    private Boolean isReadyN = false;

    private Bitmap[] mBit;

    private int depth = 12; // толщина линии по умолчанию

    private Boolean isNewPage = true;

    private BottomNavigationView menuVisibility;

    private ProgressBar progressBar;

    private BottomNavigationView.OnNavigationItemSelectedListener menu = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_dashboard:
                    mPaint = new Paint();
                    settingsDraw();
                    onClicks(true);
                    return true;

                case R.id.draw_menu:
                    onClicks(false);
                    return true;
                case R.id.draw_settings:
                    mPaint = new Paint();
                    settingsDraw();
                    AlertDialog.Builder ad;
                    ad = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
                    ad.setTitle("Настройки");  // заголовок
                    ad.setMessage("Настройте рисовалку"); // сообщение
                    ad.setPositiveButton("Цвет", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int arg1) {
                            ColorPickerDialog.OnColorChangedListener onColorChangedListener = new ColorPickerDialog.OnColorChangedListener() {
                                @Override
                                public void colorChanged(int newColor) {
                                    color = newColor;
                                    mPaint = new Paint();
                                    settingsDraw();
                                }
                            };
                            new ColorPickerDialog(getActivity(), onColorChangedListener, mPaint.getColor(), thisWidth, thisHeight).show();
                        }
                    });
                    ad.setNeutralButton("Ластик", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                            mPaint.setAlpha(0);
                            onClicks(true);
                        }
                    });
                    ad.setNegativeButton("Толщина кисти", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int arg1) {
                            final String[] mChoose = {"Тонкий", "Средний", "Толстый", "Очень толстый"};

                            AlertDialog.Builder ad;
                            ad = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
                            ad.setTitle("Толщина кисти");
                            ad.setCancelable(false);
                            ad.setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int arg1) {
                                    dialog.cancel();
                                }
                            });
                            ad.setSingleChoiceItems(mChoose, -1,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int item) {
                                            switch (item) {
                                                case 0:
                                                    depth = 12;
                                                    settingsDraw();
                                                    break;
                                                case 1:
                                                    depth = 20;
                                                    settingsDraw();
                                                    break;
                                                case 2:
                                                    depth = 40;
                                                    settingsDraw();
                                                    break;
                                                case 3:
                                                    depth = 80;
                                                    settingsDraw();
                                            }
                                        }
                                    });
                            ad.show();
                            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                            mPaint.setAlpha(0);
                        }
                    });
                    ad.setCancelable(true);
                    ad.show();
            }
            return true;
        }
    };

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView1 = inflater.inflate(R.layout.fragment_home, container, false);
        progressBar = rootView1.findViewById(R.id.progressBarInHome);

        mPaint = new Paint();

        mPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);

        mBit = new Bitmap[300];

        settingsDraw();

        menuVisibility = rootView1.findViewById(R.id.navigation);
        menuVisibility.setVisibility(View.INVISIBLE);

        menuVisibility.setOnNavigationItemSelectedListener(menu);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        uriOfPdf = preferences.getString("URI", "");
        String thisPageString = preferences.getString("thisPageString" + uriOfPdf.substring(Objects.requireNonNull(uriOfPdf).lastIndexOf("/") + 1), "0");

        try {
            assert thisPageString != null;
            thisPageInt = Integer.parseInt(thisPageString);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Ошибка в получении страницы", Toast.LENGTH_LONG).show();
        }

        if (!uriOfPdf.equalsIgnoreCase("")) {
            uriOfPdf = uriOfPdf + "";
        }



        final String dark = preferences.getString("Theme", "0");

        // открытие pdf файла
        pdfView = rootView1.findViewById(R.id.pdfView);
        if ("TRUE".equals(dark)) {
            pdfView.fromUri(Uri.parse(uriOfPdf))
                    .enableSwipe(true) // allows to block changing pages using swipe
                    .swipeHorizontal(true) // свайп по горизонтали или по вертикати
                    .enableDoubletap(true) // возможность сделать двойной таб (при двойно табе идет увелечений/уменьшение страницы)
                    .defaultPage(thisPageInt) // устанавливает страницу по дефолту (на которой первый раз откроется PDF)
                    .nightMode(true)
                    .enableAnnotationRendering(false) // render annotations (such as comments, colors or forms)
                    .password(null)
                    .scrollHandle(new DefaultScrollHandle(getContext()))
                    .onDrawAll(new OnDrawListener() {
                        @Override
                        public void onLayerDrawn(final Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {
                            if(isReadyN) {
                                if (thisPageInt >= 0 && mBit!=null && mBit[thisPageInt] != null && mBit.length > 0 && isNewPage) {
                                    //mNewBitmap = BitmapFactory.decodeFile(Objects.requireNonNull(getContext()).getExternalFilesDir("Draw") + "/" + name + ".png");
                                    // из-за этого очень долго рендерится страница, поэтому собираем все рисунки в самом начале загрузки книги
                                    mBitmap = combineBitmap(mBit[thisPageInt], mBitmap);
                                }
                            }

                            if(isReady) {
                                getDraw(pdfView.getPageCount());
                            }

                            canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
                            canvas.drawPath(mPath, mPaint);
                            canvas.drawPath(circlePath, circlePaint);
                        }
                    })
                    .enableAntialiasing(true) // improve rendering a little bit on low-res screens
                    .spacing(0) // расстояние между страницами
                    .pageSnap(true)
                    .pageFitPolicy(FitPolicy.HEIGHT)
                    .load();
        } else {
            pdfView.fromUri(Uri.parse(uriOfPdf))
                    .enableSwipe(false) // allows to block changing pages using swipe
                    .swipeHorizontal(true) // свайп по горизонтали или по вертикати
                    .enableDoubletap(true) // возможность сделать двойной таб (при двойно табе идет увелечений/уменьшение страницы)
                    .defaultPage(thisPageInt) // устанавливает страницу по дефолту (на которой первый раз откроется PDF)
                    .nightMode(false)
                    .enableAnnotationRendering(false) // render annotations (such as comments, colors or forms)
                    .password(null)
                    .scrollHandle(new DefaultScrollHandle(getContext()))
                    .onDrawAll(new OnDrawListener() {
                        @Override
                        public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {
                            if(isReadyN) {
                                thisPageInt = pdfView.getCurrentPage();
                                if (thisPageInt >= 0 && mBit!=null && mBit[thisPageInt] != null && mBit.length > 0 && isNewPage) {
                                    //mNewBitmap = BitmapFactory.decodeFile(Objects.requireNonNull(getContext()).getExternalFilesDir("Draw") + "/" + name + ".png");
                                    // из-за этого очень долго рендерится страница, поэтому собираем все рисунки в самом начале загрузки книги
                                    mBitmap = combineBitmap(mBit[thisPageInt], mBitmap);
                                }
                                mBit[thisPageInt] = mBitmap;
                            }

                            if(isReady) {
                                getDraw(pdfView.getPageCount());
                            }
                            canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
                            canvas.drawPath(mPath, mPaint);
                            canvas.drawPath(circlePath, circlePaint);

                        }
                    })
                    .enableAntialiasing(true) // improve rendering a little bit on low-res screens
                    .spacing(0) // расстояние между страницами
                    .pageSnap(true)
                    .pageFitPolicy(FitPolicy.WIDTH)
                    .load();

        }
        isReady = true;

        onClicks(false);

        pdfView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (left == 0 && top == 0 && right == 0 && bottom == 0) {
                    return;
                }
                thisHeight = top + bottom;
                thisWidth = right + left;

                mBitmap = Bitmap.createBitmap(left + right, top + bottom, Bitmap.Config.ARGB_8888);
                mCanvas = new Canvas(mBitmap);
            }
        });
        return rootView1;
    }

    public Bitmap combineBitmap(Bitmap c, Bitmap s)
    {
        Bitmap cs;

        int width, height;

        if(c.getWidth() > s.getWidth()) {
            width = c.getWidth();
        } else {
            width = s.getWidth();
        }
        if(c.getHeight() > s.getHeight()){
            height = c.getHeight();
        }
        else {
            height = s.getHeight();
        }

        cs = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas comboImage = new Canvas(cs);

        mCanvas = new Canvas(cs);
        mCanvas.drawBitmap(c, 0f, 0f, null);
        mCanvas.drawBitmap(s, c.getWidth(), 0f, null);

        comboImage.drawBitmap(c, 0f, 0f, null);
        comboImage.drawBitmap(s, c.getWidth(), 0f, null);

        return cs;
    }

    private void onClicks(Boolean isDraw){
        if(isDraw){
            pdfView.setOnTouchListener(new View.OnTouchListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    float x = event.getX();
                    float y = event.getY();

                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            touch_start(x, y);
                            pdfView.invalidate();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            touch_move(x, y);
                            pdfView.invalidate();
                            break;
                        case MotionEvent.ACTION_UP:
                            touch_up();
                            saveDrawing();
                            pdfView.invalidate();
                            break;
                    }
                    return true;
                }
            });
        }
        else {
            pdfView.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
                public void onSwipeRight() {
                    // для пользования ползунком для прокрутки страниц
                    isNewPage = true;
                    thisPageInt = pdfView.getCurrentPage();
                    //обработка свайпа вправо
                    if(thisPageInt > 0) {
                        thisPageInt--;
                        pdfView.jumpTo(thisPageInt, true);
                    }

                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("thisPageString" + uriOfPdf.substring(uriOfPdf.lastIndexOf("/") + 1), thisPageInt + "");
                    editor.apply();
                }

                @Override
                public void onSwipeLeft() {
                    // для пользования ползунком для прокрутки страниц
                    thisPageInt = pdfView.getCurrentPage();

                    isNewPage = true;
                    //обработка свайпа влево
                    thisPageInt++;

                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("thisPageString" + uriOfPdf.substring(uriOfPdf.lastIndexOf("/") + 1), thisPageInt + "");
                    editor.apply();

                    pdfView.jumpTo(thisPageInt, true);
                }

                @Override
                public void onSwipeTop() {
                    menuVisibility.setVisibility(View.VISIBLE);
                }

                @Override
                public void onSwipeBottom() {
                    menuVisibility.setVisibility(View.INVISIBLE);
                }

            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getDraw(int count){
        mBit = new Bitmap[count + 1];
        for (int i = 0; i < count + 1; i++) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            String isDr = preferences.getString("isDraving" + uriOfPdf.substring(uriOfPdf.lastIndexOf("/") + 1) + "page=" + i, "false");
            String name = "drawing" + uriOfPdf.substring(Objects.requireNonNull(uriOfPdf).lastIndexOf("/") + 1) + "page=" + i;
            assert isDr != null;
            if (isDr.equals("true")) {
                mBit[i] = BitmapFactory.decodeFile(Objects.requireNonNull(getContext()).getExternalFilesDir("Draw") + "/" + name + ".png");
            } else {
                mBit[i] = null;
            }
        }
        isReadyN = true;
        isReady = false;
        progressBar.setVisibility(ProgressBar.INVISIBLE);
    }

    private void settingsDraw() {
        // настройки кисти
        circlePaint = new Paint();
        circlePath = new Path();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.BLUE); // цвеь кисти
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeJoin(Paint.Join.MITER);
        circlePaint.setStrokeWidth(4f); // толщина кисти

        // настройки линии
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(color); // цвет кисти
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(depth); // толщина линии
    }

    // сохранение
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void saveDrawing() {

        // не забудьте очистить его (см. выше), или вы просто получить дубликаты

        // почти всегда вы будете хотеть уменьшить res от очень высокого res экрана
        // whatTheUserDrewBitmap = ThumbnailUtils.extractThumbnail(whatTheUserDrewBitmap, 256, 256);
        //Обратите внимание, что невероятно полезный трюк для обрезки и изменения размера квадратов

        // теперь можно сохранить растровое изображение в файл или отобразить его в ImageView:

        //* ImageView testArea = ...
        //* testArea.setImageBitmap( whatTheUserDrewBitmap );

        // в эти дни вам часто нужен "массив байт". например, чтобы сохранить в parse.com или другие облачные службы

        thisPageInt = pdfView.getCurrentPage(); // для правильного рисования послн использования ползунка
        String name = "drawing" + uriOfPdf.substring(Objects.requireNonNull(uriOfPdf).lastIndexOf("/") + 1) + "page=" + thisPageInt;
        Bitmap bitmap = mBitmap;
        if(mBitmap.getHeight() <= 0 && mBitmap.getWidth() <= 0){
            File file = new File(Objects.requireNonNull(getContext()).getExternalFilesDir("Draw") + "/" + name + ".png");
            file.delete();
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("isDraving" + uriOfPdf.substring(uriOfPdf.lastIndexOf("/") + 1) + "page=" + thisPageInt, "false");
            editor.apply();
        }
        else {

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("isDraving" + uriOfPdf.substring(uriOfPdf.lastIndexOf("/") + 1) + "page=" + thisPageInt, "true");
            editor.apply();


            File file = new File(Objects.requireNonNull(getContext()).getExternalFilesDir("Draw") + "/" + name + ".png");
            try {
                if (!file.exists()) {
                    file.createNewFile();
                }

                FileOutputStream ostream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 10, ostream);
                ostream.close();
                pdfView.invalidate();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

                pdfView.setDrawingCacheEnabled(false);
            }
        }
    }

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    private void touch_start(float x, float y) {
        isNewPage = false;
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;

            circlePath.reset();
            circlePath.addCircle(mX, mY, 30, Path.Direction.CW);
        }
    }

    private void touch_up() {
        mPath.lineTo(mX, mY);
        circlePath.reset();
        // commit the path to our offscreen
        mCanvas.drawPath(mPath, mPaint);
        // kill this so we don't double draw
        mPath.reset();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
