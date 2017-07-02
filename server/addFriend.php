<?php
error_reporting(0);
/*
 Used in: BluetoothChatFragment, Following code will record users friendship
 */
 
// array for JSON response
$response = array();

// check for required fields
if (isset($_POST['myId']) && isset($_POST['friendId'])) {
    $friendId = $_POST['friendId'];
	$myId = $_POST['myId'];
	
    // include db connect class
    require_once __DIR__ . '/db_connect.php';

    // connecting to db
    $db = new DB_CONNECT();
$result1 = mysql_query("SELECT id FROM friendstb WHERE userid='$myId' AND friendid='$friendId'" ) or die(mysql_error());

// check for empty result
if (mysql_num_rows($result1) == 0) {
    // looping through all results
    
		// mysql inserting a new row
		$result = mysql_query("INSERT INTO friendstb (userid, friendid) VALUES('$myId','$friendId')");
		//$result1 = mysql_query("INSERT INTO friendstb (userid, friendid) VALUES('$friendId','$myId')");

		// check if row inserted or not
		if ($result && $result1) {
			// successfully inserted into database
			$response["success"] = 1;
			$response["message"] = "Friendship successfully added.";

			// echoing JSON response
			echo json_encode($response);
		} else {
			// failed to insert row
			$response["success"] = 0;
			$response["message"] = "Oops! An error occurred.";
			
			// echoing JSON response
			echo json_encode($response);
		}
	}
	else {
			// failed to insert row
			$response["success"] = 0;
			$response["message"] = "Already friends.";
			
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