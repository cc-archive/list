<?php 
$I = new ApiTester($scenario);
$I->wantTo('fetch the details for the given item id');
$I->sendGET('items/1');
$I->seeResponseCodeIs(200);
$I->seeResponseIsJson();
$I->seeResponseContainsJson(
[[
  "id"=>"1",
  "makerid"=>"1",
  "title"=>"Matt Lee",
  "description"=>"Matt Lee",
  "uri"=>"http://creativecommons.org",
  "approved"=>"1",
  "category"=>"1",
  "name"=>"Creative Commons"
]]);