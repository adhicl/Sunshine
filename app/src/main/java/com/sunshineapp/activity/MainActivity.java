package com.sunshineapp.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.sunshineapp.R;
import com.sunshineapp.model.SunshineURL;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    class AmbilCuacaRamalanTask extends AsyncTask<Void, Void, String> {
        public AmbilCuacaRamalanTask(){

        }

        @Override
        protected String doInBackground(Void... voids) {
            HttpURLConnection conn= null;
            try {
                URL url = new URL(SunshineURL.getCuacaRamalan("Jakarta"));
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(5000);
                conn.setConnectTimeout(5000);
                conn.setRequestMethod("GET");
                conn.connect();

                InputStream is = conn.getInputStream();
                StringBuilder hasilStringBuilder = new StringBuilder();

                BufferedReader bReader =
                        new BufferedReader(
                            new InputStreamReader(is));

                String strLine;
                /** Reading the contents of the file , line by line */
                while ((strLine = bReader.readLine()) != null) {
                    hasilStringBuilder.append(strLine);
                }
                return hasilStringBuilder.toString();
            }
            catch (Exception e) {
                return null;
            }
            finally {
                if (null!= conn){
                    conn.disconnect();
                }
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(MainActivity.this,s, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);

        new AmbilCuacaRamalanTask().execute();
    }

}
