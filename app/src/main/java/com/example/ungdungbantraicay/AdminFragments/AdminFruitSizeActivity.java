package com.example.ungdungbantraicay.AdminFragments;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ungdungbantraicay.AdminAdapter.AdminFruitSizeAdapter;
import com.example.ungdungbantraicay.DAO.FruitSizeDAO;
import com.example.ungdungbantraicay.Model.FruitSize;
import com.example.ungdungbantraicay.R;

import java.util.List;

public class AdminFruitSizeActivity extends AppCompatActivity {
    RecyclerView rvFruitSize;
    FruitSizeDAO sizeDAO;
    List<FruitSize> sizeList;
    int fruitId;
    String fruitName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_fruit_size);

        fruitId = getIntent().getIntExtra("fruitId", -1);
        fruitName = getIntent().getStringExtra("fruitName");

        Toolbar toolbar = findViewById(R.id.toolbarSize);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Size: " + fruitName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rvFruitSize = findViewById(R.id.rvFruitSize);
        sizeDAO = new FruitSizeDAO(this);

        findViewById(R.id.fabAddSize).setOnClickListener(v -> showDialogSize(null));

        loadData();
    }

    private void loadData() {
        sizeList = sizeDAO.getSizesByFruitIdAdmin(fruitId);
        // Ở đây bạn cần tạo thêm AdminFruitSizeAdapter nhé (tương tự AdminFruitAdapter)
        AdminFruitSizeAdapter adapter = new AdminFruitSizeAdapter(this, sizeList, new AdminFruitSizeAdapter.OnSizeAction() {
            @Override
            public void onEdit(FruitSize size) { showDialogSize(size); }
            @Override
            public void onDelete(FruitSize size) {
                sizeDAO.deleteSize(size.getId());
                loadData();
            }
        });
        rvFruitSize.setLayoutManager(new LinearLayoutManager(this));
        rvFruitSize.setAdapter(adapter);
    }

    private void showDialogSize(FruitSize size) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View v = getLayoutInflater().inflate(R.layout.dialog_add_size, null);

        EditText edtName = v.findViewById(R.id.edtSizeName);
        EditText edtPrice = v.findViewById(R.id.edtSizePrice);
        CheckBox chkStatus = v.findViewById(R.id.chkSizeStatus);

        if (size != null) {
            builder.setTitle("Sửa Size");
            edtName.setText(size.getSize());
            edtPrice.setText(String.valueOf(size.getPrice()));
            chkStatus.setChecked(size.getStatus() == 1);
        } else {
            builder.setTitle("Thêm Size mới");
        }

        builder.setView(v);
        builder.setPositiveButton("Lưu", (d, w) -> {
            String name = edtName.getText().toString();
            String priceStr = edtPrice.getText().toString();
            if (name.isEmpty() || priceStr.isEmpty()) return;

            int price = Integer.parseInt(priceStr);
            int status = chkStatus.isChecked() ? 1 : 0;

            if (size != null) {
                size.setSize(name);
                size.setPrice(price);
                size.setStatus(status);
                sizeDAO.updateSize(size);
            } else {
                sizeDAO.insertSize(new FruitSize(0, fruitId, name, price, status));
            }
            loadData();
        });
        builder.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}