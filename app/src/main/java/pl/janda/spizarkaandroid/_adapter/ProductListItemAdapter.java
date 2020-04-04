package pl.janda.spizarkaandroid._adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;

import pl.janda.spizarkaandroid.MainActivity;
import pl.janda.spizarkaandroid.R;
import pl.janda.spizarkaandroid._model.Product;

public class ProductListItemAdapter extends ArrayAdapter<Product> implements View.OnClickListener {

    private ArrayList<Product> dataSet;
    Context mContext;

    private static class ViewHolder {
        TextView txtName;
        TextView txtUnit;
        TextView txtQuantity;
        CheckBox checkBox;
    }

    public ProductListItemAdapter(ArrayList<Product> data, Context context) {
        super(context, R.layout.row, data);
        this.dataSet = data;
        this.mContext = context;
        this.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Product product = getItem(position);

        final ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row, parent, false);
            viewHolder.txtName = convertView.findViewById(R.id.name);
            viewHolder.txtUnit = convertView.findViewById(R.id.unit);
            viewHolder.txtQuantity = convertView.findViewById(R.id.quantity);
            viewHolder.checkBox = convertView.findViewById(R.id.checkbox);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.txtName.setText(product.getName());
        viewHolder.txtUnit.setText(product.getUnit());
        viewHolder.txtQuantity.setText(String.valueOf(product.getQuantity()));

        if(MainActivity.isOnList(MainActivity.moveList, product.getName()) >= 0) {
            viewHolder.checkBox.setChecked(true);
        } else {
            viewHolder.checkBox.setChecked(false);
        }

        viewHolder.checkBox.setOnClickListener(v -> {
            if(viewHolder.checkBox.isChecked()) {
                MainActivity.moveList.add(new Product(product.getName(), product.getUnit(), product.getQuantity()));
            }
        });

        if(product.getQuantity() == 0){
            convertView.setBackgroundColor(Color.parseColor("#ffa372"));
        } else {
            convertView.setBackgroundColor(Color.TRANSPARENT);
        }

        convertView.setOnClickListener(v -> {
            MainActivity.selectedProductName = product.getName();
            MainActivity.selectedProductUnit = product.getUnit();
        });

//        convertView.setOnCreateContextMenuListener((menu, v, menuInfo) -> {
//            menu.setHeaderTitle("Wybierz akcję dla: " + product.getName());
//            MainActivity.actionProductName = product.getName();
//        });

        return convertView;
    }

}
