package com.example.hchangela.sunshine;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Set;

/**
 * Created by Prashant Mehta on 09/02/2015.
 */
public class ForecastAdapter extends CursorAdapter {

    private final int VIEW_TYPE_TODAY = 0;
    private final int VIEW_TYPE_FUTURE_DAY = 1;
    private boolean mUseTodayLayout;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public ForecastAdapter(Context context, Cursor c, int flags){
        super(context,c,flags);
    }

    public void setUseTodayLayout(boolean useTodayLayout){
        mUseTodayLayout = useTodayLayout;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0 && mUseTodayLayout) ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;

        if(viewType == VIEW_TYPE_TODAY) {
            layoutId = R.layout.list_item_forecast_today;
        }
        else if(viewType == VIEW_TYPE_FUTURE_DAY){
            layoutId = R.layout.list_item_forecast;
        }
        View view = LayoutInflater.from(context).inflate(layoutId,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        int weatherId = cursor.getInt(ForecastFragment.COL_WEATHER_ID);

        int viewType = getItemViewType(cursor.getPosition());
        switch (viewType){
            case VIEW_TYPE_TODAY: {
                viewHolder.iconView.setImageResource(
                        Utility.getArtResourceForWeatherCondition(
                                cursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID)));
                break;
            }

            case VIEW_TYPE_FUTURE_DAY: {
                viewHolder.iconView.setImageResource(
                        Utility.getIconResourceForWeatherCondition(
                                cursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID)));
                break;
            }

        }

        String dateString = cursor.getString(ForecastFragment.COL_WEATHER_DATE);
        viewHolder.dateView.setText(Utility.getFriendlyDayString(context,dateString));

        String description = cursor.getString(ForecastFragment.COL_WEATHER_DESC);
        viewHolder.descriptionView.setText(description);

        boolean isMetric = Utility.isMetric(context);

        float high = cursor.getFloat(ForecastFragment.COL_WEATHER_MAX_TEMP);
        viewHolder.highTempView.setText(Utility.formatTemperature(context, high, isMetric));

        float low = cursor.getFloat(ForecastFragment.COL_WEATHER_MIN_TEMP);
        viewHolder.lowTempView.setText(Utility.formatTemperature(context,low, isMetric));
    }

    public static class ViewHolder {
        public final ImageView iconView;
        public final TextView dateView;
        public final TextView descriptionView;
        public final TextView highTempView;
        public final TextView lowTempView;

        public ViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.list_item_icon);
            dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
            descriptionView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
            highTempView = (TextView) view.findViewById(R.id.list_item_high_textview);
            lowTempView = (TextView) view.findViewById(R.id.list_item_low_textview);
        }
    }
}