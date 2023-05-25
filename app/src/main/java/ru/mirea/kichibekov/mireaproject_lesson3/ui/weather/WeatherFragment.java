package ru.mirea.kichibekov.mireaproject_lesson3.ui.weather;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import ru.mirea.kichibekov.mireaproject_lesson3.MainActivity;
import ru.mirea.kichibekov.mireaproject_lesson3.databinding.FragmentWeatherBinding;

public class WeatherFragment extends Fragment {

    private FragmentWeatherBinding binding;

    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWeatherBinding.inflate(inflater, container, false);
        mContext = inflater.getContext();
        binding.Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DownloadPageTask().execute("https://ipinfo.io/json"); // запуск нового потока
            }
        });
        binding.Button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DownloadPageTask2().execute("https://api.open-meteo.com/v1/forecast?latitude=52.52&longitude=13.41&current_weather=true");

            }
        });
        return binding.getRoot();
    }

    @SuppressLint("StaticFieldLeak")
    private class DownloadPageTask extends AsyncTask<String, Void, String> {
        @SuppressLint("SetTextI18n")
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            binding.textIp.setText("ip: Загружаем...");
        }
        @Override
        protected String doInBackground(String... urls) {
            try {
                return downloadIpInfo(urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
                return "error";
            }
        }
        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String result) {
            //binding.TextView.setText(result);
            //Log.d(MainActivity.class.getSimpleName(), result);
            try {
                JSONObject responseJson = new JSONObject(result);
                //Log.d(MainActivity.class.getSimpleName(), "Response: " + responseJson);
                String ip = responseJson.getString("ip");
                binding.textCity.setText("City: " + responseJson.getString("city"));
                binding.textHost.setText("Hostname: " + responseJson.getString("hostname"));
                binding.textRegion.setText("Region: " +responseJson.getString("region"));
                binding.textIp.setText("ip: " + responseJson.getString("ip"));
                binding.textCountry.setText("Country: " + responseJson.getString("country"));
                binding.textLoc.setText("Loc: " + responseJson.getString("loc"));
                binding.textOrg.setText("Org: " + responseJson.getString("org"));
                binding.textPostal.setText("Postal: " +responseJson.getString("postal"));
                binding.textTimeZone.setText("TimeZone: " + responseJson.getString("timezone"));
                binding.textReadMe.setText("ReadMe: " + responseJson.getString("readme"));
                //Log.d(MainActivity.class.getSimpleName(), "IP: " + ip);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            super.onPostExecute(result);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class DownloadPageTask2 extends AsyncTask<String, Void, String> {
        @SuppressLint("SetTextI18n")
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            binding.textTemp.setText("Temperature: Загружаем...");
        }
        @Override
        protected String doInBackground(String... urls) {
            try {
                return downloadIpInfo(urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
                return "error";
            }
        }
        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String result) {
            //binding.TextView.setText(result);
            //Log.d(MainActivity.class.getSimpleName(), result);
            try {
                JSONObject responseJson = new JSONObject(result);
                //Log.d(MainActivity.class.getSimpleName(), "Response: " + responseJson);
                JSONObject weather = responseJson.getJSONObject("current_weather");
                binding.textTemp.setText("Temperature: " + weather.getString("temperature"));
                binding.textWindSpeed.setText("WindSpeed: " + weather.getString("windspeed"));
                binding.textWindDirection.setText("WindDirection: " + weather.getString("winddirection"));
                binding.textTime.setText("Time: " + weather.getString("time"));
                binding.textLatitude.setText("Latitude: " + responseJson.getString("latitude"));
                binding.textlongitude.setText("Longitude: " + responseJson.getString("longitude"));
                binding.textElevation.setText("Elevation: " + responseJson.getString("elevation"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            super.onPostExecute(result);
        }
    }
    private String downloadIpInfo(String address) throws IOException {
        InputStream inputStream = null;
        String data = "";
        try {
            URL url = new URL(address);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(100000);
            connection.setConnectTimeout(100000);
            connection.setRequestMethod("GET");
            connection.setInstanceFollowRedirects(true);
            connection.setUseCaches(false);
            connection.setDoInput(true);
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 200 OK
                inputStream = connection.getInputStream();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                int read = 0;
                while ((read = inputStream.read()) != -1) {
                    bos.write(read); }
                bos.close();
                data = bos.toString();
            } else {
                data = connection.getResponseMessage()+". Error Code: " + responseCode;
            }
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return data;
    }
}