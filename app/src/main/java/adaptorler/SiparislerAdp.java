package adaptorler;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mkatr.siparisapp7.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import siniflar.Siparisler;

/**
 * Created by mkatr on 30.6.2016.
 */
public class SiparislerAdp extends BaseAdapter {

    private LayoutInflater inf;
    private List<Siparisler> katls;
    private Activity ac;

    public SiparislerAdp(Activity activity, List<Siparisler> urls) {
        inf = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.katls = urls;
        ac = activity;
    }

    @Override
    public int getCount() {//ürün sayısı kadar dön
        return katls.size();
    }

    @Override
    public Siparisler getItem(int position) {
        return katls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView;
        rowView = inf.inflate(R.layout.listerow, null);
        TextView urunBaslik = (TextView) rowView.findViewById(R.id.txtUrunBaslik);
        TextView tarih = (TextView) rowView.findViewById(R.id.txtYayinTarih);
        ImageView resim = (ImageView) rowView.findViewById(R.id.resim);

        Siparisler ul = katls.get(position);
        urunBaslik.setText(ul.getUrun_adi());
        tarih.setText(ul.getEklenme_tarihi());
        String imgUrl;
        if (!ul.getResAdi().equals("null")){
            imgUrl= "http://jsonbulut.com/admin/resim/server/php/files/"+ul.getResKlasör()+"/thumbnail/"+ul.getResAdi();
            Picasso.with(ac).load(imgUrl).into(resim);
        }
        if(ul.getResAdi().equals("null")){
            imgUrl="http://vincinmerkezi.com/images/resimyok.gif";
            Picasso.with(ac).load(imgUrl).into(resim);
        }




        return rowView;
    }
}
