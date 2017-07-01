<?php
error_reporting(0);
/*
Used in: LoginActivity
 */

// array for JSON response
$response = array();

// include db connect class
require_once __DIR__ . '/db_connect.php';

// connecting to db
$db = new DB_CONNECT();
if(isset($_POST["username"]) && isset($_POST["password"]) && isset($_POST["longitude"]) && isset($_POST["latitude"]))
{
	$username1 = $_POST["username"];
    $password1 = $_POST["password"];
	$latitude = $_POST['latitude'];
	$longitude = $_POST['longitude'];
}

$result = mysql_query("SELECT * FROM usertb WHERE username='$username1' AND password='$password1'" ) or die(mysql_error());

// check for empty result
if (mysql_num_rows($result) > 0) {
    // looping through all results
    while ($row = mysql_fetch_array($result)) {
		
		$userId = intval($row["id"]);
		$result  = mysql_query("UPDATE usertb SET status='1', latitude=$latitude, longitude=$longitude WHERE id=$userId") or die(mysql_error());
		// check for empty result
		if ($result) {		
			// success
			$response["success"] = 1;
			$response["message"] = "User connected";
			$response["userid"] = $userId;
			// echoing JSON response
			echo json_encode($response);
		} else {
			// no users found
			$response["success"] = 0;
			$response["message"] = "Error during update of status";
			echo json_encode($response);
		}
    }
} else {
    // no friends found
    $response["success"] = 0;
    $response["message"] = "No such user found";

    // echo no friends JSON
    echo json_encode($response);
}
?>
