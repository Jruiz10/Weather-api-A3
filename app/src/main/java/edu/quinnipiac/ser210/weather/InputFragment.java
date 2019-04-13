package edu.quinnipiac.ser210.weather;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class InputFragment extends Fragment {

    EditText searchBar;
    Button btnSearch;
    TextView displayInfo;
    String url;


    interface Listener{

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_input, container, false);
        searchBar = view.findViewById(R.id.searchBar);
        btnSearch = view.findViewById(R.id.btnSearch);
        displayInfo = view.findViewById(R.id.displayInfo);
        url = "https://community-open-weather-map.p.rapidapi.com/find?type=link%2C+accurate&units=imperial&q=hamden";
        new NetworkCall().execute(url);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                url = "https://community-open-weather-map.p.rapidapi.com/find?type=link%2C+accurate&units=imperial&q=" + searchBar.getText();
                new NetworkCall().execute(url);
            }
        });
        return view;
    }

    private class NetworkCall extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String weatherDataJSON = null;

            try{
                URL url = new URL(strings[0]);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("X-RapidAPI-Key","1a0163a90fmshd163a24e170daccp1b2e7ejsn49b0f038e29a");

                urlConnection.connect();

                InputStream in = urlConnection.getInputStream();
                if(in == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(in));

                weatherDataJSON = getBufferStringFromBuffer(reader).toString();

            } catch (Exception e) {
                Log.e("error","Error" + e.getMessage());
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null){
                    try{
                        reader.close();
                    } catch (IOException e){
                        Log.e("error", "Error" + e.getMessage());
                        return null;
                    }
                }
            }
            return weatherDataJSON;
        }

        protected void onPostExecute(String result){
            if (result != null){
                try {
                    String displayString = new JSONDataHandler().getWeatherData(result);
                    displayInfo.setText(displayString);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
        private StringBuffer getBufferStringFromBuffer(BufferedReader br) throws Exception{
            StringBuffer buffer = new StringBuffer();

            String line;
            while((line = br.readLine()) != null){
                buffer.append(line + '\n');
            }

            if (buffer.length() == 0)
                return null;

            return buffer;
        }
    }
}