<?php 
$I = new ApiTester($scenario);
$I->wantTo('fetch the details for the given maker id');
$I->sendGET('makers/1');
$I->seeResponseCodeIs(200);
$I->seeResponseIsJson();
$I->seeResponseContainsJson(
[[
  "id"=>"1",
  "name"=>"Creative Commons",
  "uri"=>"http://creativecommons.org"
]]);