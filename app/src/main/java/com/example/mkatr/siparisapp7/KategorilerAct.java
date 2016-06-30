package com.example.mkatr.siparisapp7;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.ArrayList;

import adaptorler.KategorilerAdp;
import siniflar.Kategoriler;

public class KategorilerAct extends AppCompatActivity {

    ListView liste;
    ArrayList<Kategoriler> arrayList = new ArrayList<>();
    //Tiklanan üst categori id almak için
    static String tiklananCatId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kategoriler);

        //Üstte bulunan action bar'a title verildi.
        this.setTitle("Kategoriler");

        liste = (ListView) findViewById(R.id.katList);

        String url = "http://jsonbulut.com/json/companyCategory.php?ref=62cffa551aa88d0cb12e3b0f4918d889";
        new jsonOku(url, this).execute();
    }

    class jsonOku extends AsyncTask<Void, Void, Void> {
        private ProgressDialog pr;

        String url = "";
        String data = "";

        public jsonOku(String url, Activity ac) {
            this.url = url;
            pr = new ProgressDialog(ac);
            pr.setMessage("Yükleniyor, Lütfen bekleyiniz...");
            pr.show();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute(); // bekleme durumu
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                data = Jsoup.connect(url).ignoreContentType(true).execute().body();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                pr.dismiss();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);// biten nokta
            try {
                JSONObject obj = new JSONObject(data);
                JSONArray kat = obj.getJSONArray("Kategoriler");
                JSONObject katObj = kat.getJSONObject(0);
                JSONArray cat = katObj.getJSONArray("Categories");

                for (int i = 0; i < cat.length(); i++) {
                    JSONObject bil = cat.getJSONObject(i);
                    if (bil.getString("TopCatogryId").equals("0")) {
                        String catId = bil.getString("CatogryId");
                        String catName = bil.getString("CatogryName");
                        Kategoriler kategoriler = new Kategoriler(catId, catName);
                        arrayList.add(kategoriler);
                    }
                }

                KategorilerAdp adp = new KategorilerAdp(KategorilerAct.this, arrayList);
                liste.setAdapter(adp);

                liste.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        try {
                            //Tıklanan'a ait olan bilgileri daha önce set edilmiş sınıftan get edilerek alındı
                            Kategoriler ur = arrayList.get(position);
                            tiklananCatId = ur.getCatogryId();
                            //Bir sonraki sayfaya geçildi.
                            Intent i = new Intent(KategorilerAct.this, KategorilerAlt.class);
                            startActivity(i);
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "ID yakalama hatası: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
