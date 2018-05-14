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
import android.widget.TextView;
import android.widget.Toast;

import com.ad.sukasukanginep.MainNavigasiActivity;
import com.ad.sukasukanginep.Model.ResultLogin;
import com.ad.sukasukanginep.Model.ValueLogin;
import com.ad.sukasukanginep.R;
import com.ad.sukasukanginep.apihelper.BaseApiService;
import com.ad.sukasukanginep.apihelper.UtilsApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText username, password;
    private Button btnLogin;
    private TextView txtRegister;
    ProgressDialog loading;

    boolean kosong;
    Context mContext;
    BaseApiService mApiService;
    private List<ResultLogin> results = new ArrayList<>();

    SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = (EditText) findViewById(R.id.User);
        password = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        txtRegister = (TextView) findViewById(R.id.txt_register);

        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        sharedPrefManager = new SharedPrefManager(this);
        //mengecek apakah kita sudah login apa belum. Jika sudah maka halaman login ini dilewati
        if (sharedPrefManager.getSPSudahLogin()){
            startActivity(new Intent(LoginActivity.this, MainNavigasiActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
            finish();
        }

        mContext = this;
        mApiService = UtilsApi.getAPIService(); // meng-init yang ada di package apihelper
        initComponents();
    }

    private void initComponents() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
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
                    requestLogin();
                }
            }
        });
    }

    public void requestLogin(){
        mApiService.loginRequest(username.getText().toString(), password.getText().toString())
                .enqueue(new Callback<ValueLogin>() {
                    @Override
                    public void onResponse(Call<ValueLogin> call, Response<ValueLogin> response) {
                        String value = response.body().getValue();
                        if (value.equals("1")) {
                            loading.dismiss();
                            results = response.body().getResult();
                            Log.e("debug",  "// "+results.toString());

                            ResultLogin result = results.get(0);
                            String username = result.getUsername();
                            String email = result.getEmail();
                            Log.e("debug", "Berhasil Login");

                            sharedPrefManager.saveSPStringUsername(SharedPrefManager.SP_USERNAME, username);
                            sharedPrefManager.saveSPStringEmail(SharedPrefManager.SP_EMAIL, email);
                            // Shared Pref ini berfungsi untuk menjadi trigger session login
                            sharedPrefManager.saveSPBoolean(SharedPrefManager.SP_SUDAH_LOGIN, true);

                            startActivity(new Intent(mContext,MainNavigasiActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                            finish();
                            Log.e("debug", "Sukses ->  "+response.body().getPesan() );
                        }else if(value.equals("0")){
                            loading.dismiss();
                            Toast.makeText(mContext, response.body().getPesan(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ValueLogin> call, Throwable t) {
                        loading.dismiss();
                        Toast.makeText(mContext, "Gagal Login! Cek Inputan Anda", Toast.LENGTH_LONG).show();
                    }
                });
    }
    @Override
    public void onBackPressed() {
        username.setText("");
        password.setText("");
        startActivity(new Intent(mContext,MainNavigasiActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
        finish();
    }

}
