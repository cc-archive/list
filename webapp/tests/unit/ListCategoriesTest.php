<?php

if (! defined('TESTING')) define ('TESTING', true);

require_once('data/List.php');

class ListCategoriesTest extends \Codeception\TestCase\Test
{
    /**
     * @var \UnitTester
     */
    protected $tester;

    protected function _before()
    {
      $this->tester->runMigrations();
      $this->tester->runFixtures(['makers', 'categories', 'list']);
    }

    protected function _after()
    {
    }

    // tests
    public function testListing()
    {
      $selection = UserList::getCategories(20);
      $this->assertEquals(count($selection), 6);
    }
}
