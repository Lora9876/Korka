<?php
error_reporting(0);
/*
 Used in: MapActivity, Following code will update users location and get refreshed locations of other users
 */

// array for JSON response
$response = array();

// include db connect class
require_once __DIR__ . '/db_connect.php';

// connecting to db
$db = new DB_CONNECT();

//get user's id, latitude and longitude
if(isset($_POST["id"]) && isset($_POST["longitude"]) && isset($_POST["latitude"]))
{
	$id = $_POST["id"];
	$latitude = $_POST['latitude'];
	$longitude = $_POST['longitude'];
}
mysql_query("UPDATE usertb SET latitude=$latitude, longitude=$longitude WHERE id=$id") or die(mysql_error());

// get all users from users table
$result = mysql_query("SELECT id, latitude, longitude, status FROM usertb") or die(mysql_error());
// check for empty result
if (mysql_num_rows($result) > 0) {
    // looping through all results
    // users node
    $response["users"] = array();
	
    while ($row = mysql_fetch_array($result)) {
        // temp user array
        $user = array();

		$user["id"] = $row["id"];
        $user["latitude"] = $row["latitude"];
        $user["longitude"] = $row["longitude"];
		
		if($row["status"]==1 && $row["id"]!=$id)
		{
			// push single user into final response array 
			array_push($response["users"], $user);
		}
    }
		
    // success
    $response["success"] = 1;

    // echoing JSON response
    echo json_encode($response);
} else {
    // no users found
    $response["success"] = 0;
    $response["message"] = "No users found";

    // echo no users JSON
    echo json_encode($response);
}
?>
