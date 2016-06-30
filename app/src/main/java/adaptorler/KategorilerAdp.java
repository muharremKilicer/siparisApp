package adaptorler;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.mkatr.siparisapp7.R;

import java.util.List;

import siniflar.Kategoriler;

/**
 * Created by mkatr on 30.6.2016.
 */
public class KategorilerAdp extends BaseAdapter {

    private LayoutInflater inf;
    private List<Kategoriler> katls;
    private Activity ac;

    public KategorilerAdp(Activity activity, List<Kategoriler> urls) {
        inf = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.katls = urls;
        ac = activity;
    }

    @Override
    public int getCount() {//ürün sayısı kadar dön
        return katls.size();
    }

    @Override
    public Kategoriler getItem(int position) {
        return katls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView;
        rowView = inf.inflate(R.layout.katrow, null);
        TextView catName = (TextView) rowView.findViewById(R.id.txtCatName);

        Kategoriler ul = katls.get(position);
        catName.setText(ul.getCatogryName());

        return rowView;
    }
}
