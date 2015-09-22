<?php
namespace Helper;
// here you can define custom actions
// all public methods declared in helper class will be available in $I

class Api extends \Codeception\Module
{

    function getRequestJSON() {
        return json_decode($this->getModule('REST')->response, true);
    }


}
