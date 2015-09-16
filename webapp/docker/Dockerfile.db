FROM mariadb:10

ENV MYSQL_ROOT_PASSWORD root
ENV MYSQL_USER listuser
ENV MYSQL_PASSWORD listusersecretpassword
ENV MYSQL_DATABASE thelist

ADD sql.txt /
ADD docker/docker-entrypoint.sh /
RUN chmod +x /docker-entrypoint.sh
