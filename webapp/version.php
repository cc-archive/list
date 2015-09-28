<?php

/* The List powered by Creative Commons

   Copyright (C) 2014,2015 Creative Commons Corporation

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
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  

   You should have received a copy of the GNU General Public License and
   the GNU Affero General Public License along with this program.  

   If not, see <http://www.gnu.org/licenses/>.

*/

// http://semver.org/

$api_major_version = 2;
$api_minor_version = 0;
$api_patch_version = 0;

$app_major_version = 4;
$app_minor_version = 0;
$app_patch_version = 0;

$webapp_major_version = 2;
$webapp_minor_version = 0;
$webapp_patch_version = 0;

$api_version = join('.', [$api_major_version, $api_minor_version,
                          $api_patch_version]);

$app_version = join('.', [$app_major_version, $app_minor_version,
                          $app_patch_version]);

$webapp_version = join('.', [$webapp_major_version, $webapp_minor_version,
                             $webapp_patch_version]);
