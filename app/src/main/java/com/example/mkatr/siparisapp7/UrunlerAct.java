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
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import adaptorler.UrunlerAdp;
import siniflar.Urunler;

public class UrunlerAct extends AppCompatActivity {

    ListView ls;
    final List<Urunler> listeArray = new ArrayList<>();
    //Ürün yoksa uyarı vermek için
    TextView urunYok;
    static String tiklananUrunId;
    //Birdaha json üretip link göndermemek için bilgiler static yapıldı.
    //Bunun amacı detay sayfasında tekrardan json üretmemek için.
    static JSONArray bilgiler = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_urunler);

        this.setTitle("Ürünler");

        ls = (ListView) findViewById(R.id.urunlerList);
        urunYok = (TextView) findViewById(R.id.txtUrunYok);
        urunYok.setVisibility(View.INVISIBLE);

        //Burada tüm var olan ürünleri alıyoruz.
        //Çünkü sepetten ürün detayını görmek istediğinde tüm ürünlerin gelmesi lazım ki ürünü bulabileyim.
        //Eger tüm ürünler değilde bir categoriye ait olan ürünler gelirse o ürünü bulamam.
        String url2 = "http://jsonbulut.com/json/product.php?ref=62cffa551aa88d0cb12e3b0f4918d889&start=1";
        new jsonOku(url2, this).execute();
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
                JSONArray products = obj.getJSONArray("Products");
                JSONObject katObj = products.getJSONObject(0);

                if(katObj.isNull("bilgiler")){
                    urunYok.setVisibility(View.VISIBLE);
                    urunYok.setText("Bu alt kategoride ait herhangi bir ürün bulunmamaktadır. \n Lütfen geri gelin.");
                }

                bilgiler  = katObj.getJSONArray("bilgiler");
                if (bilgiler.length()>0) {

                    String thumb = null;
                    for (int i = 0; i < bilgiler.length(); i++) {
                        JSONObject bilgi = bilgiler.getJSONObject(i);
                        JSONArray categories = bilgi.getJSONArray("categories");
                        JSONObject cat2 = categories.getJSONObject(1);
                        //Burada tiklananAltCatId aslında en altta tıkladığı categorinin id si.
                        //Bu alt kategoriye ait olan ürünleri getirmek içn bu şekilde yaptık.
                        if (cat2.getString("categoryId").equals(KategorilerAlt.tiklananAltCatId)){
                            String productId = bilgi.getString("productId");
                            String productName = bilgi.getString("productName");
                            String description = bilgi.getString("description");
                            String price = bilgi.getString("price");
                            String imgDurum = bilgi.getString("image");

                            if (!imgDurum.equals("false")) {
                                JSONArray resimAr = bilgi.getJSONArray("images");
                                thumb = resimAr.getJSONObject(0).getString("normal");
                            } else {
                                thumb = "http://vincinmerkezi.com/images/resimyok.gif";
                            }

                            Urunler ur = new Urunler(productId, productName, description, price, thumb);
                            listeArray.add(ur);
                        }

                    }
                }

                UrunlerAdp adp = new UrunlerAdp(UrunlerAct.this, listeArray);
                ls.setAdapter(adp);

                ls.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        try {
                            //Tıklanan'a ait olan bilgileri daha önce set edilmiş sınıftan get edilerek alındı
                            Urunler ur = listeArray.get(position);
                            tiklananUrunId = ur.getProductId();
                            Toast.makeText(getApplicationContext(), "Urun id: "+tiklananUrunId, Toast.LENGTH_SHORT).show();
                            //Bir sonraki sayfaya geçildi.
                            Intent i = new Intent(UrunlerAct.this, DetaylarAct.class);
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
