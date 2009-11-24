<VirtualHost *:80>
    ServerName www.secca-project.com
    ServerAdmin admin@secca-project.com

    DocumentRoot /node/www
    <Directory /node/www>
        Options Indexes FollowSymLinks MultiViews
        AllowOverride None
        Order allow,deny
        allow from all
    </Directory>

    Alias /doc/ /usr/share/doc/
    <Directory "/usr/share/doc/">
        Options Indexes MultiViews FollowSymLinks
        AllowOverride None
        Order allow,deny
        Allow from all
        AuthType Basic
        AuthName "User Documents"
        AuthUserFile /node/type/etc/authdb/developer.htpasswd
        Require valid-user
    </Directory>

    ErrorLog /var/log/apache2/error.log

    # Possible values include: debug, info, notice, warn, error, crit,
    # alert, emerg.
    LogLevel warn

    CustomLog /var/log/apache2/access.log combined

</VirtualHost>
