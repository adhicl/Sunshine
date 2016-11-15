package com.sunshineapp.activity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.sunshineapp.R;
import com.sunshineapp.adapter.CuacaRVAdapter;
import com.sunshineapp.data.CuacaDBHelper;
import com.sunshineapp.model.SunshineURL;
import com.sunshineapp.pojo.CuacaRamalan;
import com.sunshineapp.pojo.List;
import com.sunshineapp.pojo.Weather;
import com.sunshineapp.singleton.GsonSingleton;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    CuacaRVAdapter mCuacaAdapter;

    class AmbilCuacaSekarangTask extends AsyncTask<Void, Void, CuacaRamalan> {
        public AmbilCuacaSekarangTask(){

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
                java.util.List<List> lists = cuacaRamalan.getList();
                ContentValues[] contentValues = new ContentValues[lists.size()];
                for (int i =0; i<lists.size(); i++) {
                    int cityID= 1642911;
                    int date = lists.get(i).getDt();
                    ContentValues cv = new ContentValues();
                    cv.put(CuacaDBHelper.CITY_ID, 1642911);
                    cv.put(CuacaDBHelper.COLUMN_DT, date);
                    contentValues[i] = cv;
                }
                getContentResolver().delete(
                        Uri.parse("content://com.sunshineapp/ramalan"),
                        null,
                        null
                );
                getContentResolver().bulkInsert(
                        Uri.parse ("content://com.sunshineapp/ramalan"),
                        contentValues);
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
            // mCuacaAdapter = new CuacaRVAdapter(c.getList());
            mCuacaAdapter.updateList(c.getList());
            mCuacaAdapter.notifyDataSetChanged();
//            Toast.makeText(MainActivity.this,s, Toast.LENGTH_LONG).show();
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

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        LinearLayoutManager llm = new LinearLayoutManager(MainActivity.this,
                LinearLayoutManager.VERTICAL,false);

        mRecyclerView.setLayoutManager(llm);
        mCuacaAdapter = new CuacaRVAdapter(null);
        mRecyclerView.setAdapter(mCuacaAdapter);

        new AmbilCuacaSekarangTask().execute();
    }

}