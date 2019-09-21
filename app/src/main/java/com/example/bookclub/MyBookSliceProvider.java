package com.example.bookclub;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.Slice;
import androidx.slice.SliceProvider;
import androidx.slice.builders.ListBuilder;
import androidx.slice.builders.ListBuilder.RowBuilder;
import androidx.slice.builders.SliceAction;

import static android.app.slice.Slice.EXTRA_TOGGLE_STATE;

public class MyBookSliceProvider extends SliceProvider {
    //variable to hold the value of context.
    Context mcontext;
    static String name ,director ;
    static double mtemperature = 0.0;


    // copy the static line as global variables

    static final String _ID        = "_id"     ;
    static final String TITLE      = "title"   ;
    static final String DIRECTOR   = "director";


    Handler handler = new Handler();



    /**
     * Instantiate any required objects. Return true if the provider was successfully created,
     * false otherwise.
     */
    @Override
    public boolean onCreateSliceProvider() {
        mcontext = getContext();
        if (mcontext == null){
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * Converts URL to content URI (i.e. content://com.example.bookclub...)
     */
    @Override
    @NonNull
    public Uri onMapIntentToUri(@Nullable Intent intent) {
        // Note: implementing this is only required if you plan on catching URL requests.
        // This is an example solution.
        Uri.Builder uriBuilder = new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT);
        if (intent == null) return uriBuilder.build();
        Uri data = intent.getData();
        if (data != null && data.getPath() != null) {
            String path = data.getPath().replace("/", "");
            uriBuilder = uriBuilder.path(path);
        }
        Context context = getContext();
        if (context != null) {
            uriBuilder = uriBuilder.authority(context.getPackageName());
        }
        return uriBuilder.build();
    }


    /**
     * Construct the Slice and bind data if available.
     */
    public Slice onBindSlice(Uri sliceUri) {

        if (getContext() == null ) {
            return null;
        }
//we build the slices dynamically depending on the uri path that gets passed to the slice provider
switch (sliceUri.getPath()){
    case "/MyBookSliceProvider/bookReview" : //slice to display the reviews

        return createReviewSlice(sliceUri); // create a slice of reviews


    case "/MyBookSliceProvider/bookRatings" : //slice to display the ratings

        return createRatingSlice(sliceUri); //create a slice of ratings

default: //default creates a slice and displays that there is no slice for the path
    ListBuilder listBuilder = new ListBuilder(getContext(), sliceUri, ListBuilder.INFINITY);
    listBuilder.addRow(new RowBuilder().setTitle("No URI found").setPrimaryAction(createActivityAction()));
   return listBuilder.build();
}

    }
    //***********************************Review slice***********************************
    /* this method creates a slice for reviews*/
private Slice createReviewSlice(Uri sliceUri){

   getUpdatedTemperature(sliceUri);

    //Increase action associated with the slice
    SliceAction increaseAction = increaseReviews();

    //decrease action associated with the slice
    SliceAction decreaseAction = decreaseReviews();

       //The slice is built here with three dummy rows and two images with actions
    ListBuilder reviewlistBuilder = new ListBuilder(mcontext, sliceUri, ListBuilder.INFINITY);
    SliceAction activityAction = createActivityAction();
    reviewlistBuilder.setHeader(new ListBuilder.HeaderBuilder().setTitle("Book Reviews")
            .setSubtitle("Review Count :" + MyBroadcastReceiver.currentValue)
           // .setTitle("Temperature :" + mtemperature)
            .setTitle("Movie Name  "+ name + " Director  "+director+ " : " + mtemperature));
            //.setSubtitle("Loading Temperature",true)
            //.setSummary("Temperature :" + MyBroadcastReceiver.temperature));
    reviewlistBuilder.addRow(new RowBuilder().setTitle("Book one Reviews").setPrimaryAction(activityAction));
    reviewlistBuilder.addRow(new RowBuilder().setTitle("Book two Reviews"));
    reviewlistBuilder.addRow(new RowBuilder().setTitle("Book three Reviews"))
            .addAction(increaseAction)
            .addAction(decreaseAction);
    return reviewlistBuilder.build();
}

    //slice action associated with upward arrow
private SliceAction increaseReviews(){

    //create a pending intend to trigger the broadcast receiver and send the count value to be updated.
    // we are getting connecting to BR because some one will dump the changes to it...web services amy pump the data and broadcast reciever will get the latest data...
   // PendingIntent.getService()//BR--> mode of transport
                                  //service--> it is a daemon it can have uri notification or broadcast reciever...
    //broadcast every change in weather and then catch my in my broadcast receiver and display in your app.--version 1
    //weather app third line from top....chigago,usa..it will give its weather...have loop every 5 sec..show the weather of 5 cities...version 2...
    //getting connected to uri--version 3....

    PendingIntent increaseReviewIntent = PendingIntent.getBroadcast(mcontext,
            0,
            new Intent(mcontext, MyBroadcastReceiver.class)
                    .setAction(MyBroadcastReceiver.INCREMENT_COUNTER_ACTION)
                    .putExtra(MyBroadcastReceiver.EXTRA_VALUE_KEY,
            MyBroadcastReceiver.currentValue + 1), PendingIntent.FLAG_UPDATE_CURRENT);

    /*
    PendingIntent increaseReviewIntent = PendingIntent.getBroadcast(mcontext,
            0,
            new Intent(mcontext, MainActivity.class)
                   , PendingIntent.FLAG_UPDATE_CURRENT);
                   */

//create a slice action which is connected to the upward arrow image
   return SliceAction.create( increaseReviewIntent,
            IconCompat.createWithResource(mcontext, R.drawable.upward_arrow),
            ListBuilder.ICON_IMAGE, "Increase reviews");
}
//slice action associated with downward arrow
    private SliceAction decreaseReviews(){

        //create a pending intend to trigger the broadcast receiver and send the count value to be updated
        PendingIntent decreaseReviewIntent = PendingIntent.getBroadcast(mcontext,
                0,
                new Intent(mcontext, MyBroadcastReceiver.class)
                        .setAction(MyBroadcastReceiver.DECREMENT_COUNTER_ACTION)
                        .putExtra(MyBroadcastReceiver.EXTRA_VALUE_KEY,
                                MyBroadcastReceiver.currentValue - 1), PendingIntent.FLAG_UPDATE_CURRENT);

        //create a slice action which is connected to the upward arrow image
        return SliceAction.create( decreaseReviewIntent,
                IconCompat.createWithResource(mcontext, R.drawable.downward_arrow),
                ListBuilder.ICON_IMAGE, "Decrease reviews");
    }
//****************review slice ends*****************************

    //*****************************Rating slice****************************************
    //this method creates a rating slice
    private Slice createRatingSlice(Uri sliceUri){



   PendingIntent RatingChangedIntent = PendingIntent.getBroadcast(mcontext,0,
           new Intent(mcontext,MyBroadcastReceiver.class)
   .setAction(MyBroadcastReceiver.RATING_INCREASED)

   .putExtra(MyBroadcastReceiver.RATING_MESSAGE,"Rating Changed"),PendingIntent.FLAG_UPDATE_CURRENT);


        ListBuilder ratinglistBuilder = new ListBuilder(mcontext, sliceUri, ListBuilder.INFINITY);
        SliceAction activityAction2 = createActivityAction();
        ratinglistBuilder.setHeader(new ListBuilder.HeaderBuilder().setTitle("Book Ratings")
                .setSubtitle("want to rate?").setSummary("Rated by most users"))
                .addInputRange(new ListBuilder.InputRangeBuilder().setTitle("Rating range").setInputAction(RatingChangedIntent)
                        .setMax(5)
                        .setMin(0));
        ratinglistBuilder.addRow(new RowBuilder().setTitle("Book one Rating"));
        ratinglistBuilder.addRow(new RowBuilder().setTitle("Book two Rating"));
        ratinglistBuilder.addRow(new RowBuilder().setTitle("Book three Rating").setPrimaryAction(activityAction2));

        return ratinglistBuilder.build();
    }

    private SliceAction createActivityAction() {
       // return null;
        //Instead of returning null, you should create a SliceAction. Here is an example:

        return SliceAction.create(
                //calling activity
            PendingIntent.getActivity(
                getContext(), 0, new Intent(getContext(), MainActivity.class), 0
            ),
            IconCompat.createWithResource(mcontext, R.drawable.ic_launcher_foreground),
            ListBuilder.ICON_IMAGE,
            "Open App"
        );

    }

    public void getUpdatedTemperature(Uri sliceuri){
        ////////////////////////////////////////////////////

//                            Uri weatheruri = Uri.parse("content://" + "com.androstock.myweatherapp" + "/" + "WeatherContentProvider" + "/updateTemperature");
//                            ContentValues cv = new ContentValues();
//                            cv.put("name",120);
//                           mtemperature= mcontext.getContentResolver().update(weatheruri,cv,null,null);
//                            Log.d("myapp","temperature recieved at book activity"+mtemperature);
//                            ////////////////////////////////////////////////////

     //   mcontext.getContentResolver().notifyChange(sliceuri,null);


//        /////////////// Phase II Works Gets data from Content provider
        new Thread(

                new Runnable() {
                    @Override
                    public void run() {
                        Uri allTitles = Uri.parse("content://com.example.provider.Movies/movies/");

                        Cursor c =   mcontext.getContentResolver().query(allTitles,null,null,null);

                        if (c.moveToFirst()) {
                            // do{



//                        c.getString(c.getColumnIndex(_ID)) + ", " +
//                                c.getString(c.getColumnIndex(TITLE)) + ", " +
//                                c.getString(c.getColumnIndex(DIRECTOR))  ) ;


                            name =  c.getString(c.getColumnIndex(TITLE));
                            director = c.getString(c.getColumnIndex(DIRECTOR))   ;

//            }
//            while (c.moveToNext());
                        }
                    }
                }

        ).start();
//////////////////////////




    }

    /**
     * Slice has been pinned to external process. Subscribe to data source if necessary.
     */
    @Override
    public void onSlicePinned(Uri sliceUri) {
        // When data is received, call context.contentResolver.notifyChange(sliceUri, null) to
        // trigger MyBookSliceProvider#onBindSlice(Uri) again.

       // mcontext.getContentResolver().notifyChange(sliceuri,null);

     //   mcontext.getContentResolver().notifyChange(sliceUri, null);
//Uri uri = Uri.parse("Content://" + "com.androstock.myweatherapp" + "/" + "WeatherContentProvider" + "/updateTemperature");
//temperature = String.valueOf(mcontext.getContentResolver().update(uri,null,null,null));


/*




https://developer.android.com/reference/androidx/slice/SliceProvider.html#onSlicePinned(android.net.Uri)




onSlicePinned
public void onSlicePinned (Uri sliceUri)
Called to inform an app that a slice has been pinned.

Pinning is a way that slice hosts use to notify apps of which slices they care about updates for.
When a slice is pinned the content is expected to be relatively fresh and kept up to date.

Being pinned does not provide any escalated privileges for the slice provider.
 So apps should do things such as turn on syncing or schedule a job in response to a onSlicePinned.

Pinned state is not persisted through a reboot, and apps can expect a new call to onSlicePinned for any slices
that should remain pinned after a reboot occurs.
 */
    }

    /**
     * Unsubscribe from data source if necessary.
     */
    @Override
    public void onSliceUnpinned(Uri sliceUri) {
        // Remove any observers if necessary to avoid memory leaks.
    }


}

