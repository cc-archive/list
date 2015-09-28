<?php

namespace Helper;

class DbHelper extends \Codeception\Module
{

  public function runMigrations () {
    $migrationpaths = array();
    foreach(glob('migrations/*.sql') as $migrationpath) {
      array_push($migrationpaths, $migrationpath);
    }
    //\Codeception\Util\Debug::debug(json_encode($migrationpaths));
    $this->runSQLFiles($migrationpaths);
  }

  public function runFixtures ($names) {
    $fixturepaths = array();
    foreach($names as $name) {
      array_push($fixturepaths, 'tests/_data/' . $name . '_fixtures.sql');
    }
    $this->runSQLFiles($fixturepaths);
  }

  public function runSQLFiles ($filepaths) {
    if(count($filepaths) > 0){
      $dbh = $this->getModule('Db');
      $queries = array();
      foreach($filepaths as $filepath) {
        $query = file_get_contents($filepath);
        array_push($queries, $query);
      }
      //\Codeception\Util\Debug::debug(json_encode($queries));
      $res = $dbh->driver->load($queries);
    }
  }
 
}