package com.example.mkatr.siparisapp7;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.IOException;

public class GirisAct extends AppCompatActivity {

    //Giriş kısmında tekrar tekrar yazmamak için
    // Mail: aaa@a.ccc Şifre: aaaccc

    SharedPreferences sha;
    SharedPreferences.Editor edit;
    EditText userMail, userPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giris);

        //Üstte bulunan action bar'a title verildi.
        this.setTitle("Giriş Yap");

        sha = getSharedPreferences("kul", MODE_PRIVATE);
        edit = sha.edit();

        userMail = (EditText) findViewById(R.id.txtUserMail);
        userPass = (EditText) findViewById(R.id.txtUserPass);

        if (!sha.getString("kullaniciId", "").equals("")) {
            // daha önce kullanıcı kayıtlı
            Intent i = new Intent(GirisAct.this, KategorilerAct.class);
            startActivity(i);
            finish();
        }
    }

    public void fncGiris(View v) {
        if (!KayitAct.isEmailValid(userMail.getText().toString().trim())) {
            userMail.setError("Mail adresi uygun degil." + "\n" + "user@example.com");
            userMail.setText("");
            userMail.requestFocus();
        } else if (userPass.getText().toString().trim().length() < 4) {
            userPass.setError("Şifreniz en az 4 karakter içermelidir");
            userPass.setText("");
            userPass.requestFocus();
        } else {
            String url = "http://jsonbulut.com/json/userLogin.php?ref=62cffa551aa88d0cb12e3b0f4918d889&" +
                    "userEmail=" + userMail.getText().toString().trim() + "&" +
                    "userPass=" + userPass.getText().toString().trim() + "&face=no";
            new jsonOku(url, this).execute();
        }
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
                JSONArray arr = obj.getJSONArray("user");
                JSONObject yaz = arr.getJSONObject(0);

                if (!yaz.isNull("bilgiler")) {
                    // bilgiler var
                    JSONObject ar = yaz.getJSONObject("bilgiler");
                    edit.putString("kullaniciId", ar.getString("userId"));
                    edit.putString("adSoyad", ar.getString("userName") + " " + ar.getString("userSurname"));
                    edit.commit();
                    Intent i = new Intent(GirisAct.this, KategorilerAct.class);
                    startActivity(i);
                } else {
                    Toast.makeText(getApplicationContext(), "Kullanıcı adı yada şifre hatalı", Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void kayitSayfasi(View v) {
        Intent i = new Intent(GirisAct.this, KayitAct.class);
        startActivity(i);
    }
}
