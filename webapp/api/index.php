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

        $list = new UserList();

	$selection = $list->getCategoriesList(20, $request);

        $output = json_encode($selection, JSON_PRETTY_PRINT);
        echo $output;

      });

  });


dispatch();
