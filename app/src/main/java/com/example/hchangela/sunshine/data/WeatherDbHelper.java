package com.example.hchangela.sunshine.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.hchangela.sunshine.data.WeatherContract.*;

/**
 * Created by Prashant Mehta on 1/27/15.
 */
public class WeatherDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "weather.db";

    public WeatherDbHelper(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_LOCATION_TABLE =
                "Create Table " + LocationEntry.TABLE_NAME + " ( " +
                        LocationEntry._ID + " Integer Primary Key, " +
                        LocationEntry.COLUMN_LOCATION_SETTING + " Text Unique Not Null, " +
                        LocationEntry.COLUMN_CITY_NAME + " Text Not Null," +
                        LocationEntry.COLUMN_COORD_LAT + " Real Not Null," +
                        LocationEntry.COLUMN_COORD_LONG + " Real Not Null," +
                        "Unique (" + LocationEntry.COLUMN_LOCATION_SETTING + ") On Conflict Ignore);";

        final String SQL_CREATE_WEATHER_TABLE =
                "Create Table " + WeatherEntry.TABLE_NAME + " ( " +
                        WeatherEntry._ID + " Integer Primary Key AutoIncrement, " +

                        WeatherEntry.COLUMN_LOC_KEY + " Integer Not Null," +
                        WeatherEntry.COLUMN_DATETEXT + " Text Not Null," +
                        WeatherEntry.COLUMN_SHORT_DESC + " Text Not Null," +
                        WeatherEntry.COLUMN_WEATHER_ID + " Integer Not Null," +

                        WeatherEntry.COLUMN_MIN_TEMP + " Real Not Null," +
                        WeatherEntry.COLUMN_MAX_TEMP + " Real Not Null," +

                        WeatherEntry.COLUMN_HUMIDITY + " Real Not Null," +
                        WeatherEntry.COLUMN_PRESSURE + " Real Not Null," +
                        WeatherEntry.COLUMN_WIND_SPEED + " Real Not Null," +
                        WeatherEntry.COLUMN_DEGREES + " Real Not Null," +

                        "Foreign Key (" + WeatherEntry.COLUMN_LOC_KEY + ") References " +
                        LocationEntry.TABLE_NAME + " (" + LocationEntry._ID + "), " +

                        "Unique (" + WeatherEntry.COLUMN_DATETEXT + ", " +
                        WeatherEntry.COLUMN_LOC_KEY + ") On Conflict Replace);";
        Log.v("Test",SQL_CREATE_LOCATION_TABLE);

        db.execSQL(SQL_CREATE_LOCATION_TABLE);
        db.execSQL(SQL_CREATE_WEATHER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("Drop Table If Exists " + LocationEntry.TABLE_NAME);
        db.execSQL("Drop Table If Exists " + WeatherEntry.TABLE_NAME);
        onCreate(db);
    }
}
