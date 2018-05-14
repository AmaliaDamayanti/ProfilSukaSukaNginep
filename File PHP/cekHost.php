<?php
require_once('koneksi.php');

if(isset($_POST['username'])){
	//mendapatkan data
	$username = $_POST['username'];
	$idhost = "HS-".$username;

	$sql = "SELECT id_host FROM host WHERE id_host = '$idhost'";
  	$res = mysqli_query($con,$sql);
        $result = array();
        if(mysqli_num_rows($res)>0){
			  	while($row = mysqli_fetch_array($res)){
			   		array_push($result, array('id_host'=>$row[0]));
			  	}
				  	echo json_encode(array("value"=>1,"result"=>$result));
				     mysqli_close($con);
        }else{
        $message = "idHost tidak ditemukan";
        echo json_encode(array("value"=>0,"result"=>$message));
        mysqli_close($con);
        }
}

?>
