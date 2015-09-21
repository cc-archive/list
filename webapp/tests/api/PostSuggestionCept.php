<?php
$img = file_get_contents('tests/_data/small.jpg');
$img_base64 = base64_encode($img);

$I = new ApiTester($scenario);
$I->wantTo('upload a suggestion');
//FIXME: test with log in
$I->haveHttpHeader('Content-Type', 'application/x-www-form-urlencoded');
//FIXME: trailing slash kills this!
$I->sendPOST('suggestions/1',
             [
              'categoryid'=>'175',
              'description'=>'A new trinket image',
              'title'=>'New Trinket',
              'filedata' => $img_base64
              ]);
$I->seeResponseCodeIs(200);
//FIXME: check database
