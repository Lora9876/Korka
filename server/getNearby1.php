<?php
error_reporting(0);
/*
 Used in: NotificatonIntentService, Following code will check for new users and questions near this user
 */

// array for JSON response
$response = array();

// include db connect class
require_once __DIR__ . '/db_connect.php';

// connecting to db
$db = new DB_CONNECT();
$neabyUsers = false;
$neabyQuestions = false;

//get user's id, latitude and longitude
if(isset($_POST["id"]) && isset($_POST["longitude"]) && isset($_POST["latitude"]))
{
	$id = $_POST["id"];
	$latitude = $_POST['latitude'];
	$longitude = $_POST['longitude'];
}

// get all users from users table
$result = mysql_query("SELECT * FROM usertb") or die(mysql_error());
$resultQ = mysql_query("SELECT * FROM questionstb") or die(mysql_error());

// check for empty result
if (mysql_num_rows($result) > 0 ) {
    // looping through all results
    while ($row = mysql_fetch_array($result)) {

		//is this user nerby?
		$distance = sqrt(pow( $row["latitude"] - $latitude,2) + pow( $row["longitude"] - $longitude,2));
		if($distance < 0.1 && $row["status"]==1 && $row["id"]!=$id)
		{
			$neabyUsers=true;
		}
    }
}

// check for empty result
if (mysql_num_rows($resultQ) > 0 ) {
	while ($row = mysql_fetch_array($resultQ)) {

		//is this question nerby?
		$distance = sqrt(pow( $row["latitude"] - $latitude,2) + pow( $row["longitude"] - $longitude,2));
		if($distance < 0.1)
		{
			$neabyQuestions=true;
		}
    }
}
	
if($neabyUsers==true && $neabyQuestions==true)
{
    $response["message"] = "There are questions and other users nearby. Check it out!";
    echo json_encode($response);
} else if($neabyUsers==false && $neabyQuestions==true)
{
    $response["message"] = "There are questions nearby. Check it out!";
    echo json_encode($response);
}else if($neabyUsers==true && $neabyQuestions==false)
{
    $response["message"] = "There are other users nearby. Check it out!";
    echo json_encode($response);
}else if($neabyUsers==false && $neabyQuestions==false)
{
    $response["message"] = "";
    echo json_encode($response);
}

?>
