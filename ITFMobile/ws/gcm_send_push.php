<?php

	//Sending Push Notification
   function send_push_notification($registatoin_ids, $message) {

		$GOOGLE_API_KEY = "AIzaSyBX45xiF9EKD6LZiR3-SUjJZGU83s4N8go";
   
        // Set POST variables
        $url = 'https://android.googleapis.com/gcm/send';

		$data2   = array('message' => $message);
		
        $fields = array(
            'registration_ids' => $registatoin_ids,
            'data' => $data2,
        );

        $headers = array(
            'Authorization: key=' . $GOOGLE_API_KEY,
            'Content-Type: application/json'
        );
		//print_r($headers);
        // Open connection
        $ch = curl_init();

        // Set the url, number of POST vars, POST data
        curl_setopt($ch, CURLOPT_URL, $url);

        curl_setopt($ch, CURLOPT_POST, true);
        curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);

        // Disabling SSL Certificate support temporarly
        curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);

        curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));
		
        // Execute post
        $result = curl_exec($ch);
        if(curl_errno($ch)){ echo 'Curl error: ' . curl_error($ch); }

        // Close connection
        curl_close($ch);
        //echo $result;
    }
?>