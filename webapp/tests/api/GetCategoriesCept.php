<?php 
$I = new ApiTester($scenario);
$I->wantTo('get the list of categories');
$I->sendGET('category');
$I->seeResponseCodeIs(200);
$I->seeResponseIsJson();
$I->seeResponseContainsJson([[
      "id"=>"6",
      "title"=>"Objects",
      "makerid"=>"1",
      "approved"=>"1",
      "color"=>"e91e63"
]]);
