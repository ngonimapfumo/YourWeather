package ngonim.xyz.yourweather.weathermodels;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import ngonim.xyz.yourweather.R;

/**
 * Created by ngoni on 4/22/17.
 */

public class Current {
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

    public String getIcon() {
        return mIcon;
    }

    public void setIcon(String mIcon) {
        this.mIcon = mIcon;
    }

    public int getIconId(){
        int iconId = R.mipmap.clear_day;
        /*clear-day,
         clear-night,
         rain,
         snow,
         sleet,
         wind,
         fog,
         cloudy,
         partly-cloudy-day,
         or partly-cloudy-night*/

        if("clear-day".equals(mIcon)){
            iconId = R.mipmap.clear_day;
        }
        else if("clear-night".equals(mIcon)){
            iconId =R.mipmap.clear_night;
        }
        else if("rain".equals(mIcon)){
            iconId =R.mipmap.rain;
        }
        else if("snow".equals(mIcon)){
            iconId =R.mipmap.snow;
        }
        else if("sleet".equals(mIcon)){
            iconId =R.mipmap.sleet;
        }
        else if("wind".equals(mIcon)){
            iconId =R.mipmap.wind;
        }
        else if("fog".equals(mIcon)){
            iconId =R.mipmap.fog;
        }
        else if("cloudy".equals(mIcon)){
            iconId =R.mipmap.cloudy;
        }
        else if("partly-cloudy".equals(mIcon)){
            iconId =R.mipmap.partly_cloudy;
        }
        else if("partly-cloudy-night".equals(mIcon)){
            iconId = R.mipmap.cloudy_night;
        }

        return iconId;
    }

    public long getTime() {
        return mTime;
    }

    public String getFormattedTime(){

        SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm a");
        dateFormat.setTimeZone(TimeZone.getTimeZone(getTimeZone()));
        Date date = new Date(getTime()* 1000);
        return dateFormat.format(date);
    }

    public void setTime(long mTime) {
        this.mTime = mTime;
    }

    public int getTemperatureInCelcius(){
        return (int) ((mTemperature-32)*5/9);
    }
    public int getTemperature() {
        return (int) Math.round(mTemperature);

    }

    public void setTemperature(double mTemperature) {
        this.mTemperature = mTemperature;
    }

    public double getHumidity() {
        return mHumidity;
    }

    public void setHumidity(double mHumidity) {
        this.mHumidity = mHumidity;
    }

    public int getPrecipitation() {
        double precipPercentage = mPrecipitation * 100;
        return (int)Math.round(precipPercentage);
    }

    public void setPrecipitation(double mPrecipitation) {
        this.mPrecipitation = mPrecipitation;
    }

    public String getSummary() {
        return mSummary;
    }

    public void setSummary(String mSummary) {
        this.mSummary = mSummary;
    }

    public String getTimeZone() {
        return mTimeZone;
    }

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
