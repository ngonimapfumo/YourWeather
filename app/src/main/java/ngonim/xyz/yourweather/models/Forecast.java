package ngonim.xyz.yourweather.models;

/**
 * Created by ngoni on 7/7/17.
 */

public class Forecast extends Weather {

    private Current mCurrent;
    private Hourly[] mHourlyForecast;
    private Daily[] mDailyForecast;

    public Current getCurrent() {
        return mCurrent;
    }

    public void setCurrent(Current current) {
        mCurrent = current;
    }

    public Hourly[] getHourlyForecast() {
        return mHourlyForecast;
    }

    public void setHourlyForecast(Hourly[] hourlyForecast) {
        mHourlyForecast = hourlyForecast;
    }

    public Daily[] getDailyForecast() {
        return mDailyForecast;
    }

    public void setDailyForecast(Daily[] dailyForecast) {
        mDailyForecast = dailyForecast;
    }
}
