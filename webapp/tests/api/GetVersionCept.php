<?php

include('version.php');

$I = new ApiTester($scenario);
$I->wantTo('fetch the version information for the web app');
$I->sendGET('version');
$I->seeResponseCodeIs(200);
$I->seeResponseIsJson();
$I->seeResponseContainsJson(
[
 "api_version"=>$api_version,
 "app_version"=>$app_version,
 "webapp_version"=>$webapp_version
]);