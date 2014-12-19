The List -- Functional Specification
===================================

(this is an HTML version of the spec, which is in Markdown and should go
into git once any changes are made here.)

Last Updated: September 30th 2014

About this Specification

This specification is simply a starting point for the design of The List
1.0, not a final blueprint. As we start to build the product, we'll
discover a lot of things that won't work exactly as planned. We'll
invent new features, we'll change things, we'll refine the wording, etc.
We'll try to keep the spec up to date as things change. By no means
should you consider this spec to be some kind of holy, cast-in-stone
law.

Overview

“The List” is the codename for a new product to be developed and tested
at Creative Commons during the fall/winter of 2014. The actual name of
the product may be different.

The List allows people to contribute to the public commons by taking
photographs of the world around them and sharing these images with the
world.

Here's a brief summary of how The List works:

1.  User visits The List website and installs the app on their phone.
2.  Opens the app and registers to get an account (CCID) and agrees to
    terms, sets attribution info, etc. This should default to their name
    from their Android device where we can get it.
3.  Starts taking photos of things from The List.

Easy to get started:

-   Mobile app available on typical app stores and also direct from
    website.
-   Simple terms and conditions and registration is minimal.

Version 1.0 is Android only and requires a mobile data connection.

Design Goals 
------------

All design and engineering decisions will be taken with the following
principle in mind:

Simple is better than complicated.

We will always try to eliminate choices that the user must make. Every
bump on the user experience must be relentlessly sanded down.

No jargon will remain in the user experience, anywhere.

Major Components 
----------------

The List system runs on servers which are hosted by Creative Commons.
There are four major software components in The List.

### The Website 

A database-backed website used to register with The List, and where
partners can add items to The List.

### Mobile application 

Software run by the user. This is a mobile app that has a simple user
interface^[[a]](#cmnt1)^^[[b]](#cmnt2)^.

### The Processor 

Takes the images uploaded to The List and processes them and batch
uploads them to The Internet Archive.

### CC Search 

A search interface to the public commons that'll include (and optionally
only show) images on The List.

Licenses 
--------

All software, including CC Search and the mobile application will be
released under the GNU Affero General Public License version 3.0 or
later.

All content uploaded by users of The List is licensed under Creative
Commons Attribution 4.0.

About The Rollout 
-----------------

Our primary goal is to rollout a fully-functional version of The List in
a few months. That means that the initial version will almost certainly
not be optimal in every way and will not contain every feature.

Physical Architecture 
---------------------

The initial rollout will be hosted on CC's servers at Bytemark's data
center in York, UK.

Software Architecture 
---------------------

The app will be written using either native technology or Apache
Cordova.

The web applications will be written using PHP and MySQL, hosted on
Debian web servers and SSL, using the standard Creative Commons
application structure.

The processor will run as a cronjob, likely running a script written in
Python.

The User Experience 
-------------------

The user hears about The List from the Creative Commons website or
newsletter. They visit The List website,
https://thelist.creativecommons.org which has a little text explaining
the goals of The List and directs them to install the mobile
application.

Upon opening the application, the user is given an option to login,
register or to learn more about The List.

### Learn more 

No one can be everywhere at once. But *everyone* can.

NGOs, journalists, government agencies, and cultural institutions all need photographs to tell their story and educate others. But there's no way for those organizations to be in the right place at the right time, every time. That's where we come in.

Through The List, organizations will provide lists of locations, people, and events that they need photographs of. And when users are in the right place at the right time, they can claim an item from the list and publish a photograph of it.

All of the photographs captured through The List will be licensed under [the Creative Commons Attribution license](https://creativecommons.org/licenses/by/4.0/). That means that anyone will be able to reuse them for any purpose, commercial or noncommercial, as long as they give the photographer credit. We're building a collection of high-quality photography that anyone can use, with no fees or hoops to jump through.

Whether it's a lighthouse, a lunar eclipse, or a political demonstration, you can be the person to take the perfect shot.

The List is a project of [Creative Commons](https://creativecommons.org/), supported by a [generous grant from the Knight Foundation](https://creativecommons.org/weblog/entry/44004).

### Register 

Registration is a simple form that has three fields:

-   Name (however people want to be credited for their work) -- this
    should be a single text field.
-   Email address: A single text field.
-   Password: A single password field.

Upon completing this form, we should allow people to start taking photos
immediately and not worry about their registration to stop them from
doing anything. Completing the form sends them an email with a single
link to click. Clicking that link activates their account.

### Log in 

-   Email address field.
-   Password

There's also a link for ‘I forgot my password' which is a link to the
reset password form on https://id.creativecommons.org/forgot/

Once a user is logged in: 
-------------------------

-   The app will contact the https://thelist.creativecommons.org/list
    and download a snapshot of The List. This will contain the details
    of a number of potential images that we're looking for (ie. Mountain
    Dew, Spiderman comic, Morrissey, Mount Rushmore) and encourage the
    user to take photographs of these things with their phone.
-   The list will be displayed as a series of cards, each one listing a
    title and where possible some extra information (from Wikipedia?)
-   The user can swipe away images they have no interest in taking
    (using a metaphor from Tinder) or swipe cards into a to-do list
    feature.
-   Taking a picture would be as simple as clicking a camera icon on the
    card listing the item in The List that's of interest.

Future Features 
---------------

In future releases of The List, we would expect to know where a user is
in the world and be able to show them requests for images local to them,
such as a building in their town, or perhaps Morrissey is on tour in
their town.

Here's the general flow of The List website:

The Home Page 
-------------

Ideally this is a one-page site, with useful exciting copy at the top
and a large download button. Other copy is further down the page,
including teaser copy to get partners involved, and maybe some partner
logos.

On the home page you see:

### Welcome to The List! 

TODO: Copy goes here.

#### Download 

-   Download from Google Play
-   Download from Amazon Appstore
-   Download from F-Droid
-   Direct apk download

#### Privacy Policy and Terms of Service 

-   These will likely be standard CC documents, but check with Diane on
    this.
-   Will need custom ToS, I expect. Sarah volunteered to work on this
    with us.

#### Conflicts and Communications Problems 

To insure a seamless experience this section will list possible things
that can go wrong and explain how we recover from them gracefully.

App can't connect to the The List because of a DNS problem.

The app knows the absolute IP address of The List server it's supposed
to use and never uses DNS.

App can't connect because they no longer have Internet connectivity.

The app should have a cache of at least some images we're looking for.
Users who are offline can still take pictures, which are queued up in
the app to be uploaded when there's a connection.

Future versions could expand on this by having an offline-mode.

Security 
--------

The List website, the CC search website and the CCID site are SSL
websites from top to bottom.

The app communicates with The List website over HTTPS.

[[a]](#cmnt_ref1)Worth noting here that the app invokes the user's
preferred camera? That it is not, in itself, a camera app?

[[b]](#cmnt_ref2)Android seems to have a mechanism to just invoke the
camera directly. Not sure if that uses a preferred app or the default OS
camera functionality.
