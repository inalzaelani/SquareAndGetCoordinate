package com.example.buatkotak;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ImageView drawingView;
    private Bitmap bitmap;
    private Canvas canvas;
    private Paint strokePaint;
    private Paint fillPaint;

    private float startX, startY;
    private Path path;

    private List<String> coordinateList;
    private List<Path> paths;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawingView = findViewById(R.id.drawingView);
        drawingView.setVisibility(View.INVISIBLE);
//        drawingView.setTranslationX(-drawingView.getWidth());

        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        int screenWidth = display.getWidth();
        int screenHeight = display.getHeight();

        bitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        drawingView.setImageBitmap(bitmap);


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


        coordinateList.add("100;100;120;100");
        coordinateList.add("300;100;100;100");
        coordinateList.add("500;100;100;100");
        coordinateList.add("700;100;100;100");
        coordinateList.add("900;100;100;100");

        coordinateList.add("100;300;100;100");
        coordinateList.add("300;300;100;100");
        coordinateList.add("500;300;100;100");
        coordinateList.add("700;300;100;100");
        coordinateList.add("900;300;100;100");



        drawSquares();

        drawingView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float x = event.getX();
                float y = event.getY();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        path = new Path();
                        path.moveTo(x, y);
                        startX = x;
                        startY = y;
                        break;

                    case MotionEvent.ACTION_MOVE:
                        float dx = Math.abs(x - startX);
                        float dy = Math.abs(y - startY);
                        float minSideLength = Math.min(dx, dy);
                        path.reset();
                        path.addRect(startX, startY, startX + minSideLength, startY + minSideLength, Path.Direction.CCW);
                        break;

                    case MotionEvent.ACTION_UP:
                        canvas.drawPath(path, strokePaint);
                        canvas.drawPath(path, fillPaint);
                        String coordinate = x+";"+ y;
                        coordinateList.add(coordinate);
                        paths.add(path);
                        Log.d("Coordinates", coordinate);
                        break;
                }

                drawingView.invalidate();
                return true;
            }
        });


        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (String coordinate : coordinateList) {
                    Log.d("SavedCoordinates", coordinate);
                }
            }
        });

        Button deleteButton = findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!paths.isEmpty()) {
                    paths.remove(paths.size() - 1);
                    clearCanvas();
                }
            }
        });

        FloatingActionButton fabShowDrawing = findViewById(R.id.fabShowDrawing);
        fabShowDrawing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Animasi untuk memunculkan ImageView dari kiri
                ObjectAnimator animator = ObjectAnimator.ofFloat(drawingView, "translationX", -drawingView.getWidth(), 0);
                drawingView.setVisibility(View.VISIBLE);
                animator.setDuration(500);
                animator.start();
            }
        });
    }

    private void clearCanvas() {
        canvas.drawColor(0, android.graphics.PorterDuff.Mode.CLEAR);
        for (Path p : paths) {
            canvas.drawPath(p, strokePaint);
            canvas.drawPath(p, fillPaint);
        }
        drawingView.invalidate();
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
//

        canvas.drawPath(squarePath, strokePaint);
        canvas.drawPath(squarePath, fillPaint);


        paths.add(squarePath);

        drawingView.invalidate();
    }
}
