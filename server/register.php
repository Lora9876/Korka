<?php
error_reporting(0);
/*
 Used in: RegisterActivity
 */
 
// array for JSON response
$response = array();
// include db connect class
require_once __DIR__ . '/db_connect.php';

// connecting to db
$db = new DB_CONNECT();

// check for required fields
if (isset($_POST['imgString']) && isset($_POST['name']) &&isset($_POST['surname']) &&isset($_POST['username']) && isset($_POST['password']) && isset($_POST['email'])) {
    
    $username = $_POST['username'];
    $password = $_POST['password'];
	$name = $_POST['name'];
	$surname = $_POST['surname'];
    $email = $_POST['email'];
	$imgString = $_POST['imgString'];
	
	$result = mysql_query("SELECT * FROM usertb WHERE username='$username'" ) or die(mysql_error());
	
	// check for empty result
	if (mysql_num_rows($result) == 0) {
	
		// mysql inserting a new row
		$result = mysql_query("INSERT INTO usertb (username, password, email, name, surname, score, status, imgString) VALUES('$username', '$password', '$email','$name','$surname', '0', '0', '$imgString')");

		// check if row inserted or not
		if ($result) {
			// successfully inserted into database
			$response["success"] = 1;
			$response["message"] = "User successfully created.";

			// echoing JSON response
			echo json_encode($response);
		} else {
			// failed to insert row
			$response["success"] = 0;
			$response["message"] = "Oops! An error occurred.";
			
			// echoing JSON response
			echo json_encode($response);
		}
	} else {
		// failed to insert row
		$response["success"] = 0;
		$response["message"] = "This username is already taken.";
		
		// echoing JSON response
		echo json_encode($response);
	}
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";

    // echoing JSON response
    echo json_encode($response);
}
?>