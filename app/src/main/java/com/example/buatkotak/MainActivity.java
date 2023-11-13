package com.example.buatkotak;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ImageView drawingView;
    private Bitmap bitmap;
    private Canvas canvas;
    private Paint strokePaint;
    private Paint fillPaint;
    private FloatingActionButton fabShowDrawing;
    private Path path;

    private List<String> coordinateList;
    private List<Path> paths;
    private boolean visible = false;
    private int screenWidth;
    private int screenHeight;
    private Display display;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initView();

        path = new Path();
        strokePaint = new Paint();
        strokePaint.setAntiAlias(true);
        strokePaint.setStrokeWidth(5);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setColor(0xFF000000);

        fillPaint = new Paint();
        fillPaint.setAntiAlias(true);
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setColor(0xFFFFFF00);

        coordinateList = new ArrayList<>();
        paths = new ArrayList<>();


        fetchData();

    }

    public void initView(){

        drawingView = findViewById(R.id.drawingView);
        drawingView.setVisibility(View.INVISIBLE);
        drawingView.setTranslationX(-drawingView.getWidth());
        fabShowDrawing = findViewById(R.id.fabShowDrawing);


        WindowManager windowManager = getWindowManager();
        display = windowManager.getDefaultDisplay();
        screenWidth = display.getWidth();
        screenHeight = display.getHeight();

        bitmap = Bitmap.createBitmap(screenWidth * 2, screenHeight * 2, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        drawingView.setImageBitmap(bitmap);

        drawingView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float x = event.getX();
                float y = event.getY();

                switch (event.getAction()) {

                    case MotionEvent.ACTION_UP:
//                        canvas.drawPath(path, strokePaint);
//                        canvas.drawPath(path, fillPaint);
//                        String coordinate = x+";"+ y;
//                        coordinateList.add(coordinate);
//                        paths.add(path);
//                        Log.d("Coordinates", coordinate);

                        for (int i = 0; i < paths.size(); i++) {
                            Path path = paths.get(i);
                            RectF bounds = new RectF();
                            path.computeBounds(bounds,true);
                            if (bounds.contains(x, y)) {
                                showPopupWindow();
                                break;
                            }
                        }
                        break;
                    default:
                        break;
                }

                drawingView.invalidate();
                return true;
            }
        });
        fabShowDrawing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!visible) {
                    visible = true;
                    ObjectAnimator animator = ObjectAnimator.ofFloat(drawingView, "translationX", -drawingView.getWidth(), 0);
                    drawingView.setVisibility(View.VISIBLE);
                    animator.setDuration(500);
                    animator.start();
                } else {
                    visible = false;
                    ObjectAnimator animator = ObjectAnimator.ofFloat(drawingView, "translationX", 0, -drawingView.getWidth());
                    animator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            drawingView.setVisibility(View.INVISIBLE);
                        }
                    });
                    animator.setDuration(500);
                    animator.start();
                }
            }
        });

    }

    public void fetchData(){
        // x,y,length, width
        coordinateList.add("100;100;550;500");
        coordinateList.add("700;100;540;500");
        coordinateList.add("1300;100;510;500");
        coordinateList.add("1700;100;400;500");
        coordinateList.add("2500;100;500;500");
        coordinateList.add("3000;100;300;500");
        coordinateList.add("3700;100;200;500");

        coordinateList.add("100;700;650;700");
        coordinateList.add("700;700;640;800");
        coordinateList.add("1300;700;710;500");
        coordinateList.add("1700;700;800;500");
        coordinateList.add("2500;700;900;500");
        coordinateList.add("3000;700;300;500");
        coordinateList.add("3700;700;200;500");



        drawSquares();

    }

    private void drawSquares() {
        for (String coordinate : coordinateList) {
            String[] parts = coordinate.split(";");
            if (parts.length == 4) {
                float x = Float.parseFloat(parts[0]);
                float y = Float.parseFloat(parts[1]);
                float length = Float.parseFloat(parts[2]);
                float width = Float.parseFloat(parts[3]);

                drawSquare(x, y, length, width);
            }
        }
    }

    private void drawSquare(float x, float y, float length, float width) {
        Path squarePath = new Path();
        squarePath.addRect(x, y, x + length, y + width, Path.Direction.CCW);

        canvas.drawPath(squarePath, strokePaint);
        canvas.drawPath(squarePath, fillPaint);

        String text = "X: " + x + "\nY: " + y;
        float textX = x + length / 2;
        float textY = y + width / 2;

        Paint textPaint = new Paint();
        textPaint.setColor(0xFF000000);
        textPaint.setTextSize(10);

        canvas.drawText(text, textX, textY, textPaint);

        paths.add(squarePath);

        drawingView.invalidate();
    }

//    private void showSquareClickedAlert(int squareIndex) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        squareIndex += 1;
//        builder.setMessage("Square " + squareIndex + " clicked!")
//                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        showPopupWindow();
//                    }
//                });
//        builder.create().show();
//    }

    private void showPopupWindow() {
        // Create a new PopupWindow
        View popupView = getLayoutInflater().inflate(R.layout.dialog_slide_in, null);
        PopupWindow popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.MATCH_PARENT, true);

        // Set the animation style
        popupWindow.setAnimationStyle(android.R.style.Animation_Dialog);

        // Show the popup window from the right
        popupWindow.showAtLocation(drawingView, Gravity.END, 0, 0);

         Button closeButton = popupView.findViewById(R.id.closeButton);
         closeButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 popupWindow.dismiss();
             }
         });
    }



}
