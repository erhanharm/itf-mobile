<?php

error_reporting(1);
// Put your device token here (without spaces):
 $deviceToken = 'c5b6c37efbbb0d3e0d3d0e7c556de654982ffca428f86ea99b2ef307e6e62ada'; //iPad 2
 //$deviceToken = '43bbd17bbef2eff91c8fcb94237b0e84259ed6e2403ea4c54c0f8b4230dfb1a1';
//$deviceToken = '1dee11c9f8210dca3b8b0685841271017b6c4204ffd771f0dbcae9e454715eda'; //iPhone
//echo $deviceToken ;
// Put your private key's passphrase here:
$passphrase = 'vicnj12345-';

// Put your alert message here:
$message = 'Hello VAAAA';

////////////////////////////////////////////////////////////////////////////////

$ctx = stream_context_create();
stream_context_set_option($ctx, 'ssl', 'local_cert', 'pushchat.pem');
stream_context_set_option($ctx, 'ssl', 'passphrase', $passphrase);

// Open a connection to the APNS server
$fp = stream_socket_client(
	'ssl://gateway.sandbox.push.apple.com:2195', $err,
	$errstr, 60, STREAM_CLIENT_CONNECT|STREAM_CLIENT_PERSISTENT, $ctx);

if (!$fp)
	exit("Failed to connect: $err $errstr" . PHP_EOL);

echo 'Connected to APNS' . PHP_EOL;

// Create the payload body
$body['aps'] = array(
	'alert' => $message,
	'sound' => 'default'
	);

// Encode the payload as JSON
$payload = json_encode($body);
$msg = chr(0) . pack('n', 32) . pack('H*', $deviceToken) . pack('n', strlen($payload)) . $payload;
$result = fwrite($fp, $msg, strlen($msg));
if (!$result)
	echo 'Message not to ' . $deviceToken . 'delivered' . PHP_EOL;
else
	echo 'Message to ' . $deviceToken . ' successfully delivered' . PHP_EOL;
	


// Close the connection to the server
fclose($fp);

?>