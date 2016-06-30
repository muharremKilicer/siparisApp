package com.example.mkatr.siparisapp7;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.HashMap;

public class DetaylarAct extends AppCompatActivity {

    Button btnSiparis;
    private SliderLayout mDemoSlider;
    TextView xbilgi;
    WebView webView;
    SharedPreferences sha;
    SharedPreferences.Editor edit;
    static String kulId;
    String gelenTiklananUrunId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detaylar);

        this.setTitle("Ürün Detay");
        sha = getSharedPreferences("kul", MODE_PRIVATE);
        edit = sha.edit();
        mDemoSlider = (SliderLayout) findViewById(R.id.slider);
        xbilgi = (TextView) findViewById(R.id.txtBilgi);
        webView = (WebView) findViewById(R.id.webView);
        btnSiparis = (Button) findViewById(R.id.btnSiparis);

        gelenTiklananUrunId = UrunlerAct.tiklananUrunId;

        //Eğer sepetin olduğu sayfadan bir ürüne tıklarsa o sayfadan geldiğini anlamak
        //için o sayfaya bir kod verdim.
        if (SiparislerAct.sayfaCode.equals("1992")){
            gelenTiklananUrunId = SiparislerAct.tiklananSiparisId;
            //Sepet sayfasından geldiği için buton görünmüyor.
            //Ayrıca sayfaCode sıfırlıyoruz ki sepetten çıkıp anasayfadan başka bir alt
            //kategoriye ait ürünlerin de detayını görebilelim.
            SiparislerAct.sayfaCode="";
            btnSiparis.setVisibility(View.INVISIBLE);
        }
        //Toast.makeText(DetaylarAct.this, gelenTiklananUrunId, Toast.LENGTH_SHORT).show();
        //Slider burada biçimleniyor.
        HashMap<String, String> url_maps = new HashMap<>();

        try {

            for (int a = 0; a < UrunlerAct.bilgiler.length(); a++) {
                JSONObject bilgi = UrunlerAct.bilgiler.getJSONObject(a);

                //UrunlerAct sayfasından gelen id ile buradaki ürünü bul
                //Buldugunun resimlerini slayt haline getir
                if (bilgi.getString("productId").equals(gelenTiklananUrunId)){
                    String imgDurum = bilgi.getString("image");
                    //Fiyat ve adını yaz
                    xbilgi.setText("Ürün Adı: " + bilgi.getString("productName") + "\nFiyat: " + bilgi.getString("price"));
                    //Webview ekle
                    webView.getSettings().setJavaScriptEnabled(true);
                    webView.loadDataWithBaseURL("",bilgi.getString("description"),"text/html","UTF-8","");
                    gelenTiklananUrunId="1";
                    if (imgDurum.equals("true")) {
                        JSONArray resimler = bilgi.getJSONArray("images");
                        for (int i = 0; i < resimler.length(); i++) {
                            JSONObject resim = resimler.getJSONObject(i);
                            //Normal boyuttaki resimlerini ekliyoruz.
                            url_maps.put(i + "", resim.getString("normal"));
                        }
                    } else {
                        url_maps.put("", "http://vincinmerkezi.com/images/resimyok.gif");
                    }
                }

            }

            for (String sira : url_maps.keySet()) {
                TextSliderView textSliderView = new TextSliderView(this);
                // initialize a SliderLayout
                textSliderView
                        .description("Resimler")
                        .image(url_maps.get(sira))
                        .setScaleType(BaseSliderView.ScaleType.Fit);

                //add your extra information
                textSliderView.bundle(new Bundle());
                textSliderView.getBundle()
                        .putString("extra", sira + "");

                mDemoSlider.addSlider(textSliderView);
            }
            mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
            mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
            mDemoSlider.setCustomAnimation(new DescriptionAnimation());
            //Resimin durma süresi
            mDemoSlider.setDuration(2000);

        } catch (Exception e) {
            Toast.makeText(DetaylarAct.this, "Resim verisi bulunamadı.", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onStop() {
        //Üzerine tıklayınca slider dursun.
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        mDemoSlider.stopAutoCycle();
        super.onStop();
    }

    public void fncSiparisVer(View v){
        //Sipariş verirken onay alma için
        AlertDialog.Builder builder = new AlertDialog.Builder(DetaylarAct.this);
        builder.setTitle("Sipariş Onay");
        builder.setMessage("Siparişiniz Verilecek Onaylamıyormusunuz ?");
        builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                kulId = sha.getString("kullaniciId", "");
                //Toast.makeText(Detaylar.this, kulId, Toast.LENGTH_SHORT).show();
                String url = "http://jsonbulut.com/json/orderForm.php?ref=62cffa551aa88d0cb12e3b0f4918d889&customerId="+kulId+"&productId="+UrunlerAct.tiklananUrunId+"&html="+UrunlerAct.tiklananUrunId;
                new jsonOku(url, DetaylarAct.this).execute();
            }
        });
        builder.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.create();
        builder.show();


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
                JSONArray order = obj.getJSONArray("order");
                JSONObject orderS = order.getJSONObject(0);
                String durum = orderS.getString("durum");
                if (durum.equals("true")){
                    Intent i = new Intent(DetaylarAct.this, SiparislerAct.class);
                    startActivity(i);
                }else{
                    Toast.makeText(getApplicationContext(), "Sipariş verilemedi.", Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
