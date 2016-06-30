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

import adaptorler.SiparislerAdp;
import siniflar.Siparisler;

public class SiparislerAct extends AppCompatActivity {

    ListView liste;
    ArrayList<Siparisler> arrayList = new ArrayList<>();
    static String tiklananSiparis,tiklananSiparisId;
    static String sayfaCode="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_siparisler);
        this.setTitle("Sepet");

        liste = (ListView) findViewById(R.id.siparislerList);

        String url = "http://jsonbulut.com/json/orderList.php?ref=62cffa551aa88d0cb12e3b0f4918d889&musterilerID=" + DetaylarAct.kulId;
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
                JSONArray order = obj.getJSONArray("orderList");
                JSONArray orderS = order.getJSONArray(0);
                for (int i = 0; i < orderS.length(); i++) {
                    JSONObject bil = orderS.getJSONObject(i);
                    if (!bil.isNull("urun_adi")) {
                        String urunSec = bil.getString("siparis_bilgisi");
                        String urunId = bil.getString("id_category");
                        String[] tokens = urunId.split(",");
                        String cat="";
                        for (String t : tokens){
                            cat=t;
                        }
                        String urunAdi = bil.getString("urun_adi");
                        String eklenmeTarihi = bil.getString("eklenme_tarihi");
                        String resimAdi = bil.getString("adi");
                        String klasor = bil.getString("klasor");
                        Siparisler siparisler = new Siparisler(cat, urunAdi, eklenmeTarihi, resimAdi, klasor,urunSec);
                        arrayList.add(siparisler);
                    }
                }

                SiparislerAdp adp = new SiparislerAdp(SiparislerAct.this, arrayList);
                liste.setAdapter(adp);

                liste.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        try {
                            //Tıklanan'a ait olan bilgileri daha önce set edilmiş sınıftan get edilerek alındı
                            Siparisler ur = arrayList.get(position);
                            tiklananSiparis = ur.getUrunId();
                            tiklananSiparisId = ur.getUrunSec();
                            sayfaCode="1992";
                            //Bir sonraki sayfaya geçildi.
                            Intent i = new Intent(SiparislerAct.this, DetaylarAct.class);
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
