package pl.janda.spizarkaandroid;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

import pl.janda.spizarkaandroid._adapter.ProductListItemAdapter;
import pl.janda.spizarkaandroid._model.Product;

public class MainActivity extends AppCompatActivity {

    ListView list;
    private ProductListItemAdapter adapter;

    ArrayList<Product> products;

    EditText name;
    EditText unit;
    EditText quantity;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = findViewById(R.id.list);
        products = new ArrayList<>();
        save();
//        readData();
        read();

        updateList();


        name = findViewById(R.id.name);
        unit = findViewById(R.id.unit);
        quantity = findViewById(R.id.quantity);

        name.addTextChangedListener(search());
    }

    public TextWatcher search(){
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                MainActivity.this.adapter.getFilter().filter(s);
                for(int i = 0; i < products.size(); i++) {
                    if(products.get(i).getName().startsWith(s.toString())) {
                        unit.setText(products.get(i).getUnit());
                        break;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }

    public void onAdd(View view) {
        onEdit(true);
    }

    public void onDelete(View view) {
        onEdit(false);
    }

    private void onEdit(boolean add) {
        String productName = name.getText().toString();
        String productUnit = unit.getText().toString();
        double productQuantity = Double.parseDouble(quantity.getText().toString());

        if(isOnList(productName) < 0) {
            if(add) {
                products.add(new Product(productName, productUnit, productQuantity));
            } else {
                Toast toast = Toast.makeText(getApplicationContext(),"brak produktu na liście",
                        Toast.LENGTH_SHORT);
                toast.show();
            }
        } else {
            if (!add) {
                productQuantity = -productQuantity;
            }

            double changedQuantity = products.get(isOnList(productName)).getQuantity() + productQuantity;

            if (changedQuantity <= 0) {
                products.remove(isOnList(productName));
            } else {
                products.get(isOnList(productName)).setQuantity(changedQuantity);
            }
        }

        updateList();

        name.setText("");
        unit.setText("");
        quantity.setText("");
    }

    private void updateList(){
        Collections.sort(products, (item1, item2) -> item1.getName().compareTo(item2.getName()));

        adapter = new ProductListItemAdapter(products, getApplicationContext());
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        save();
    }

    private int isOnList(String name) {
        for(int i = 0; i < products.size(); i++) {
            if(products.get(i).getName().equals(name)) {
                return i;
            }
        }
        return -1;
    }

    // FILE
    private void readData() {
        String text;
        products = new ArrayList<>();
        try{
            InputStream inputStream = getAssets().open("items.txt");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            text = new String(buffer);

            String[] inputArray = text.split(";");
            for(String s: inputArray) {
                String[] ss = s.split(",");
                Product product = new Product(ss[0].trim(), ss[1].trim(), Double.parseDouble(ss[2].trim()));
                products.add(product);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void save() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(products);

        editor.putString("products", json);
        editor.commit();

        read();
    }

    private void read() {
        String response = sharedPreferences.getString("products", "");
        Gson gson1 = new Gson();
        products = gson1.fromJson(response, new TypeToken<List<Product>>(){}.getType());
        for(Product p: products) {
            System.out.println(p);
        }
    }

}
