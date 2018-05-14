package com.ad.sukasukanginep.apihelper;

import com.ad.sukasukanginep.Model.Value;
import com.ad.sukasukanginep.Model.ValueHost;
import com.ad.sukasukanginep.Model.ValueLogin;
import com.ad.sukasukanginep.Model.ValueProfil;
import com.ad.sukasukanginep.Model.ValueUploadGambar;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by USER on 11/25/2017.
 */

public interface BaseApiService {

    //Login
    @FormUrlEncoded
    @POST("login.php")
    Call<ValueLogin> loginRequest(@Field("username") String username,
                                  @Field("password") String password);

    //Register
    @FormUrlEncoded
    @POST("register.php")
    Call<Value> register(@Field("username") String username,
                         @Field("password") String password,
                         @Field("nama") String nama,
                         @Field("email") String email,
                         @Field("telepon") String telp,
                         @Field("rekening") String rek);

    //Tampil Detail Profil
    @FormUrlEncoded
    @POST("tampildetailprofil.php")
    Call<ValueProfil> detailProfil (@Field("username") String username);

    //Ubah Detail Profil
    @FormUrlEncoded
    @POST("ubahdetailprofil.php")
    Call<ValueProfil> ubahdetailProfil (@Field("usernameawal") String usernameAwal,
                                        @Field("username") String username,
                                        @Field("nama") String nama,
                                        @Field("email") String email,
                                        @Field("telepon") String telp,
                                        @Field("rekening") String rek);

    //Tambah Host
    @FormUrlEncoded
    @POST("tambahHost.php")
    Call<ValueHost> tambahHost (@Field("username") String username);

    //Cek Host
    @FormUrlEncoded
    @POST("cekHost.php")
    Call<ValueHost> cekHost (@Field("username") String username);

    //Upload Gambar
    @Multipart
    @POST("retrofit_client.php")
    Call<ValueUploadGambar> uploadFile(@Part MultipartBody.Part file, @Part("file") RequestBody name);

    //Simpan Daftar rumah
    @FormUrlEncoded
    @POST("simpanRumah.php")
    Call<Value> simpanRumah(@Field("namarumah") String namaRumah,
                            @Field("lat") Double lat,
                            @Field("lng") Double lng,
                            @Field("harga") int harga,
                            @Field("fasilitas") String fasilitas,
                            @Field("kamar") int kamar,
                            @Field("alamat") String alamat,
                            @Field("gambar") String nama_foto,
                            @Field("idhost") String idHost);
}
