<?php
require_once('koneksi.php');
if($_SERVER['REQUEST_METHOD']=='POST'){
	$response = array();
	//mendapatkan data
	$file = $_POST['gambar'];
	$namaRumah = $_POST['namarumah'];
	$harga = $_POST['harga'];
	$fasilitas = $_POST['fasilitas'];
	$kamar = $_POST['kamar'];
	$alamat = $_POST['alamat'];
	$idHost = $_POST['idhost'];
	$lt = $_POST['lat'];
	$lng = $_POST['lng'];

	$sql = "INSERT INTO tempat (nama_tempat, lat, lng, harga, fasilitas, sisa_kamar, alamat, nama_foto, fk_id_host) VALUES('$namaRumah','$lt', '$lng', '$harga', '$fasilitas', '$kamar', '$alamat', '$file', '$idHost')";
			if(mysqli_query($con,$sql)){
				$response["value"] = 1;
				$response["message"] = "Sukses mendaftar";
				echo json_encode($response);
			}else{

	//tutup database
	mysqli_close($con);
}
}else{
	$response["value"] = 0;
	$response["message"] = "oops! Coba lagi!";
	echo json_encode($response);
}
?>
