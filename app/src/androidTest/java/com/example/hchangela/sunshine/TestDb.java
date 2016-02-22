package com.example.hchangela.sunshine;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.test.AndroidTestCase;
import android.util.Log;

import com.example.hchangela.sunshine.data.WeatherContract.*;
import com.example.hchangela.sunshine.data.WeatherDbHelper;

import java.util.Map;
import java.util.Set;

/**
 * Created by Prashant Mehta on 1/27/15.
 */
public class TestDb extends AndroidTestCase {
    public static final String LOG_TAG = TestDb.class.getSimpleName();

    public void testCreateDb() throws Throwable {
        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new WeatherDbHelper(this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
    }

    static public String TEST_CITY_NAME = "North Pole";

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

    public void testInsertReadDb() {
        WeatherDbHelper dbHelper = new WeatherDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = getLocationContentValues();

        long locationRowId;
        locationRowId = db.insert(LocationEntry.TABLE_NAME, null, values);

        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New Row id:" + locationRowId);

        Cursor cursor = db.query(
          LocationEntry.TABLE_NAME,
          null,
          null,
          null,
          null,
          null,
          null
        );

        if(cursor.moveToFirst()) {
            validateCursor(values,cursor);

            ContentValues weatherValues = getWeatherContentValues(locationRowId);

            long weatherRowId;
            weatherRowId = db.insert(WeatherEntry.TABLE_NAME, null, weatherValues);
            assertTrue(weatherRowId != -1);

            Cursor weatherCursor = db.query(
                    WeatherEntry.TABLE_NAME,
                    null,
                    null,
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
