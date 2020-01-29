package ngonim.xyz.yourweather.weathermodels;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
//import java.time.LocalDateTime;

import ngonim.xyz.yourweather.R;

/**
 * Created by ngoni on 4/22/17.
 */

public class Current extends Weather {
    private String mIcon;
    private long mTime;
    private double mTemperature;
    private double mHumidity;
    private double mPrecipitation;
    private String mSummary;
    private String mTimeZone;
    private double mVisibility;

    public Current() {
    }

    @Override
    public String getIcon() {
        return mIcon;
    }

    @Override
    public void setIcon(String mIcon) {
        this.mIcon = mIcon;
    }

    public int getIconId() {
        int iconId = R.mipmap.clear_day;

        if ("clear-day".equals(mIcon)) {
            iconId = R.mipmap.clear_day;
        } else if ("clear-night".equals(mIcon)) {
            iconId = R.mipmap.clear_night;
        } else if ("rain".equals(mIcon)) {
            iconId = R.mipmap.rain;
        } else if ("snow".equals(mIcon)) {
            iconId = R.mipmap.snow;
        } else if ("sleet".equals(mIcon)) {
            iconId = R.mipmap.sleet;
        } else if ("wind".equals(mIcon)) {
            iconId = R.mipmap.wind;
        } else if ("fog".equals(mIcon)) {
            iconId = R.mipmap.fog;
        } else if ("cloudy".equals(mIcon)) {
            iconId = R.mipmap.cloudy;
        } else if ("partly-cloudy".equals(mIcon)) {
            iconId = R.mipmap.partly_cloudy;
        } else if ("partly-cloudy-night".equals(mIcon)) {
            iconId = R.mipmap.cloudy_night;
        }

        return iconId;
    }

    @Override
    public long getTime() {
        return mTime;
    }

    public String getFormattedTime() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm a");
        dateFormat.setTimeZone(TimeZone.getTimeZone(getTimeZone()));
        Date date = new Date(getTime() * 1000);
        return dateFormat.format(date);
    }

    @Override
    public void setTime(long mTime) {
        this.mTime = mTime;
    }

    public int getTemperatureInCelcius() {
        return (int) ((mTemperature - 32) * 5 / 9);
    }

    public int getTemperature() {
        return (int) Math.round(mTemperature);

    }

    @Override
    public void setTemperature(double mTemperature) {
        this.mTemperature = mTemperature;
    }

    @Override
    public double getHumidity() {
        double Hum = mHumidity * 100;
        return  (int)Math.round(Hum);
    }

    @Override
    public void setHumidity(double mHumidity) {
        this.mHumidity = mHumidity;
    }

    public int getPrecipitation() {
        double precipPercentage = mPrecipitation * 100;
        return (int) Math.round(precipPercentage);
    }

    @Override
    public void setPrecipitation(double mPrecipitation) {
        this.mPrecipitation = mPrecipitation;
    }

    @Override
    public String getSummary() {
        return mSummary;
    }

    @Override
    public void setSummary(String mSummary) {
        this.mSummary = mSummary;
    }

    @Override
    public String getTimeZone() {
        return mTimeZone;
    }

    @Override
    public void setTimeZone(String mTimeZone) {
        this.mTimeZone = mTimeZone;
    }

    public double getVisibility() {
        return mVisibility;
    }

    public void setVisibility(double visibility) {
        mVisibility = visibility;
    }

}
