package adaptorler;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.example.mkatr.siparisapp7.R;

import java.util.List;

import siniflar.Urunler;

/**
 * Created by mkatr on 30.6.2016.
 */
public class UrunlerAdp extends BaseAdapter {

    private LayoutInflater inf;
    private List<Urunler> urls;
    private Activity ac;

    public UrunlerAdp(Activity activity, List<Urunler> urls) {
        inf = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.urls = urls;
        ac = activity;
    }

    @Override
    public int getCount() {//ürün sayısı kadar dön
        return urls.size();
    }

    @Override
    public Urunler getItem(int position) {
        return urls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView;
        rowView = inf.inflate(R.layout.urunrow, null);
        TextView urunBaslik = (TextView) rowView.findViewById(R.id.txtUrunBaslik);
        ImageView resim = (ImageView) rowView.findViewById(R.id.resim);

        Urunler ul = urls.get(position);
        urunBaslik.setText(ul.getProductName());
        Picasso.with(ac).load(ul.getImages()).into(resim);

        return rowView;
    }
}
