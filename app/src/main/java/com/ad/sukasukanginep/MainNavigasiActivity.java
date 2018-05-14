package com.ad.sukasukanginep;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.ad.sukasukanginep.LogReg.LoginActivity;
import com.ad.sukasukanginep.LogReg.SharedPrefManager;
import com.ad.sukasukanginep.Profile.ProfilActivity;

public class MainNavigasiActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_navigasi);

        sharedPrefManager = new SharedPrefManager(this);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override

            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.action_home:
                        Toast.makeText(MainNavigasiActivity.this, "Home", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_add:
                        Toast.makeText(MainNavigasiActivity.this, "Add", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_profil:
                        if(sharedPrefManager.getSPSudahLogin()==true){
                                Toast.makeText(MainNavigasiActivity.this, "Profil", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(MainNavigasiActivity.this,ProfilActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                                finish();
                        }else {
                            Toast.makeText(MainNavigasiActivity.this, "Anda harus Login dahulu.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainNavigasiActivity.this,LoginActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                            finish();
                            }
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        showAlertDialog();
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setMessage("Tutup Aplikasi ini?")
                .setNegativeButton("Tidak",null)
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainNavigasiActivity.this.finish();

                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
