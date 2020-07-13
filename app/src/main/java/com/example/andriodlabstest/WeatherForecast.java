package com.example.andriodlabstest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.xmlpull.v1.XmlPullParser.START_TAG;

public class WeatherForecast extends AppCompatActivity {

    private ProgressBar progressBar;
    private ImageView weatherImage;
    private TextView currentTemperature;
    private TextView minTemperature;
    private TextView maxTemperature;
    private TextView uvRating;
    private static final String ACTIVITY_NAME = "WeatherForecast";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_forecast);

        weatherImage = findViewById(R.id.currentWeather);
        currentTemperature = findViewById(R.id.currentTemperature);
        minTemperature = findViewById(R.id.minTemperature);
        maxTemperature = findViewById(R.id.maxTemperature);
        uvRating = findViewById(R.id.uvRating);
        progressBar = findViewById(R.id.progressBar);

        progressBar.setVisibility(View.VISIBLE);

        String ottawaWeatherUrl = "http://api.openweathermap.org/data/2.5/weather?q=ottawa,ca&APPID=7e943c97096a9784391a981c4d878b22&mode=xml&units=metric";
        String UvUrl = "http://api.openweathermap.org/data/2.5/uvi?appid=7e943c97096a9784391a981c4d878b22&lat=45.348945&lon=-75.759389";

        ForecastQuery forecastQuery = new ForecastQuery();
        forecastQuery.execute( ottawaWeatherUrl, UvUrl);

    }

    private class ForecastQuery extends AsyncTask<String, Integer, String> {
        private String UV;
        private String min;
        private String max;
        private String current_temp;
        private Bitmap image;
        private String iconName;

        protected String doInBackground(String... args) {

            String returnString = null;

            try {

                URL weatherUrl = new URL(args[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) weatherUrl.openConnection();
                InputStream inputStreamWeather = urlConnection.getInputStream();

                XmlPullParserFactory pullParserFactory = XmlPullParserFactory.newInstance();
                pullParserFactory.setNamespaceAware(false);
                XmlPullParser xpp = pullParserFactory.newPullParser();
                xpp.setInput(inputStreamWeather, "UTF-8");

                int EVENT_TYPE;
                while ((EVENT_TYPE = xpp.getEventType()) != XmlPullParser.END_DOCUMENT) {

                    if (EVENT_TYPE == START_TAG) {

                        String tagName = xpp.getName();

                        switch (tagName) {
                            case "temperature":
                                current_temp = xpp.getAttributeValue(null, "value");
                                publishProgress(25);
                                min = xpp.getAttributeValue(null, "min");
                                publishProgress(50);
                                max = xpp.getAttributeValue(null, "max");
                                publishProgress(75);
                                break;
                            case "weather":
                                iconName = xpp.getAttributeValue(null, "icon");
                        }
                    }
                    xpp.next();
                }

                String fileName = iconName + ".png";

                Log.i(ACTIVITY_NAME, "Searching for " + fileName);

                if (!fileExistence(fileName)) {

                    Log.i(ACTIVITY_NAME, "Downloading image from server");
                    image = null;
                    URL url = new URL("http://openweathermap.org/img/w/" + fileName);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.connect();

                    int responseCode = connection.getResponseCode();

                    if (responseCode == 200) {
                        image = BitmapFactory.decodeStream(connection.getInputStream());
                    }

                    Log.i(ACTIVITY_NAME, "Image downloaded from server");

                    FileOutputStream outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
                    image.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
                    outputStream.flush();
                    outputStream.close();

                    Log.i(ACTIVITY_NAME, "Image saved to local storage");

                } else {

                    Log.i(ACTIVITY_NAME, "Downloading " + fileName + " from local storage");

                    FileInputStream fis = null;
                    try {
                        fis = openFileInput(fileName);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    image = BitmapFactory.decodeStream(fis);
                }
                publishProgress(100);

                URL uvUrl = new URL(args[1]);
                HttpURLConnection uvUrlConnection = (HttpURLConnection) uvUrl.openConnection();
                InputStream inStreamUv = uvUrlConnection.getInputStream();

                BufferedReader jsonReader = new BufferedReader(new InputStreamReader(inStreamUv, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder(100);

                String line;

                while ((line = jsonReader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                String result = sb.toString();

                JSONObject jObject = new JSONObject(result);
                UV = String.valueOf(jObject.getDouble("value"));

            } catch (Exception e) {
                returnString = "error";
            }
            return returnString;
        }


        public boolean fileExistence(String fname) {
            File file = getBaseContext().getFileStreamPath(fname);
            return file.exists();
        }

        @Override
        protected void onProgressUpdate(Integer... value) {
            super.onProgressUpdate(value);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(value[0]);
        }

        @Override
        protected void onPostExecute(String sentFromDoInBackground) {
            super.onPostExecute(sentFromDoInBackground);
            char celsiusSymbol = 0x2103;

            weatherImage.setImageBitmap(image);
            currentTemperature.setText(String.format("Current Temp: %s%c", current_temp, celsiusSymbol));
            maxTemperature.setText(String.format("Max: %s%c", max, celsiusSymbol));
            minTemperature.setText(String.format("Min: %s%c", min, celsiusSymbol));
            uvRating.setText(String.format("UV Index: %s", UV));

            progressBar.setVisibility(View.INVISIBLE);
        }
    }

}