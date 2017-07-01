<?php
error_reporting(0);
/*
 Used in: MainActivity, Following code will set users status to 0 when app is exited
 */

// array for JSON response
$response = array();

// include db connect class
require_once __DIR__ . '/db_connect.php';

// connecting to db
$db = new DB_CONNECT();
if(isset($_POST["id"]))
{
	$id = $_POST["id"];
}

$result  = mysql_query("UPDATE usertb SET status='0'WHERE id=$id") or die(mysql_error());
	// check for empty result
	if ($result) {		
		// success
		$response["success"] = 1;
		$response["message"] = "User succesfully exited app";
		// echoing JSON response
		echo json_encode($response);
	} else {
		// no users found
		$response["success"] = 0;
		$response["message"] = "Error during update of status";
		echo json_encode($response);
	}
?>
