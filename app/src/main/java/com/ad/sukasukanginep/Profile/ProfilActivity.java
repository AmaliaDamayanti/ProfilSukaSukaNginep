package com.ad.sukasukanginep.Profile;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ad.sukasukanginep.LogReg.SharedPrefManager;
import com.ad.sukasukanginep.MainNavigasiActivity;
import com.ad.sukasukanginep.Model.ValueHost;
import com.ad.sukasukanginep.R;
import com.ad.sukasukanginep.apihelper.BaseApiService;
import com.ad.sukasukanginep.apihelper.UtilsApi;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfilActivity extends AppCompatActivity {

    @BindView(R.id.txtProfUser) TextView txtProfUser;
    @BindView(R.id.txtProfEmail) TextView txtProfEmail;
    @BindView(R.id.btnUbahProfil) TextView btnUbahProfil;
    @BindView(R.id.btnDaftarHost) TextView btnDaftarHost;
    @BindView(R.id.btnDaftarRumah) TextView btnDaftarRumah;
    @BindView(R.id.btnLogout) TextView btnLogout;


    ProgressDialog loading;
    SharedPrefManager sharedPrefManager;
    BaseApiService mApiService;
    Boolean cek, cekSetelahSuksesDaftarHost;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        loading =   new ProgressDialog(this);

        sharedPrefManager = new SharedPrefManager(this);
        mApiService = UtilsApi.getAPIService(); // meng-init yang ada di package apihelper
        Log.i("ID HOST1", sharedPrefManager.getSpIdhost().toString());
        cekIdHostTanpaCreate();


        if(sharedPrefManager.getSPNama()!=null||sharedPrefManager.getSPEmail()!=null){
            txtProfUser.setText(sharedPrefManager.getSPNama());
            txtProfEmail.setText(sharedPrefManager.getSPEmail());

            btnUbahProfil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ProfilActivity.this,EditProfilActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                    finish();
                }
            });

            btnDaftarHost.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     cek();
                     cekIdHost();
                 }
             });


            btnDaftarRumah.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("ID HOST2", sharedPrefManager.getSpIdhost().toString());
                    cek();
                    if(sharedPrefManager.getSpIdhost().toString()!="spIdHost" | cek.equals("true")){
                        Toast.makeText(ProfilActivity.this, "ID host ditemukan!! ", Toast.LENGTH_LONG).show();
                        sharedPrefManager.saveSPStringIdHost(sharedPrefManager.SP_IDHOST, "HS-"+sharedPrefManager.getSPNama().toString() );
                        startActivity(new Intent(ProfilActivity.this,PendaftaranRumah.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                        finish();

                    }else{
                        Toast.makeText(ProfilActivity.this, "Maaf, Anda harus daftar Host terlebih dahulu! ", Toast.LENGTH_LONG).show();
                        sharedPrefManager.saveSPStringIdHost(sharedPrefManager.SP_IDHOST, "spIdHost" );
                    }
            }});

            btnLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog();
                }
            });
        }else{
            Toast.makeText(ProfilActivity.this, "Gagal Id posisi di Tampil Profil", Toast.LENGTH_SHORT).show();
        }
    }

    private Boolean cek(){
        if(sharedPrefManager.getSpIdhost().toString()!="HS-"+sharedPrefManager.getSPNama().toString()){
            cek = false;
            Log.i("CEKKKKKKK", sharedPrefManager.getSpIdhost().toString()+" "+cek+"--"+"HS-"+sharedPrefManager.getSPNama().toString());
            return  cek;
        }else{
            cek = true;
            Log.i("CEKKKKKKK", sharedPrefManager.getSpIdhost().toString()+" "+cek);
            return  cek;
        }

    }


    private void showDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        // set title dialog
        alertDialogBuilder.setTitle("Apakah Anda yakin untuk keluar dari aplikasi ini?");

        // set pesan dari dialog
        alertDialogBuilder
                .setMessage("Klik Ya untuk keluar!")
                .setIcon(R.mipmap.ic_launcher)
                .setNegativeButton("Tidak",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // jika tombol ini diklik, akan menutup dialog
                        // dan tidak terjadi apa2
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Ya",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // jika tombol diklik, maka akan menutup activity ini
                        sharedPrefManager.saveSPBoolean(SharedPrefManager.SP_SUDAH_LOGIN, false);
                        finish();
                    }
                });


        // membuat alert dialog dari builder
        AlertDialog alertDialog = alertDialogBuilder.create();

        // menampilkan alert dialog
        alertDialog.show();
    }

    private void cekIdHost(){
        if(sharedPrefManager.getSpIdhost().toString()!="spIdHost" | cek.equals("true")){
            Toast.makeText(ProfilActivity.this, "Anda sudah terdaftar menjadi Host " +sharedPrefManager.getSpIdhost().toString()+cek, Toast.LENGTH_LONG).show();
            cek = true;

        }else{
            Log.d("CEK ID HOST", "Cek id tidak ditemukan "+sharedPrefManager.getSpIdhost().toString());
            AlertDialog.Builder builder = new AlertDialog.Builder(ProfilActivity.this)
                    .setMessage("Daftar Menjadi Host?")
                    .setNegativeButton("Tidak",null)
                    .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            tambahIdHost();
                            cek = true;
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();

        }
    }

    private void tambahIdHost(){
        loading = ProgressDialog.show(ProfilActivity.this, null, "Harap Tunggu...", true, false);
        mApiService.tambahHost(sharedPrefManager.getSPNama().toString()).enqueue(new Callback<ValueHost>() {
            @Override
            public void onResponse(Call<ValueHost> call, Response<ValueHost> response) {
                String value = response.body().getValue();
                String pesan = response.body().getMessage();
                loading.dismiss();
                if (value.equals("1")) {
                    Log.e("debug", "Tambah ID berhasil");

                    sharedPrefManager.saveSPStringIdHost(sharedPrefManager.SP_IDHOST, "HS-"+sharedPrefManager.getSPNama().toString() );
                    Toast.makeText(ProfilActivity.this, "Sukses Mendaftar", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("debug", "Tidak Berhasil : "+pesan);
                    sharedPrefManager.saveSPStringIdHost(sharedPrefManager.SP_IDHOST, "spIdHost" );
                    Toast.makeText(ProfilActivity.this, "Gagal Mendaftar!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ValueHost> call, Throwable t) {
                Toast.makeText(ProfilActivity.this, "OnFailure gagal tambah ID", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cekIdHostTanpaCreate(){
        String username = sharedPrefManager.getSPNama().toString();
        mApiService.cekHost(username).enqueue(new Callback<ValueHost>() {
            @Override
            public void onResponse(Call<ValueHost> call, Response<ValueHost> response) {
                String value = response.body().getValue();
                if (value.equals("1")) {
                    sharedPrefManager.saveSPStringIdHost(sharedPrefManager.SP_IDHOST, "HS-"+sharedPrefManager.getSPNama().toString() );

                    Log.i("CEK TNAPA CREATE", "Anda sudah terdaftar menjadi Host");
                }
            }

            @Override
            public void onFailure(Call<ValueHost> call, Throwable t) {
                Log.d("debug", "Cek id tidak ditemukan");
                sharedPrefManager.saveSPStringIdHost(SharedPrefManager.SP_IDHOST, "spIdHost");
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ProfilActivity.this,MainNavigasiActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
        finish();
    }
}
