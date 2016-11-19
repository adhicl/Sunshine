package com.sunshineapp.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.sunshineapp.R;
import com.sunshineapp.model.SunshineURL;
import com.sunshineapp.pojo.CuacaRamalan;
import com.sunshineapp.singleton.GsonSingleton;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;

    class AmbilCuacaRamalanTask extends AsyncTask<Void, Void, CuacaRamalan> {
        public AmbilCuacaRamalanTask(){

        }

        @Override
        protected CuacaRamalan doInBackground(Void... voids) {
            HttpURLConnection conn= null;
            try {
                URL url = new URL(SunshineURL.getCuacaRamalan("Jakarta"));
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(5000);
                conn.setConnectTimeout(5000);
                conn.setRequestMethod("GET");
                conn.connect();

                int responseCode = conn.getResponseCode();
                if (responseCode!= HttpURLConnection.HTTP_OK) {
                    return null;
                }
                InputStream is = conn.getInputStream();
                if (null == is) {
                    return null;
                }
                StringBuilder hasilStringBuilder = new StringBuilder();

                BufferedReader bReader =
                        new BufferedReader(
                            new InputStreamReader(is));

                String strLine;
                /** Reading the contents of the file , line by line */
                while ((strLine = bReader.readLine()) != null) {
                    hasilStringBuilder.append(strLine);
                }
                CuacaRamalan cuacaRamalan =
                        GsonSingleton.getGson().fromJson(hasilStringBuilder.toString()
                        , CuacaRamalan.class);
                return cuacaRamalan;
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
        protected void onPostExecute(CuacaRamalan c) {
            super.onPostExecute(c);
//            Toast.makeText(MainActivity.this,s, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);

        new AmbilCuacaRamalanTask().execute();
    }

}
