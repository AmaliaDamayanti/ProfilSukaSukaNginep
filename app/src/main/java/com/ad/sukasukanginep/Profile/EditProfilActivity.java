package com.ad.sukasukanginep.Profile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ad.sukasukanginep.LogReg.SharedPrefManager;
import com.ad.sukasukanginep.MainNavigasiActivity;
import com.ad.sukasukanginep.Model.ResultLogin;
import com.ad.sukasukanginep.Model.ResultProfil;
import com.ad.sukasukanginep.Model.ValueHost;
import com.ad.sukasukanginep.Model.ValueLogin;
import com.ad.sukasukanginep.Model.ValueProfil;
import com.ad.sukasukanginep.R;
import com.ad.sukasukanginep.apihelper.BaseApiService;
import com.ad.sukasukanginep.apihelper.UtilsApi;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfilActivity extends AppCompatActivity {

    @BindView(R.id.edt_username) EditText edt_username;
    @BindView(R.id.edt_nama) EditText edt_nama;
    @BindView(R.id.edt_email) EditText edt_email;
    @BindView(R.id.edt_telepon) EditText edt_telepon;
    @BindView(R.id.edt_rekening) EditText edt_rekening;
    @BindView(R.id.masukan_IdHost) TextView masukanIdHost;

    ProgressDialog loading;
    SharedPrefManager sharedPrefManager;
    Context mContext;
    BaseApiService mApiService;
    private List<ResultProfil> results = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        ButterKnife.bind(this);

        sharedPrefManager = new SharedPrefManager(this);

        mContext = this;
        mApiService = UtilsApi.getAPIService(); // meng-init yang ada di package apihelper
        requestTampilProfil();
        cekIdHostTanpaCreate();

    }

    private void cekIdHostTanpaCreate(){
        String username = sharedPrefManager.getSPNama().toString();
        mApiService.cekHost(username).enqueue(new Callback<ValueHost>() {
            @Override
            public void onResponse(Call<ValueHost> call, Response<ValueHost> response) {
                String value = response.body().getValue();
                if (value.equals("1")) {
                    sharedPrefManager.saveSPStringIdHost(sharedPrefManager.SP_IDHOST, "HS-"+sharedPrefManager.getSPNama().toString() );
                    masukanIdHost.setText("HS-"+sharedPrefManager.getSPNama().toString());

                    Toast.makeText(EditProfilActivity.this, "Anda sudah terdaftar menjadi Host", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ValueHost> call, Throwable t) {
                Log.d("debug", "Cek id tidak ditemukan");
                masukanIdHost.setText("-");
            }
        });
    }

    private void requestTampilProfil() {
        mApiService.detailProfil(sharedPrefManager.getSPNama().toString())
                .enqueue(new Callback<ValueProfil>() {
                    @Override
                    public void onResponse(Call<ValueProfil> call, Response<ValueProfil> response) {
                        String value = response.body().getValue();
                        if (value.equals("1")) {
                            results = response.body().getResult();
                            Log.e("debug",  "// "+results.toString());

                            ResultProfil result = results.get(0);
                            String userName = result.getUsername();
                            String nama = result.getNama();
                            String eMail = result.getEmail();
                            String telepon = result.getTelepon();
                            String rekening = result.getRekening();


                            Log.e("debug", "Berhasil Tampil Detail");

                            edt_username.setText(userName);
                            edt_nama.setText(nama);
                            edt_email.setText(eMail);
                            edt_telepon.setText(telepon);
                            edt_rekening.setText(rekening);

                            Log.e("debug", "Sukses ->  "+response.body().getPesan() );
                        }else if(value.equals("0")){
                            Toast.makeText(mContext, response.body().getPesan(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ValueProfil> call, Throwable t) {
                        Toast.makeText(mContext, "OnFailure di Tampil Detail Profil", Toast.LENGTH_LONG).show();
                    }
                });
    }

    @OnClick(R.id.btnSimpanEditProfil) void ubah(){
        loading = ProgressDialog.show(mContext, null, "Harap Tunggu...", true, false);
        Log.i("debug", ">> "+sharedPrefManager.getSPNama().toString()+" "+edt_username.getText().toString());
        Log.i("debug", ">> "+" "+edt_nama.getText().toString()+" "+ edt_email.getText().toString());
        Log.i("debug", ">> "+ " "+  edt_telepon.getText().toString() + " "+  edt_rekening.getText().toString());
        mApiService.ubahdetailProfil(sharedPrefManager.getSPNama().toString(),
                                    edt_username.getText().toString(),
                                    edt_nama.getText().toString(),
                                    edt_email.getText().toString(),
                                    edt_telepon.getText().toString(),
                                    edt_rekening.getText().toString())
            .enqueue(new Callback<ValueProfil>() {
                @Override
                public void onResponse(Call<ValueProfil> call, Response<ValueProfil> response) {
                    String value = response.body().getValue();
                    String pesan = response.body().getPesan();
                    loading.dismiss();
                    if (value.equals("1")) {
                        Log.e("debug", "Ubah Profil : "+pesan);
                        Toast.makeText(EditProfilActivity.this, "Sukses Ubah", Toast.LENGTH_SHORT).show();

                        String userName = edt_username.getText().toString();
                        String eMail = edt_email.getText().toString();
                        sharedPrefManager.saveSPStringUsername(SharedPrefManager.SP_USERNAME, userName);
                        sharedPrefManager.saveSPStringEmail(SharedPrefManager.SP_EMAIL, eMail);

                        requestTampilProfil();
                    } else {
                        Log.e("debug", "Ubah Profil : "+pesan);
                        Toast.makeText(EditProfilActivity.this, "Gagal Ubah! Username sudah digunakan orang lain", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ValueProfil> call, Throwable t) {
                    Toast.makeText(mContext, "Maaf Username paten! Karena Anda terdaftar sebagai Host", Toast.LENGTH_LONG).show();
                    loading.dismiss();
                }
            });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Peringatan");
        alertDialogBuilder
                .setMessage("Apakah Anda yakin ingin keluar dari halaman ini?")
                .setCancelable(false)
                .setPositiveButton("Iya",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        clear();
                        finish();
                        startActivity(new Intent(EditProfilActivity.this,ProfilActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                    }
                })
                .setNegativeButton("Batal",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void clear() {
        edt_username.setText("");
        edt_nama.setText("");
        edt_email.setText("");
        edt_telepon.setText("");
        edt_rekening.setText("");
    }
}
