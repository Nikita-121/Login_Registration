<?php 

require_once 'include/db_functions.php';
$db=new db_functions();

//	json response
	$response=array("error"=>FALSE);

	if($_POST['name']!=null && $_POST['email']!=null  && $_POST['password']!=null){
		$name=$_POST['name'];
		$email=$_POST['email'];
		$password=$_POST['password'];
		
		if($db->doesUserExist($email)){
			$response["error"]=TRUE;
			$response["error_msg"]="User already exists with ".$email;
			echo json_encode($response);
		}
		else{
			//create new user
			$user=$db->storeUser($name,$email,$password);
			if($user){
			//user stored successfully
			$response["error"]=FALSE;
			$response["uid"]=$user["unique_id"];
			$response["user"]["name"]=$user["name"];
			$response["user"]["email"]=$user["email"];
			$response["user"]["password"]=$user["password"];
			$response["user"]["created_at"]=$user["created_at"];
			echo json_encode($response);			
			}
			else{
				//failed to store user
			$response["error"]=TRUE;
			$response["error_msg"]="Unknown error occured in registration. Please try again later.";
			echo json_encode($response);
			}
		}
	}
	else{
		$response["error"]=TRUE;
		$response["error_msg"]="Required fields missing";
		echo json_encode($response);
	}

?>