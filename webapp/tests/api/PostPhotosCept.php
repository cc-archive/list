<?php
$img = file_get_contents('tests/_data/small.jpg');
$img_base64 = base64_encode($img);

$I = new ApiTester($scenario);
$I->wantTo('upload a photo');
//TODO: test with log in
//$I->haveHttpHeader('Content-Type', 'application/x-www-form-urlencoded');
$I->sendPOST('photos/1/135', ['filedata' => $img_base64]);
$I->seeResponseCodeIs(200);
//TODO: check database
