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
import android.widget.ImageView;
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
    public static ArrayList<Product> buyList;
    public static ArrayList<Product> moveList;

    EditText name;
    EditText unit;
    EditText quantity;
    ImageView logo;

    MenuItem buyOption;

    public static String selectedProductName = "";
    public static String selectedProductUnit = "";
    public static String actionProductName = "";
    private boolean isProductList = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = findViewById(R.id.list);
        products = new ArrayList<>();
        buyList = new ArrayList<>();
        moveList = new ArrayList<>();

        readData();

        updateList(products);

        name = findViewById(R.id.name);
        unit = findViewById(R.id.unit);
        quantity = findViewById(R.id.quantity);

        name.addTextChangedListener(search());

        logo = findViewById(R.id.logo);

        buyOption = findViewById(R.id.actionBuy);
    }

//    @Override
//    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
//    {
//        super.onCreateContextMenu(menu, v, menuInfo);
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.product_menu, menu);
//    }

//    @Override
//    public boolean onContextItemSelected(MenuItem item){
//        if(item.getItemId()==R.id.actionDelete){
//            if(isProductList) {
//                deleteProductFromList(products);
//            } else {
//                deleteProductFromList(buyList);
//            }
//        } else if(item.getItemId()==R.id.actionBuy){
//            if(isOnList(products, actionProductName) >=0
//                    && isOnList(buyList, actionProductName) < 0) {
//                buyList.add(products.get(isOnList(products, actionProductName)));
//                doToast("Dodano do listy zakupów");
//            }
//        } else if(item.getItemId()==R.id.actionStore){
//            if(isOnList(products, actionProductName) >=0
//                    && isOnList(buyList, actionProductName) < 0) {
//                products.add(buyList.get(isOnList(buyList, actionProductName)));
//                doToast("Dodano do listy produktów");
//            }
//        } else {
//            return false;
//        }
//        return true;
//    }
//
//    private void deleteProductFromList(ArrayList<Product> currentList) {
//        if(isOnList(currentList, actionProductName) >=0) {
//            currentList.remove(isOnList(currentList, actionProductName));
//            updateList(currentList);
//            doToast("Usunięto");
//        }
//    }

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

    public void onMove(View view) {
        if(moveList.size() > 0) {
            if (isProductList) {
                for (Product p : moveList) {
                    listEdit(buyList, true, p.getName(), p.getUnit(), p.getQuantity());
                }
                for (Product p : moveList) {
                    products.remove(products.get(isOnList(products, p.getName())));
                }
                updateList(products);
                doToast("Przeniesiono na listę zakupów");
            } else {
                for (Product p : moveList) {
                    listEdit(products, true, p.getName(), p.getUnit(), p.getQuantity());
                }
                for (Product p : moveList) {
                    buyList.remove(buyList.get(isOnList(buyList, p.getName())));
                }
                updateList(buyList);
                doToast("Przeniesiono na listę produktów");
            }
        }
        moveList = new ArrayList<>();
    }

    public void onDelete(View view) {
        if(moveList.size() > 0) {
            if (isProductList) {
                for (Product p : moveList) {
                    products.remove(products.get(isOnList(products, p.getName())));
                }
                updateList(products);
            } else {
                for (Product p : moveList) {
                    buyList.remove(buyList.get(isOnList(buyList, p.getName())));
                }
                updateList(buyList);
            }
            doToast("Usunięto produkty");
        }
        moveList = new ArrayList<>();
    }

    public void onAdd(View view) {
        onEdit(true);
    }

    public void onSubtract(View view) {
        onEdit(false);
    }

    private void onEdit(boolean add) {
        if(areEditFieldsFilled()) {
            String productName = name.getText().toString();
            String productUnit = unit.getText().toString();
            double productQuantity = Double.parseDouble(quantity.getText().toString());

            if(isProductList) {
                listEdit(products, add, productName, productUnit, productQuantity);
            } else {
                listEdit(buyList, add, productName, productUnit, productQuantity);
            }

            changeList();

            saveData();

            name.setText("");
            unit.setText("");
            quantity.setText("");

            name.requestFocus();
        } else {
            doToast("Wypełnij wszystkie pola");
        }
    }

    private void listEdit(ArrayList<Product> currentList, boolean add,
                          String productName, String productUnit, double productQuantity) {
        int position = isOnList(currentList, productName);
        if (position < 0) {
            if (add) {
                currentList.add(new Product(productName, productUnit, productQuantity));
            } else {
                doToast("Brak produktu na liście");
            }
        } else {
            if (!add) {
                productQuantity = -productQuantity;
            }

            double changedQuantity = currentList.get(position).getQuantity() + productQuantity;

            if (changedQuantity <= 0) {
                currentList.get(position).setQuantity(0);
            } else {
                currentList.get(position).setQuantity(changedQuantity);
            }
        }
    }

    private void updateList(ArrayList<Product> currentList){
        Collections.sort(currentList, (item1, item2) -> item1.getName().compareTo(item2.getName()));

        adapter = new ProductListItemAdapter(currentList, getApplicationContext());
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        registerForContextMenu(list);

        saveData();
    }

    public static int isOnList(ArrayList<Product> currentList, String name) {
        for(int i = 0; i < currentList.size(); i++) {
            if(currentList.get(i).getName().equals(name)) {
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
        saveDataToFile(products, "sample");
        saveDataToFile(buyList, "buy");
    }

    private void saveDataToFile(ArrayList<Product> currentList, String fileName) {
        File file = new File(MainActivity.this.getFilesDir(), "text");
        if (!file.exists()) {
            file.mkdir();
        }
        try {
            File gpxfile = new File(file, fileName);
            FileWriter writer = new FileWriter(gpxfile);
            for(Product p: currentList) {
                writer.append(p.toString()+"\r\n");
            }
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readData() {
        readDataFromFile(products, "sample");
        readDataFromFile(buyList, "buy");
    }

    private void readDataFromFile(ArrayList<Product> currentList, String fileName){
        String text;
//        currentList = new ArrayList<>();

        File fileEvents = new File(MainActivity.this.getFilesDir() + "/text/" + fileName);
        try{
            BufferedReader br = new BufferedReader(new FileReader(fileEvents));
            while((text = br.readLine()) != null) {
                String[] ss = text.split(",");
                Product product = new Product(ss[0].trim(), ss[1].trim(),
                        Double.parseDouble(ss[2].replace(";","").trim()));
                currentList.add(product);
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


    public void onBuyList(View view) {
        isProductList = !isProductList;
        changeList();
    }

    private void changeList() {
        if(isProductList){
            updateList(products);
            logo.setImageResource(R.drawable.shopping);
            doToast("Wyświetlasz zapasy");
        } else {
            updateList(buyList);
            logo.setImageResource(R.drawable.check);
            doToast("Wyświetlasz listę zakupów");
        }
        moveList = new ArrayList<>();
    }
}
