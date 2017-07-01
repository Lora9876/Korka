<?php
error_reporting(0);
/*
 Used in: MainActivity when entering Map, Following code will list photos of this users friends
 */

// array for JSON response
$response = array();

// include db connect class
require_once __DIR__ . '/db_connect.php';

// connecting to db
$db = new DB_CONNECT();

$idk = intval($_POST["id"]);
// get all friends from friends table
$result = mysql_query("SELECT friendid FROM friendstb WHERE userid=$idk") or die(mysql_error());

// check for empty result
if (mysql_num_rows($result) > 0) {
    // looping through all results
    // friends node
    $response["friends"] = array();
    
    while ($row = mysql_fetch_array($result)) {
        // temp user array
		$user = array();
		$id =  $row["friendid"];
		$resultI = mysql_query("SELECT imgString FROM usertb WHERE id=$id") or die(mysql_error());

		// check for empty result
		if (mysql_num_rows($resultI) > 0) {
			
			$row = mysql_fetch_array($resultI);
			$user["imgString"] = $row["imgString"];
			$user["id"]= $id;
		}
        
        // push single user into final response array
        array_push($response["friends"], $user);
    }
    // success
    $response["success"] = 1;

    // echoing JSON response
    echo json_encode($response);
} else {
    // no friends found
    $response["success"] = 0;
    $response["message"] = "No friends found";

    // echo no friends JSON
    echo json_encode($response);
}
?>
