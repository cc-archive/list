<?php

$I = new ApiTester($scenario);

$I->wantTo('log in as an anonymous user');
$I->sendPOST('users/login');
$I->seeResponseCodeIs(200);
$I->seeResponseIsJson();
$I->seeResponseJsonMatchesJsonPath('$.skey');
$I->seeResponseJsonMatchesJsonPath('$.userid');
$I->seeResponseJsonMatchesJsonPath('$.email');
$guidResponse = $I->getRequestJSON();

$I->wantTo('log in as an anonymous user using a previous GUID');
$I->sendPOST('users/login', ['username'=>$guidResponse['email']]);
$I->seeResponseCodeIs(200);
$I->seeResponseIsJson();
$I->seeResponseContainsJSON(['email'=>$guidResponse['email']]);
$I->seeResponseContainsJSON(['userid'=>$guidResponse['userid']]);

$I->wantTo('fail to log in with an unregistered CCID');
$I->sendPOST('users/login', ['username'=>'@12345678']);
$I->seeResponseCodeIs(401);

$I->wantTo('fail to log in with an incorrect CCID password');
$I->sendPOST('users/login', ['username'=>'rob@creativecommons.org',
                             'password'=>'obviouslybadpassword']);
$I->seeResponseCodeIs(401);