FROM debian:jessie

MAINTAINER Rob Myers <rob@robmyers.org>

################################################################################
# System dependencies
################################################################################

RUN apt-get update
RUN apt-get -y upgrade

RUN DEBIAN_FRONTEND=noninteractive apt-get -y install curl apache2 \
    libapache2-mod-php5 php5-mysql php5-gd php-pear php-apc php5-curl

################################################################################
# Apache
################################################################################

# Enable apache mods.
RUN a2enmod php5
RUN a2enmod rewrite

# Apache environment variables
ENV APACHE_RUN_USER www-data
ENV APACHE_RUN_GROUP www-data
ENV APACHE_LOG_DIR /var/log/apache2
ENV APACHE_LOCK_DIR /var/lock/apache2
ENV APACHE_PID_FILE /var/run/apache2.pid

# Map the web server port
EXPOSE 80

# Nuke the index.html so index.php defaults
RUN rm /var/www/html/index.html

# Make the api and images accesible
ADD docker/rewrites.conf /etc/apache2/
RUN perl -i -p -e \
    "s|(DocumentRoot /var/www/html)|\1\n\tInclude /etc/apache2/rewrites.conf|" \
    /etc/apache2/sites-available/000-default.conf

################################################################################
# Composer
################################################################################

# Install composer
ENV COMPOSER_VERSION 1.0.0-alpha10
RUN curl -sS https://getcomposer.org/installer | php -- \
    --install-dir=/usr/local/bin --filename=composer \
    --version=${COMPOSER_VERSION}

################################################################################
# The List
################################################################################

# Install and configure the application
ADD . /var/www/html

WORKDIR /var/www/html

# Remove any existing config
RUN rm config.php

# Create a new config
RUN cp config.sample config.php
RUN perl -i -p -e 's/username:password\@localhost/listuser:listusersecretpassword\@thelistdb/' config.php
RUN perl -i -p -e 's/login.example.com/login.creativecommons.org/' config.php
RUN perl -i -p -e 's/https:\/\/libre.fm/http:\/\/\$_SERVER["SERVER_ADDR"]/' config.php

# Run composer to install the php dependencies
RUN composer install

################################################################################
# The command to run each time the container launches
################################################################################

# Run Apache in the foreground as the container's process
CMD /usr/sbin/apache2ctl -D FOREGROUND
