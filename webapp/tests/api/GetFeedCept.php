<?php 
$I = new ApiTester($scenario);
$I->wantTo('list the most recent photos in the feed');
$I->sendGET('photos');
$I->seeResponseCodeIs(200);
//TODO: Check for reasonable values
$I->seeResponseJsonMatchesJsonPath('$.next_id');
$I->seeResponseJsonMatchesJsonPath('$.from_id');
$I->seeResponseJsonMatchesJsonPath('$.count');
//TODO: check for Creative Commons values
$I->seeResponseJsonMatchesJsonPath('$photos[*].itemid');
$I->seeResponseJsonMatchesJsonPath('$photos[*].url');
$I->seeResponseJsonMatchesJsonPath('$photos[*].title');
$I->seeResponseJsonMatchesJsonPath('$photos[*].description');
$I->seeResponseJsonMatchesJsonPath('$photos[*].category');
$I->seeResponseJsonMatchesJsonPath('$photos[*].approved');
$I->seeResponseJsonMatchesJsonPath('$photos[*].username');
$I->seeResponseJsonMatchesJsonPath('$photos[*].userid');
$I->seeResponseJsonMatchesJsonPath('$photos[*].makerid');
$I->seeResponseJsonMatchesJsonPath('$photos[*].makername');