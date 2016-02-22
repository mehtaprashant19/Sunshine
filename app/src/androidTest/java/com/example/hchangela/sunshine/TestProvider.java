package com.example.hchangela.sunshine;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

import com.example.hchangela.sunshine.data.WeatherContract.*;
import com.example.hchangela.sunshine.data.WeatherDbHelper;
import java.util.Map;
import java.util.Set;

/**
 * Created by Prashant Mehta on 1/27/15.
 */
public class TestProvider extends AndroidTestCase {
    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    static public String TEST_CITY_NAME = "North Pole";
    static public String TEST_LOCATION = "99705";
    static public String TEST_DATE = "20150128";

    ContentValues getLocationContentValues() {
        ContentValues values = new ContentValues();
        String testLocationSetting = "99705";
        double testLatitude = 64.772;
        double testLongitude = -147.355;

        values.put(LocationEntry.COLUMN_CITY_NAME, TEST_CITY_NAME);
        values.put(LocationEntry.COLUMN_COORD_LAT, testLatitude);
        values.put(LocationEntry.COLUMN_COORD_LONG, testLongitude);
        values.put(LocationEntry.COLUMN_LOCATION_SETTING, testLocationSetting);

        return values;
    }

    static public ContentValues getWeatherContentValues(long locationRowId) {
        ContentValues weatherValues = new ContentValues();
        weatherValues.put(WeatherEntry.COLUMN_LOC_KEY,locationRowId);
        weatherValues.put(WeatherEntry.COLUMN_DATETEXT,"20150128");
        weatherValues.put(WeatherEntry.COLUMN_DEGREES,1.1);
        weatherValues.put(WeatherEntry.COLUMN_HUMIDITY,1.2);
        weatherValues.put(WeatherEntry.COLUMN_PRESSURE,1.3);
        weatherValues.put(WeatherEntry.COLUMN_MAX_TEMP,75);
        weatherValues.put(WeatherEntry.COLUMN_MIN_TEMP,65);
        weatherValues.put(WeatherEntry.COLUMN_SHORT_DESC,"Astroids");
        weatherValues.put(WeatherEntry.COLUMN_WIND_SPEED,5.5);
        weatherValues.put(WeatherEntry.COLUMN_WEATHER_ID,321);
        return weatherValues;
    }

    static public void validateCursor(ContentValues expectedValues, Cursor valueCursor){
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();

        for(Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse(-1 == idx);
            String expectedValue= entry.getValue().toString();
            assertEquals(expectedValue, valueCursor.getString(idx));
        }
    }

    public void testDeleteAllRecords() {
        mContext.getContentResolver().delete(
          WeatherEntry.CONTENT_URI,
                null,
                null
        );

        mContext.getContentResolver().delete(
          LocationEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                WeatherEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        assertEquals(cursor.getCount(),0);
        cursor.close();

        cursor = mContext.getContentResolver().query(
                LocationEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        assertEquals(cursor.getCount(),0);
        cursor.close();
    }

    public void testUpdateLocation() {
        testDeleteAllRecords();

        ContentValues values = getLocationContentValues();

        Uri locationUri = mContext.getContentResolver().insert(LocationEntry.CONTENT_URI,values);
        long locationRowId = ContentUris.parseId(locationUri);

        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG,"New Row Id: " + locationRowId);

        ContentValues values2 = new ContentValues(values);
        values2.put(LocationEntry._ID,locationRowId);
        values2.put(LocationEntry.COLUMN_CITY_NAME,"Santa's Village");

        int count = mContext.getContentResolver().update(LocationEntry.CONTENT_URI,values2,LocationEntry._ID + " = ?", new String[]{Long.toString(locationRowId)});

        assertEquals(count,1);

        Cursor cursor = mContext.getContentResolver().query(
          LocationEntry.buildLocationUri(locationRowId),
                null,
                null,
                null,
                null
        );

        if(cursor.moveToFirst()){
            validateCursor(values2,cursor);
        }
        cursor.close();

    }

    public void testGetType() {
        String type = mContext.getContentResolver().getType(WeatherEntry.CONTENT_URI);
        assertEquals(WeatherEntry.CONTENT_TYPE,type);

        String testLocation = "94074";
        type = mContext.getContentResolver().getType(WeatherEntry.buildWeatherLocation(testLocation));
        assertEquals(WeatherEntry.CONTENT_TYPE,type);

        String testDate = "20140612";
        type = mContext.getContentResolver().getType(WeatherEntry.buildWeatherLocationWithDate(testLocation,testDate));
        assertEquals(WeatherEntry.CONTENT_ITEM_TYPE,type);

        type = mContext.getContentResolver().getType(LocationEntry.CONTENT_URI);
        assertEquals(LocationEntry.CONTENT_TYPE,type);

        type=mContext.getContentResolver().getType(LocationEntry.buildLocationUri(1L));
        assertEquals(LocationEntry.CONTENT_ITEM_TYPE,type);
    }

    public void testInsertReadProvider() {
        ContentValues values = getLocationContentValues();

        Uri locationUri = mContext.getContentResolver().insert(LocationEntry.CONTENT_URI, values);
        long locationRowId = ContentUris.parseId(locationUri);

        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New Row id:" + locationRowId);

        Cursor cursor = mContext.getContentResolver().query(LocationEntry.buildLocationUri(locationRowId),
                null,
                null,
                null,
                null
        );

        if(cursor.moveToFirst()) {
            validateCursor(values,cursor);

            ContentValues weatherValues = getWeatherContentValues(locationRowId);
            Uri insertUri = mContext.getContentResolver().insert(WeatherEntry.CONTENT_URI, weatherValues);

            Cursor weatherCursor = mContext.getContentResolver().query(WeatherEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    null
            );
            if(weatherCursor.moveToFirst()) {
                validateCursor(weatherValues,weatherCursor);
            }
            else {
                fail("No weather data returned");
            }
            weatherCursor.close();

            weatherCursor = mContext.getContentResolver().query(WeatherEntry.buildWeatherLocation(TEST_LOCATION),
                    null,
                    null,
                    null,
                    null
            );
            if(weatherCursor.moveToFirst()) {
                validateCursor(weatherValues,weatherCursor);
            }
            else {
                fail("No weather data returned");
            }

            weatherCursor.close();

            weatherCursor = mContext.getContentResolver().query(WeatherEntry.buildWeatherLocationWithStartDate(TEST_LOCATION, TEST_DATE),
                    null,
                    null,
                    null,
                    null
            );
            if(weatherCursor.moveToFirst()) {
                validateCursor(weatherValues,weatherCursor);
            }
            else {
                fail("No weather data returned");
            }

            weatherCursor.close();

            weatherCursor = mContext.getContentResolver().query(WeatherEntry.buildWeatherLocationWithDate(TEST_LOCATION, TEST_DATE),
                    null,
                    null,
                    null,
                    null
            );
            if(weatherCursor.moveToFirst()) {
                validateCursor(weatherValues,weatherCursor);
            }
            else {
                fail("No weather data returned");
            }

        }
        else {
            fail("No values returned:(");
        }
    }
}
