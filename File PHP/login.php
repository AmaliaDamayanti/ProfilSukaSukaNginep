<?php
require_once('koneksi.php');

if(isset($_POST['username']) && isset($_POST['password'])){
	//mendapatkan data
	$username = $_POST['username'];
	$pass = $_POST['password'];

	$sql = "SELECT username, email FROM akun WHERE username = '$username' AND password = '$pass'";
  	$res = mysqli_query($con,$sql);
        $result = array();
        if(mysqli_num_rows($res)>0){
			  	while($row = mysqli_fetch_array($res)){
			   		array_push($result, array('username'=>$row[0],'email'=>$row[1]));
			  	}
				  	echo json_encode(array("value"=>1,"result"=>$result));
				     mysqli_close($con);
        }else{
        $message = "Gagal Login! Cek Username/Password Anda.";
        echo json_encode(array("value"=>0,"result"=>$message));
        mysqli_close($con);
        }
}

?>
