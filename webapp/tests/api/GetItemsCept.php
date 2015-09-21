<?php 
$I = new ApiTester($scenario);
$I->wantTo('get the list of latest items from the master list');
$I->sendGET('items');
$I->seeResponseCodeIs(200);
$I->seeResponseIsJson();
//TODO: ensure count is 20
// The responses are random, so we cannot check specific id/title/etc. values
//TODO: check for Creative Commons values
$I->seeResponseJsonMatchesJsonPath('$[*].id');
$I->seeResponseJsonMatchesJsonPath('$[*].makerid');
$I->seeResponseJsonMatchesJsonPath('$[*].title');
$I->seeResponseJsonMatchesJsonPath('$[*].description');
$I->seeResponseJsonMatchesJsonPath('$[*].uri');
$I->seeResponseJsonMatchesJsonPath('$[*].approved');
$I->seeResponseJsonMatchesJsonPath('$[*].category');
$I->seeResponseJsonMatchesJsonPath('$[*].name');