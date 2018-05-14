<?php
require_once('koneksi.php');

if(isset($_POST['username'])){
	//mendapatkan data
	$username = $_POST['username'];

	$sql = "SELECT username, nama, email, telepon, rekening FROM akun WHERE username = '$username'";
  	$res = mysqli_query($con,$sql);
        $result = array();
        if(mysqli_num_rows($res)>0){
			  	while($row = mysqli_fetch_array($res)){
			   		array_push($result, array('username'=>$row[0],'nama'=>$row[1], 'email'=>$row[2], 'telepon'=>$row[3], 'rekening'=>$row[4]));
			  	}
				  	echo json_encode(array("value"=>1,"result"=>$result));
				     mysqli_close($con);
        }else{
        $message = "Gagal Tampil Detail Profil";
        echo json_encode(array("value"=>0,"result"=>$message));
        mysqli_close($con);
        }
}

?>
