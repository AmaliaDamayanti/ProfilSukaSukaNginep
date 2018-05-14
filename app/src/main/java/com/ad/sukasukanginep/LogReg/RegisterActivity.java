package com.ad.sukasukanginep.LogReg;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ad.sukasukanginep.Model.Value;
import com.ad.sukasukanginep.R;
import com.ad.sukasukanginep.apihelper.BaseApiService;
import com.ad.sukasukanginep.apihelper.UtilsApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText username, password;
    private Button btnRegister;
    ProgressDialog loading;

    boolean kosong;
    Context mContext;
    BaseApiService mApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = (EditText) findViewById(R.id.regUser);
        password = (EditText) findViewById(R.id.regPassword);
        btnRegister = (Button) findViewById(R.id.btnReg);

        mContext = this;
        mApiService = UtilsApi.getAPIService(); // meng-init yang ada di package apihelper
        initComponents();
    }

    private void initComponents() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(username.getText().toString().length()==0 | password.getText().toString().length()==0){
                    if(username.getText().toString().length()==0){
                        kosong = false;
                        username.setError("Kolom ini WAJIB diisi");
                    }
                    if(password.getText().toString().length()==0){
                        kosong = false;
                        password.setError("Kolom ini Wajib diisi");
                    }
                }else{
                    kosong=true;
                    loading = ProgressDialog.show(mContext, null, "Harap Tunggu...", true, false);
                    requestRegister();
                }
            }
        });
    }

    private void requestRegister() {

        String user = username.getText().toString();
        String pass = password.getText().toString();
        String nama = "-";
        String email = "-";
        String telp = "-";
        String rek = "-";

        mApiService.register(user, pass, nama, email, telp, rek)
                .enqueue(new Callback<Value>() {
                    @Override
                    public void onResponse(Call<Value> call, Response<Value> response) {
                        String value = response.body().getValue();
                        String pesan = response.body().getMessage();
                        loading.dismiss();
                        if (value.equals("1")) {
                            Log.e("debug", "Register : "+pesan);
                            Toast.makeText(RegisterActivity.this, "Sukses Mendaftar", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                        } else {
                            Log.e("debug", "Register : "+pesan);
                            Toast.makeText(RegisterActivity.this, "Gagal Mendaftar! Username sudah ada", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Value> call, Throwable t) {
                        loading.dismiss();
                        Toast.makeText(RegisterActivity.this, "Gagal Mendaftar! Username sudah ada", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        username.setText("");
        password.setText("");
        startActivity(new Intent(mContext,LoginActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
        finish();
    }
}
