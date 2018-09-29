<?php 
class db_functions{
	
	private $conn;
	
	function __construct(){
		require_once 'db_connect.php';
		$db = new db_connect();
		$this->conn= $db->connect();
	}
	
	public function storeUser($name,$email,$password){
		$uuid = uniqid('',true);
		
		$stmt=$this->conn->prepare("INSERT INTO users(unique_id,name,email,password,created_at)VALUES(?,?,?,?,NOW())");
		$stmt->bind_param("ssss",$uuid,$name,$email,$password);
		$result=$stmt->execute();
		$stmt->close();
		
		if ($result) {
			$stmt = $this->conn->prepare("SELECT * FROM users WHERE email = ?");
			$stmt->bind_param("s", $email);
			$stmt->execute();
			$user = $stmt->get_result()->fetch_assoc();
			$stmt->close();
			
			return $user;
		} else {
			return false;
		}
		
	}
	public function getUserByEmailAndPassword($email, $password) {
		
		$stmt = $this->conn->prepare("SELECT * FROM users WHERE email = ?");
		
		$stmt->bind_param("s", $email);
		
		if ($stmt->execute()) {
			$user = $stmt->get_result()->fetch_assoc();
			$stmt->close();
			

			// check for password equality
			if ($password == $user['password']) {
				// user authentication details are correct
				return $user;
			}
		} else {
			return NULL;
		}
	}
	
	public function doesUserExist($email){
		$stmt=$this->conn->prepare("SELECT email from users WHERE email=?");
		$stmt->bind_param("s",$email);
		$stmt->execute();
		$stmt->store_result();
		
		if($stmt->num_rows>0){
			$stmt->close();
			return true; //user exists
		}
		else{
			$stmt->close();
			return false;
		}
		
	}
	
	
	
	
}


?>