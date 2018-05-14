<?php
require_once('koneksi.php');
if($_SERVER['REQUEST_METHOD']=='POST'){
	$response = array();
	//mendapatkan data
	$username = $_POST['username'];
	$pass = $_POST['password'];
	$nama = $_POST['nama'];
	$email = $_POST['email'];
	$telp = $_POST['telepon'];
	$rek = $_POST['rekening'];

	$sql = "INSERT INTO akun (username, password, nama, email, telepon, rekening) VALUES('$username', '$pass', '$nama', '$email', '$telp', '$rek')";
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
