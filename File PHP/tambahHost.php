<?php
require_once('koneksi.php');
if($_SERVER['REQUEST_METHOD']=='POST'){
	$response = array();
	//mendapatkan data
	$username = $_POST['username'];
	$idhost = "HS-".$username;

	$sql = "INSERT INTO host (id_host, fk_username) VALUES('$idhost','$username')";
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
