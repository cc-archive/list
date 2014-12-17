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

    respond('POST','/[:id]', function ($rquest, $response) {

        echo "This is where you can add things to your list";

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

        $user = new UserList();

        $foo = $user->getUserInfo($result);

        if ($foo == "") {

            global $adodb;

            $q = sprintf('INSERT INTO Users (email) VALUES (%s)'
            , $adodb->qstr($email));

            try {
                $res = $adodb->Execute($q);
                $user = new UserList();
                $foo = $user->getUserInfo($request->param('username'));

            } catch (Exception $e) {
                echo "There was an error";
                header('Content-Type: text/plain');
                exit;
            }
        }

        echo json_encode($foo, JSON_PRETTY_PRINT);

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
