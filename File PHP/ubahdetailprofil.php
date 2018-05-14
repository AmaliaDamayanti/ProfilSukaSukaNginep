<?php
require_once('koneksi.php');
if($_SERVER['REQUEST_METHOD']=='POST'){
	$response = array();
	//mendapatkan data
	$usernameAwal = $_POST['usernameawal'];
	$username = $_POST['username'];
	$nama = $_POST['nama'];
	$email = $_POST['email'];
	$telp = $_POST['telepon'];
	$rek = $_POST['rekening'];

	$sql = "UPDATE akun SET username = '$username', nama = '$nama', email = '$email', telepon = '$telp', rekening = '$rek' WHERE username = '$usernameAwal'";
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
