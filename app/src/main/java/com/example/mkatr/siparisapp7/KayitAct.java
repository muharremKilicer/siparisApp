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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KayitAct extends AppCompatActivity {

    /*
     Sol tarafta siniflari ve onlara ait olan adaptör leri ayırdım.
     Ve rowların başında ek getirdim. Örn: urunrow gibi.
     */
    SharedPreferences sha;
    SharedPreferences.Editor edit;
    EditText userName, userSurname, userPhone, userMail, userPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Üstte bulunan action bar'a title verildi.
        this.setTitle("Kayıt Ol");

        sha = getSharedPreferences("kul", MODE_PRIVATE);
        edit = sha.edit();

        userName = (EditText) findViewById(R.id.txtUserName);
        userSurname = (EditText) findViewById(R.id.txtUserSurname);
        userPhone = (EditText) findViewById(R.id.txtUserPhone);
        userMail = (EditText) findViewById(R.id.txtUserMail);
        userPass = (EditText) findViewById(R.id.txtUserPass);

        if (!sha.getString("kullaniciId", "").equals("")) {
            // daha önce kullanıcı kayıtlı
            Intent i = new Intent(KayitAct.this, KategorilerAct.class);
            startActivity(i);
            finish();

            //Kayıtlı kullanıcı silmek için
            //edit.clear();
            //edit.commit();
        }
    }

    public void fncKayit(View v) {
        if (userName.getText().toString().trim().length() == 0) {
            userName.setError("Kullanıcı adı zorunludur.");
            userName.setText("");
            userName.requestFocus();
        } else if (userSurname.getText().toString().trim().length() == 0) {
            userSurname.setError("Kullanıcı soyadı zorunludur.");
            userSurname.setText("");
            userSurname.requestFocus();
        } else if (userPhone.getText().toString().trim().length() < 11) {
            userPhone.setError("Telefon zorunludur.");
            userPhone.setText("");
            userPhone.requestFocus();
        } else if (!isEmailValid(userMail.getText().toString().trim())) {
            userMail.setError("Mail adresi uygun degil." + "\n" + "user@example.com");
            userMail.setText("");
            userMail.requestFocus();
        } else if (userPass.getText().toString().trim().length() < 4) {
            userPass.setError("Şifreniz en az 4 karakter içermelidir");
            userPass.setText("");
            userPass.requestFocus();
        } else {
            String url = "http://jsonbulut.com/json/userRegister.php?ref=62cffa551aa88d0cb12e3b0f4918d889&userName=" +
                    "" + userName.getText().toString().trim() + "&userSurname=" +
                    "" + userSurname.getText().toString().trim() + "&userPhone=" +
                    "" + userPhone.getText().toString().trim() + "&userMail=" +
                    "" + userMail.getText().toString().trim() + "&userPass=" +
                    "" + userPass.getText().toString().trim() + "";
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

                if (!yaz.isNull("kullaniciId")) {
                    // Kullanıcı girişi başarılı
                    // Sadece kullanıcı id yeterli profil sayfası olmayacak
                    edit.putString("kullaniciId", yaz.getString("kullaniciId"));
                    edit.commit();
                    Toast.makeText(getApplicationContext(), yaz.getString("mesaj"), Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(KayitAct.this, KategorilerAct.class);
                    startActivity(i);
                    finish();
                } else {
                    // kullanıcı girişi başarısız
                    Toast.makeText(getApplicationContext(), yaz.getString("mesaj"), Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * method is used for checking valid email id format.
     *
     * @param email
     * @return boolean true for valid false for invalid
     */
    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public void girisSayfasi(View v) {
        Intent i = new Intent(KayitAct.this, GirisAct.class);
        startActivity(i);
    }
}
