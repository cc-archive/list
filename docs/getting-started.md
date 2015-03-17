---
layout: page
title: How to get started hacking on The List
permalink: /hacking/webapp/
---

**by Matt Lee**

The code is split up into two sections: The Android app and the web
application. This early version of the getting started guide reflects
way to get started hacking on the web application.

The web application also serves as the host for the API that is used
by both the web application and the Android application.

You'll need a couple of things to get started.

* A text editor
* The git version control system and how to use it
* A local web server (Apache) with MySQL and PHP 5.4 or later.
* PHP Composer `curl -sS https://getcomposer.org/installer | php`
* A GitHub account
* A [local CAS server](https://github.com/rubycas/rubycas-server), or a [Creative Commons ID](https://login.creativecommons.org) (CCID)

Plus a few libraries for PHP:

* smarty
* adodb
* curl
* gd

All of the above can be installed on a Debian system with:

`apt-get install smarty3 php5-curl php5-gd php5-adodb libphp-adodb`

We also assume `mod_rewrite` is enabled in Apache, for the API at least.

### A word on environments

All of the instructions assume a GNU/Linux environment. We deploy on
Debian Wheezy currently. If you're on anything else, you have a couple choices:

* Install Debian inside of [Vagrant](http://vagrantup.com). Install
Vagrant and follow these commands to get a Vagrant box similar to the
production server, and yes, `puphpet` is not a typo.

  * `vagrant init puphpet/debian75-x64`
  * `vagrant up`
  * See the [Vagrant documentation](http://docs.vagrantup.com) for
    more details on working with Vagrant.

* Set up a server at [BigV](http://bigv.io) where we have The List
  hosted, or a similar VPS provider. Linode, AWS, etc.

## Getting started

* Go to https://github.com/creativecommons/list and make a fork of the
  project. You'll do your work on a fork and then commit pull requests
  to the main project.

* With your clone of the project, set up Apache to look at `webapp/` as
  the DocumentRoot for your site.

* Copy the `config.sample` file to `config.php` and change a few
  variables. Most notably: the variable for the database (first line)
  and the line which points to the CAS server (login.example.com is
  used as an example).

* You should also look over the included `sql.txt` and at the very
  least import the tables, if not the data.

* You should make sure that the directory
  `webapp/themes/thelist/templates_c` is writable by the
  web-server. This is where smarty writes the compiled templates
  cache.

* You should be all set. Visit <http://localhost> or equivalent in
  your browser and you should see a screen with a <span class="btn
  btn-success">login</span> button and a <span class="btn
  btn-primary">register</span> button. If you don't, something is
  amiss. Check your Apache log files for errors while refreshing the page.

  * `tail -f /var/log/apache2/error.log`

### How the project is structured

* There are a few common files, most notably `database.php` in the
  root, and `data/User.php`, `data/List.php` and `data/Auth.php`. Both
  User.php and List.php represent the functions used to add/fetch
  Users and List items from the database. `database.php` itself
  provides easy access to `php5-adodb` and Auth.php handles
  authentication against the `rubycas-server` which in our case, is
  <https://login.creativecommons.org>.

* Each controller in the database has its own PHP file. With the
  exception of index.php, these should be reasonably easy to
  understand what they do from the name, but as ever, I am terrible at
  naming things consistently. Feel free to send fixes to have things
  named better.

* Each controller calls one or more functions in data/List.php,
  assembles an array or two of things and passes these into Smarty
  template variables. It then calls the Smarty template that'll be
  used. These same functions are then mapped to API endpoints in
  `api/index.php` which uses `api/klein.php` as a router. Currently
  this is the only part which requires mod_rewrite, so if you're not
  hacking the Android app too, you can forgo this. Nevertheless,
  [here's how we set it up](https://gist.github.com/mattl/ed8557c290660f8e0c7c).

* As the web application is based on GNU FM, here's the
[GNU FM install instructions](http://bugs.foocorp.net/projects/fm/wiki/How_to_install),
just for reference.

> This work is licensed under a <a rel="license"
href="http://creativecommons.org/licenses/by/4.0/">Creative Commons
Attribution 4.0 International License</a>.
