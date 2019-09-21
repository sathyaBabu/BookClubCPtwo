package com.example.bookclub;

import androidx.appcompat.app.AppCompatActivity;

import android.app.slice.SliceManager;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    static double temperature = 20.00;
    Uri dynamicSliceUri       = Uri.parse("content://com.example.bookclub/MyBookSliceProvider/bookReview");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    protected void onResume() {
        super.onResume();
        ////////////////////////////////////////////////////*
        /*
        Uri weatheruri = Uri.parse("content://" + "com.androstock.myweatherapp" + "/" + "WeatherContentProvider" + "/updateTemperature");
        ContentValues cv = new ContentValues();
        cv.put("name",120);
        temperature = getContentResolver().update(weatheruri,cv,null,null);
        Log.d("myapp","temperature recieved at book activity"+temperature);
        getContentResolver().notifyChange(dynamicSliceUri, null);
        */
        ////////////////////////////////////////////////////
    }

    public void getBookReviews(View view) {
        String uri = "slice-content://com.example.bookclub/MyBookSliceProvider/bookReview";
        Intent intent = new Intent (Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent);
    }

    public void getBookRatings(View view) {
        String uri = "slice-content://com.example.bookclub/MyBookSliceProvider/bookRatings";
        Intent intent1 = new Intent (Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent1);
    }
}
