<?php 
$I = new AcceptanceTester($scenario);
$I->wantTo('ensure that index works');
$I->amOnPage('/');
$I->see('Creative Commons');
