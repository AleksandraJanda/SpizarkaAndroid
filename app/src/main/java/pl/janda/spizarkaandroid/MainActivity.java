package pl.janda.spizarkaandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import pl.janda.spizarkaandroid._adapter.ProductListItemAdapter;
import pl.janda.spizarkaandroid._model.Product;

public class MainActivity extends AppCompatActivity {

    ListView list;
    private ProductListItemAdapter adapter;

    public static ArrayList<Product> products;

    EditText name;
    EditText unit;
    EditText quantity;

    public static String selectedProductName = "";
    public static String selectedProductUnit = "";
    public static String actionProductName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = findViewById(R.id.list);
        products = new ArrayList<>();

        readData();

        updateList();

        name = findViewById(R.id.name);
        unit = findViewById(R.id.unit);
        quantity = findViewById(R.id.quantity);

        name.addTextChangedListener(search());
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.delete_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        if(item.getItemId()==R.id.actionDelete){
            if(isOnList(actionProductName) >=0) {
                products.remove(isOnList(actionProductName));
                updateList();
                doToast("Usunięto");
            }

        } else if(item.getItemId()==R.id.actionBuy){
            doToast("Dodano do listy zakupów");
        }else{
            return false;
        }
        return true;
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

    public void onCopy(View view) {
        name.setText(selectedProductName);
        unit.setText(selectedProductUnit);
        selectedProductName = "";
        selectedProductUnit = "";
    }

    public void onAdd(View view) {
        onEdit(true);
    }

    public void onDelete(View view) {
        onEdit(false);
    }

    private void onEdit(boolean add) {
        if(areEditFieldsFilled()) {
            String productName = name.getText().toString();
            String productUnit = unit.getText().toString();
            double productQuantity = Double.parseDouble(quantity.getText().toString());

            if (isOnList(productName) < 0) {
                if (add) {
                    products.add(new Product(productName, productUnit, productQuantity));
                } else {
                    doToast("brak produktu na liście");
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

            saveData();

            name.setText("");
            unit.setText("");
            quantity.setText("");

            name.requestFocus();
        } else {
            doToast("wypełnij wszystkie pola");
        }
    }

    private void updateList(){
        Collections.sort(products, (item1, item2) -> item1.getName().compareTo(item2.getName()));

        adapter = new ProductListItemAdapter(products, getApplicationContext());
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        registerForContextMenu(list);

        saveData();
    }

    public static int isOnList(String name) {
        for(int i = 0; i < products.size(); i++) {
            if(products.get(i).getName().equals(name)) {
                return i;
            }
        }
        return -1;
    }

    private boolean areEditFieldsFilled() {
        return !name.getText().toString().equals("") &&
                !unit.getText().toString().equals("") &&
                !quantity.getText().toString().equals("");
    }

    // FILE
    private void saveData() {
        File file = new File(MainActivity.this.getFilesDir(), "text");
        if (!file.exists()) {
            file.mkdir();
        }
        try {
            File gpxfile = new File(file, "sample");
            FileWriter writer = new FileWriter(gpxfile);
            for(Product p: products) {
                writer.append(p.toString()+"\r\n");
            }
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readData() {
        String text;
        products = new ArrayList<>();
        File fileEvents = new File(MainActivity.this.getFilesDir() + "/text/sample");
        try{
            BufferedReader br = new BufferedReader(new FileReader(fileEvents));
            while((text = br.readLine()) != null) {
                String[] ss = text.split(",");
                Product product = new Product(ss[0].trim(), ss[1].trim(),
                        Double.parseDouble(ss[2].replace(";","").trim()));
                products.add(product);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // TOAST
    private void doToast(String msg){
        Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
        toast.show();
    }

}
