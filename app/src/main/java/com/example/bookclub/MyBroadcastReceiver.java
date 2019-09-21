package com.example.bookclub;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Rating;
import android.net.Uri;
import android.widget.Toast;

public class MyBroadcastReceiver extends BroadcastReceiver {
    public  String EXTRA_MESSAGE = "message";

    public static String EXTRA_VALUE_KEY = "extra_value";
    public static String RATING_INCREASED = "com.example.bookclub.MyBroadcastReceiver.ACTION_RATING_INCREASED";
    public static String RATING_MESSAGE = "Default value rating";
    public static String DECREMENT_COUNTER_ACTION = "com.example.bookclub.MyBroadcastReceiver.ACTION_DECREMENT_COUNTER";
    public static String INCREMENT_COUNTER_ACTION = "com.example.bookclub.MyBroadcastReceiver.ACTION_INCREMENT_COUNTER";
    public static String TEMPERATURE_ACTION = "com.example.bookclub.MyBroadcastReceiver.TEMPERATURE_ACTION";
    public static int currentValue = 0;
    public static String temperature = "25 *c";
    public static String RatingMessage;
    String city;
    Uri dynamicSliceUri       = Uri.parse("content://com.example.bookclub/MyBookSliceProvider/bookReview");
    Uri dynamicRatingSliceUri = Uri.parse("content://com.example.bookclub/MyBookSliceProvider/bookRatings");

    @Override
    public void onReceive(Context context, Intent intent) {
      //  String action = intent.getAction();

        if (intent.getAction().equals(INCREMENT_COUNTER_ACTION) ){

//On clicking the increment action button get the current value of the counter from the slice and depending on the value of the current value
            //set the values of the city. and then send a  broad cast to the weather app.

            currentValue = intent.getIntExtra(EXTRA_VALUE_KEY, 0);

            // context.getContentResolver().insert();
            switch (currentValue){
                case 0 :
                    city = "Bengaluru, IN";

                    break;
                case 1 :
                    city = "Mumbai, IN";

                    break;
                case 2 :
                    city = "Pune, IN";

                    break;
                case 3 :
                    city = "Chennai, IN";

                    break;
                case 4 :
                    city = "Hyderabad, IN";

                    break;

                default :
                    city = "Pune, IN";

                    break;
            }
//send the broad cast to the weather app and send the city name.
            Intent i = new Intent();
            i.putExtra("city",city);
            i.setClassName("com.androstock.myweatherapp","com.androstock.myweatherapp.MyReceiver");
            i.setAction("com.androstock.myweatherapp.MyReceiver.CITY");
            context.sendBroadcast(i);

            context.getContentResolver().notifyChange(dynamicSliceUri, null);
        } else if (intent.getAction().equals(DECREMENT_COUNTER_ACTION) ){
            currentValue = intent.getIntExtra(EXTRA_VALUE_KEY, 0);
            //can have own class as observer and it can be notified about the change// its an object..it can do some actions if needed..
            context.getContentResolver().notifyChange(dynamicSliceUri, null);
        } else if (intent.getAction().equals(RATING_INCREASED) ){
            RatingMessage = String.valueOf((intent.getStringExtra(RatingMessage)));
            Toast.makeText(context, RatingMessage, Toast.LENGTH_SHORT).show();
            context.getContentResolver().notifyChange(dynamicRatingSliceUri, null); }
        else if (intent.getAction().equals(TEMPERATURE_ACTION) ){
            temperature = intent.getStringExtra("Temp");
            context.getContentResolver().notifyChange(dynamicRatingSliceUri, null);
        }

    }
}
