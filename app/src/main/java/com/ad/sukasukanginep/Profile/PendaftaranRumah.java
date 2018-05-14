package com.ad.sukasukanginep.Profile;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.ad.sukasukanginep.LogReg.SharedPrefManager;
import com.ad.sukasukanginep.Model.Value;
import com.ad.sukasukanginep.Model.ValueUploadGambar;
import com.ad.sukasukanginep.R;
import com.ad.sukasukanginep.apihelper.BaseApiService;
import com.ad.sukasukanginep.apihelper.RetrofitClient;
import com.ad.sukasukanginep.apihelper.UtilsApi;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PendaftaranRumah extends AppCompatActivity {

    @BindView(R.id.gambarUpload) ImageView gambar;
    @BindView(R.id.edt_nama) EditText namaPenginapan;
    @BindView(R.id.edt_harga) EditText harga;
    @BindView(R.id.edt_fasilitas) EditText fasilitas;
    @BindView(R.id.edt_jumlahKamar) EditText kamar;
    @BindView(R.id.edt_alamat) EditText alamat;

    String mediaPath;
    String[] mediaColumns = {MediaStore.Video.Media._ID};
    BaseApiService mApiService;
    SharedPrefManager sharedPrefManager;

    Boolean kosong;
    Double lat, lng;
    String namaGambar, namaTempat, almt, fasil, idHost;
    int hargaTempat, jmlhKamar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_rumah_host);

        ButterKnife.bind(this);
        mApiService = UtilsApi.getAPIService(); // meng-init yang ada di package apihelper
        sharedPrefManager = new SharedPrefManager(this);

        currentLoc();
    }

    @OnClick(R.id.btn_add)
    void takeGambar() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, 0);

    }

    @OnClick(R.id.btn_simpanRumah)
    void simpan() {
        initComponents();
        uploadFile();

        namaTempat = namaPenginapan.getText().toString();
        hargaTempat = Integer.parseInt(harga.getText().toString());
        fasil = fasilitas.getText().toString();
        jmlhKamar = Integer.parseInt(kamar.getText().toString());
        almt = alamat.getText().toString();
        idHost = sharedPrefManager.getSpIdhost();

        Log.i("INFO", namaTempat+" "+lat+" "+lng+" "+hargaTempat+" "+fasil+" "+jmlhKamar+" "+almt+" "+namaGambar+" "+idHost);

        mApiService.simpanRumah(namaTempat, lat, lng, hargaTempat, fasil, jmlhKamar, almt, namaGambar, idHost)
                .enqueue(new Callback<Value>() {
                    @Override
                    public void onResponse(Call<Value> call, Response<Value> response) {
                        String value = response.body().getValue();
                        String pesan = response.body().getMessage();
                        if (value.equals("1")) {
                            Log.e("debug", "Register : "+pesan);
                            Toast.makeText(PendaftaranRumah.this, "Sukses Sewakan Kamar/Rumahmu", Toast.LENGTH_LONG).show();
                            clear();
                            finish();
                            Intent intent = new Intent(PendaftaranRumah.this, ProfilActivity.class);
                            startActivity(intent);
                        } else {
                            Log.e("debug", "Register : "+pesan);
                            Toast.makeText(PendaftaranRumah.this, "Gagal Menyewakan!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Value> call, Throwable t) {
                        Toast.makeText(PendaftaranRumah.this, "OnFailure : Gagal Menyewakan!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == 0 && resultCode == RESULT_OK && null != data) {

                // Get the Image from data
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                assert cursor != null;
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                mediaPath = cursor.getString(columnIndex);
                // Set the Image in ImageView for Previewing the Media
                gambar.setImageBitmap(BitmapFactory.decodeFile(mediaPath));
                cursor.close();

            } else {
                Toast.makeText(this, "You haven't picked Image/Video", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
        }
    }

    // Uploading Image/Video
    private void uploadFile() {

        // Map is used to multipart the file using okhttp3.RequestBody
        File file = new File(mediaPath);

        // Parsing any Media type file
        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
        RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());

        //Get Nama File
        namaGambar = file.getName().toString();

        mApiService.uploadFile(fileToUpload, filename).enqueue(new Callback<ValueUploadGambar>() {
            @Override
            public void onResponse(Call<ValueUploadGambar> call, Response<ValueUploadGambar> response) {
                ValueUploadGambar serverResponse = response.body();
                if (serverResponse != null) {
                    if (serverResponse.getSuccess()) {
                        Toast.makeText(getApplicationContext(), serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    assert serverResponse != null;
                    Log.v("Response", serverResponse.toString());
                }
            }

            @Override
            public void onFailure(Call<ValueUploadGambar> call, Throwable t) {

            }
        });
    }

    private void currentLoc() {
        FusedLocationProviderClient mFusedLocation = LocationServices.getFusedLocationProviderClient(PendaftaranRumah.this);
        checkLocationPermission();
        mFusedLocation.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
            if (location != null) {
                // Do it all with location
                Log.d("My Current location", "Lat : " + location.getLatitude() + " Long : " + location.getLongitude());
                lat = location.getLatitude();
                lng = location.getLongitude();
            }
            }
        });
    }

    public boolean checkLocationPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 99);
            }else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 99);
            }
            return  false;
        }else{
            return true;
        }
    }

    private void initComponents() {
        if(namaPenginapan.getText().toString().length()==0
            | harga.getText().toString().length()==0
            | fasilitas.getText().toString().length()==0
            | kamar.getText().toString().length()==0
            | alamat.getText().toString().length()==0){
                if(namaPenginapan.getText().toString().length()==0){
                    kosong = false;
                    namaPenginapan.setError("Kolom ini WAJIB diisi");
                }
                if(harga.getText().toString().length()==0){
                    kosong = false;
                    harga.setError("Kolom ini Wajib diisi");
                }
                if(fasilitas.getText().toString().length()==0){
                    kosong = false;
                    fasilitas.setError("Kolom ini Wajib diisi");
                }
                if(kamar.getText().toString().length()==0){
                    kosong = false;
                    kamar.setError("Kolom ini Wajib diisi");
                }
                if(alamat.getText().toString().length()==0){
                    kosong = false;
                    alamat.setError("Kolom ini Wajib diisi");
                }
        }
    }

    private void clear(){
        namaPenginapan.setText("");
        harga.setText("");
        fasilitas.setText("");
        kamar.setText("");
        alamat.setText("");
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
                        startActivity(new Intent(PendaftaranRumah.this,ProfilActivity.class)
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

}


