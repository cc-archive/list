<?php 

/* The List powered by Creative Commons

   Copyright (C) 2014, 2015 Creative Commons Corporation

   based on:

   GNU FM -- a free network service for sharing your music listening habits
   GNU Archie Framework -- a web application framework derived from GNU FM

   Copyright (C) 2009, 2015 Free Software Foundation, Inc

   This program is free software: you can redistribute it and/or modify
   it under the terms of either the GNU Affero General Public License or
   the GNU General Public License as published by the
   Free Software Foundation, either version 3 of the Licenses, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU Affero General Public License for more details.

   You should have received a copy of the GNU General Public License and
   the GNU Affero General Public License along with this program.  

   If not, see <http://www.gnu.org/licenses/>.

*/

require_once(dirname(__DIR__).'/database.php');
require_once(dirname(__DIR__).'/data/User.php');

class UserLogin {

  function __construct () {
    $this->url = 'https://login.creativecommons.org/x.php';
    $this->username = "";
    $this->password = "";
    $this->GUIDToMerge = false;
  }

  function isPreviousGUIDLogin () {
    return (strpos($this->username, "%40") === false)
      && ($this->username !== "");
  }

  function isTempRegistration () {
    return $this->username == "" && $this->password == "";
  }

  function isCASLogin () {
    return $this->username !== "" && $this->password !== "";
  }
  
  function registerTempUser() {
    // No user name or password? Temp user registration
    // Let's make a user with a GUID instead of an email address?
    $guid = generateGUID();
    $result = User::getUserInfo($guid);
    
    while(!empty($result)) {
      $guid = generateGUID();
      $result = User::getUserInfo($guid);
    }
    
    return $guid;
  }

  function loginWithPreviousGUID () {
    $result = User::getUserInfo($this->username);
    if(empty($result)) {
      $result = $false;
    } else {
      $result = $this->username; // this assumes a previous GUID
    }
    return $result;
  }

  function mergePreviousGuid ($email) {
    $anonymous = User::getUserInfo($this->GUIDToMerge);
    $previous_user = User::getUserInfo($email);
    if(!empty($previous_user)) {
      User::mergeUserInfo($previous_user['id'], $anonymous['id']);
    } else {
      if(!empty($anonymous)) {
        User::convertAnonymousUser($anonymous['email'], $email);
      }
    }
  }

  function loginCASUser () {
    $posty = 'username=' . $this->username;
    $posty .= '&password=' . $this->password;
    // username and pasword
    $fieldCount = 2;
    $curl = curl_init();

    curl_setopt($curl, CURLOPT_URL, $this->url);
    curl_setopt($curl, CURLOPT_POST, $fieldCount);
    curl_setopt($curl, CURLOPT_POSTFIELDS, $posty);
    curl_setopt($curl, CURLOPT_RETURNTRANSFER, true);

    $result = curl_exec($curl);

    $http_code = curl_getinfo($curl, CURLINFO_RESPONSE_CODE);
    // If the server is not returning error pages
    if ($http_code == 200) {
      if($this->GUIDToMerge) {
        $this->mergePreviousGuid($result);
      }
    } else {
      $result = false;
    }

    curl_close($curl);
    return $result;
  }

  function processLoginResult($result) {
    $foo = User::getUserInfo($result); // get the userID, etc          
    // @TODO: This may never hit
    if (empty($foo)) {  // first time on The List
      User::makeUser($result); // make a User
      $foo = User::getUserInfo($result); // now get the userID
      
    }
    // Now let's make a session for the user
    $userid = $foo['id'];
    
    $session = User::getUserSession($userid);
    $session['email'] = $result;
    
    return $session;
  }
  
  function login () {
    if ($this->isCASLogin()) {
      $result = $this->loginCASUser();
    } elseif ($this->isTempRegistration()) {
      $result = $this->registerTempUser();
    } elseif ($this->isPreviousGUIDLogin()) {
      $result = $this->loginWithPreviousGuid();
    } else {
      $result = false;
    }
    if ($result) {
      $result = $this->processLoginResult($result);
    }
    return $result;
  }
  
}
