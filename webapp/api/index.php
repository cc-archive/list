<?php 

/* The List powered by Creative Commons

   Copyright (C) 2014 Creative Commons

   based on:

   GNU FM -- a free network service for sharing your music listening habits

   Copyright (C) 2009 Free Software Foundation, Inc

   This program is free software: you can redistribute it and/or modify
   it under the terms of the GNU Affero General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU Affero General Public License for more details.

   You should have received a copy of the GNU Affero General Public License
   along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/

require_once('../database.php');
require '../data/User.php';
require '../data/List.php';
require 'klein.php';

header('Content-Type: text/javascript; charset=utf8');
header('Access-Control-Allow-Origin: https://thelist.creativecommons.org/');
header('Access-Control-Max-Age: 3628800');
header('Access-Control-Allow-Methods: GET, POST, PUT, DELETE');

with('/api/category', function () {

    respond('GET', '/?', function ($request, $response) {

        $list = new UserList();

	$selection = $list->getCategories(20);
        
        $output = json_encode($selection, JSON_PRETTY_PRINT);
        echo $output;


    });

    respond('GET', '/[:id]', function ($request, $response) {
        // Show items from a single category

	$id =$request->id;

        $list = new UserList();

        $selection = $list->getCategoriesList(20, $id);

        $output = json_encode($selection, JSON_PRETTY_PRINT);
        echo $output;

    });

});


with('/api/items', function () {

    respond('GET', '/?', function ($request, $response) {

        $list = new UserList();

	$selection = $list->getNewList(20);
        
        $output = json_encode($selection, JSON_PRETTY_PRINT);
        echo $output;


    });

    respond('GET', '/[:id]', function ($request, $response) {
        // Show items from a single category

	$id =$request->id;

        $list = new UserList();

        $selection = $list->getSingleListItem($id);

        $output = json_encode($selection, JSON_PRETTY_PRINT);
        echo $output;

    });

    respond('POST','/[:id]', function ($request, $response) {

        echo "This is where you can add things to your list";

    });

});

with('/api/photos', function () {

    respond('POST', '/[:userid]/[:id]', function ($request, $response) {

        $userid = $request->userid;
        $listitem = $request->id;
        
        $filedata = $request->param('filedata');

        $filedata = base64_decode($filedata);

        if (strlen($filedata) > 50) {

        $tmp = "" . tempnam("../list-uploads/", "process-me-" . $userid . "-");

        $image = fopen($tmp, "w") or die("Unable to open file!");
        fwrite($image, $filedata);
        fclose($image);

        
        $list = new UserList;       
        $filename = $tmp;
                    
        $result = $list->addPhoto($userid, $filename, $listitem);

        }

        else {

            http_response_code(400);
        }
        
    });

});

with('/api/users', function () {

    respond('POST', '/login', function ($request, $response) {

        $url = 'https://login.creativecommons.org/x.php';
        $fields = array(
            'username' => urlencode($request->param('username')),
            'password' => urlencode($request->param('password'))
        );

        foreach($fields as $key=>$value) { $posty .= $key.'='.$value.'&'; }
        rtrim($posty, '&');

        $curl = curl_init();

        curl_setopt($curl,CURLOPT_URL, $url);
        curl_setopt($curl,CURLOPT_POST, count($fields));
        curl_setopt($curl,CURLOPT_POSTFIELDS, $posty);
        curl_setopt($curl, CURLOPT_RETURNTRANSFER, true);

        $result = curl_exec($curl);

        if ($result) {     // if the user exists in CAS

            $user = new UserList();

            $foo = $user->getUserInfo($result); // get the userID, etc

            if (count($foo) == 0) {  // first time on The List

                $user->makeUser($result); // make a User

                $foo = $user->getUserInfo($result); // now get the userID
            }

            // Now let's make a session for the user

            $userid = $foo['id'];

            $session = $user->getUserSession($userid);

            $result = array($session, $userid);

            return $result;
            
        }

        else { // invalid user

            http_response_code(401);                

        }

        curl_close($curl);

    });

    respond('POST', '/register', function ($request, $response) {

        echo "This is the register stub";

    });

    respond('GET', '/[:email]', function ($request, $response) {

        echo "This is the GET USER stub";

    });



});


with('/api/makers', function () {

    respond('GET', '/[:id]', function ($request, $response) {

	$id =$request->id;

        $list = new UserList();

        $selection = $list->getMakerProfile($id);

        $output = json_encode($selection, JSON_PRETTY_PRINT);
        echo $output;

    });

});

with('/api/userlist', function () {

    respond('GET', '/[:id]', function ($request, $response) {

	$id=$request->id;

        $list = new UserList();

        $selection = $list->getUserTopList(100, $id);

        $output = json_encode($selection, JSON_PRETTY_PRINT);

        echo $output;

    });

    respond('POST','/[:user]/[:id]', function ($request, $response) {

        $item=$request->id;
	$userid=$request->user;

        // We'll need to figure out a way to do these more securely

        $list = new UserList();
        $save = $list->addToMyList($item, $userid);

        echo "[]";

    });

});

with('/api', function () {

    respond('GET', '/', function ($request, $response) {

        header('Content-Type: text/html; charset=utf8');
        require_once('api.html');

    });

});



dispatch();
