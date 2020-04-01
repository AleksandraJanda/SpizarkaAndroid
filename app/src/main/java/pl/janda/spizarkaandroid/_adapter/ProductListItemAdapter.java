package pl.janda.spizarkaandroid._adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

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
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.txtName.setText(product.getName());
        viewHolder.txtUnit.setText(product.getUnit());
        viewHolder.txtQuantity.setText(String.valueOf(product.getQuantity()));

//        convertView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MainActivity.selectedProduct.setName(product.getName());
//                MainActivity.selectedProduct.setUnit(product.getUnit());
//                MainActivity.selectedProduct.setQuantity(product.getQuantity());
//                System.out.println(MainActivity.selectedProduct);
//            }
//        });

        return convertView;
    }

}
