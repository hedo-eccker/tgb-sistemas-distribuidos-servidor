package model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;

public class WeatherReport {

    public String cityName;
    public double degreesInCelsius;
    public Date reportDate;

    public WeatherReport(String cityName, double degreesInCelsius, Date reportDate) {
        this.cityName = cityName;
        this.degreesInCelsius = degreesInCelsius;
        this.reportDate = reportDate;
    }

    public String toJsonString() {
        GsonBuilder builder = new GsonBuilder();
        builder.setDateFormat("dd/MM/yyyy");
        Gson gson = builder.create();

        return gson.toJson(this);
    }

}

