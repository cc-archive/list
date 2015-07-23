<?php

/* The List powered by Creative Commons

   Copyright (C) 2014, 2015 Creative Commons Corporation

   This program is free software: you can redistribute it and/or modify
   it under the terms of either the GNU Affero General Public License or
   the GNU General Public License as published by the
   Free Software Foundation, either version 3 of the Licenses, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 

   You should have received a copy of the GNU General Public License and
   the GNU Affero General Public License along with this program.  

   If not, see <http://www.gnu.org/licenses/>.

*/

// TODO: This needs a lot of work to make it not horribly unsafe.

require "templating.php";
require "data/List.php";

if ($userid) {

    if ($auth) {
        $item = $_POST['list-item'];
        $list = new UserList();
        $save = $list->addToMyList($item, $userid);
        header('Location: my-list.php');
    }
}

?>
