package fpoly.account.lab5api;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;


public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EditText edtTimKiem;
    private HttpRequest httpRequest;
    private ArrayList<Distributor> distributors;
    private DistributorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        edtTimKiem = findViewById(R.id.edtTimKiem);
        httpRequest = new HttpRequest();
        distributors = new ArrayList<>();
        adapter = new DistributorAdapter(distributors, new DistributorAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Distributor distributor) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Chỉnh sửa tên nhà phân phối");

                View dialogView = getLayoutInflater().inflate(R.layout.dialog_them_moi, null);
                final EditText editText = dialogView.findViewById(R.id.editText);
                editText.setText(distributor.getName());
                builder.setView(dialogView);

                builder.setPositiveButton("Lưu", (dialogInterface, i) -> {
                    String tenMoi = editText.getText().toString();
                    updateDistributor(distributor.getId(), tenMoi);
                });

                builder.setNegativeButton("Hủy", (dialogInterface, i) -> dialogInterface.dismiss());

                AlertDialog dialog = builder.create();
                dialog.show();
            }

            @Override
            public void onDeleteClick(Distributor distributor) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Xác nhận xóa")
                        .setMessage("Bạn có chắc chắn muốn xóa nhà phân phối này?")
                        .setPositiveButton("Xóa", (dialog, which) -> deleteDistributor(distributor.getId()))
                        .setNegativeButton("Hủy", null)
                        .show();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        getListDistributor();

        FloatingActionButton btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Thêm nhà phân phối");

            View dialogView = getLayoutInflater().inflate(R.layout.dialog_them_moi, null);
            final EditText editText = dialogView.findViewById(R.id.editText);
            builder.setView(dialogView);

            builder.setPositiveButton("Thêm", (dialogInterface, i) -> {
                String tenNhaPhanPhoi = editText.getText().toString();

                addDistributor(tenNhaPhanPhoi);
            });

            builder.setNegativeButton("Hủy", (dialogInterface, i) -> dialogInterface.dismiss());

            AlertDialog dialog = builder.create();
            dialog.show();
        });




        edtTimKiem.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (keyEvent != null && keyEvent.getKeyCode()
 == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN))
            {

                String key = edtTimKiem.getText().toString();
                searchDistributor(key);
                return true;
            }
            return false;
        });
    }

    private void getListDistributor() {
        httpRequest.callAPI().getListDistributor().enqueue(new Callback<Response<ArrayList<Distributor>>>() {
            @Override
            public void onResponse(Call<Response<ArrayList<Distributor>>> call, retrofit2.Response<Response<ArrayList<Distributor>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getStatus() == 200) {
                        distributors.clear();
                        distributors.addAll(response.body().getData());
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(MainActivity.this, response.body().getMessenger(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        Log.e("API Error", "Response unsuccessful: " + response.errorBody().string());
                    } catch (IOException e) {
                        Log.e("API Error", "Error reading error body: " + e.getMessage());
                    }
                    Toast.makeText(MainActivity.this, "Lỗi API", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Response<ArrayList<Distributor>>> call, Throwable t) {
                Log.e("API Error", "API call failed: " + t.getMessage(), t);
                Toast.makeText(MainActivity.this, "Lỗi kết nối API", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchDistributor(String key) {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("key", key);

        httpRequest.callAPI().searchDistributor(queryParams).enqueue(new Callback<Response<ArrayList<Distributor>>>() {
            @Override
            public void onResponse(Call<Response<ArrayList<Distributor>>> call, retrofit2.Response<Response<ArrayList<Distributor>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getStatus() == 200) {
                        distributors.clear();
                        distributors.addAll(response.body().getData());
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(MainActivity.this, response.body().getMessenger(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        Log.e("API Error", "Search Response unsuccessful: " + response.errorBody().string());
                    } catch (IOException e) {
                        Log.e("API Error", "Error reading error body: " + e.getMessage());
                    }
                    Toast.makeText(MainActivity.this, "Lỗi API khi tìm kiếm", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Response<ArrayList<Distributor>>> call, Throwable t) {
                Log.e("API Error", "Search API call failed: " + t.getMessage(), t);
                Toast.makeText(MainActivity.this, "Lỗi kết nối API khi tìm kiếm", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addDistributor(String name) {
        Distributor distributor = new Distributor();
        distributor.setName(name);

        httpRequest.callAPI().addDistributor(distributor).enqueue(new Callback<Response<String>>() {
            @Override
            public void onResponse(Call<Response<String>> call, retrofit2.Response<Response<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getStatus() == 200) {
                        String newDistributorId = response.body().getData();

                        Log.d("New Distributor ID", newDistributorId);

                        getListDistributor();

                        Toast.makeText(MainActivity.this, response.body().getMessenger(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, response.body().getMessenger(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        Log.e("API Error", "Add Response unsuccessful: " + response.errorBody().string());
                    } catch (IOException e) {
                        Log.e("API Error", "Error reading error body: " + e.getMessage());
                    }
                    Toast.makeText(MainActivity.this, "Lỗi API khi thêm", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Response<String>> call, Throwable t) {
                Log.e("API Error", "Add API call failed: " + t.getMessage(), t);
                Toast.makeText(MainActivity.this, "Lỗi kết nối API khi thêm", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void deleteDistributor(String id) {
        httpRequest.callAPI().deleteDistributorById(id).enqueue(new Callback<Response<Void>>() {
            @Override
            public void onResponse(Call<Response<Void>> call, retrofit2.Response<Response<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getStatus() == 200) {
                    getListDistributor();
                    Toast.makeText(MainActivity.this, response.body().getMessenger(), Toast.LENGTH_SHORT).show();
                } else if (response.body() != null) {
                    Toast.makeText(MainActivity.this, response.body().getMessenger(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Response<Void>> call, Throwable t) {
                Log.d("TAG", t.getMessage());
            }
        });
    }

    private void updateDistributor(String id, String name) {
        Distributor distributor = new Distributor();
        distributor.setName(name);

        httpRequest.callAPI().updateDistributorById(id, distributor).enqueue(new Callback<Response<Void>>() { // Sửa ở đây
            @Override
            public void onResponse(Call<Response<Void>> call, retrofit2.Response<Response<Void>> response) { // Sửa ở đây
                if (response.isSuccessful() && response.body() != null && response.body().getStatus() == 200) {
                    getListDistributor();
                    Toast.makeText(MainActivity.this, response.body().getMessenger(), Toast.LENGTH_SHORT).show();
                } else if (response.body() != null) {
                    Toast.makeText(MainActivity.this, response.body().getMessenger(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Response<Void>> call, Throwable t) {
                Log.d("TAG", t.getMessage());
            }
        });
    }
}