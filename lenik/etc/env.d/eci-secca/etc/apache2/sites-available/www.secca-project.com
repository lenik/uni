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

    ErrorLog /var/log/apache2/error.log

    # Possible values include: debug, info, notice, warn, error, crit,
    # alert, emerg.
    LogLevel warn

    CustomLog /var/log/apache2/access.log combined

    <Location /trac>
        SetHandler mod_python
        PythonInterpreter main_interpreter
        PythonHandler trac.web.modpython_frontend
        PythonOption TracEnv /node/repos/trac
        # PythonOption TracUriRoot /trac
    </Location>

    <LocationMatch /trac/login>
        LoadModule auth_digest_module /usr/lib/apache2/modules/mod_auth_digest.so
        AuthType Digest
        AuthName "trac"
        AuthDigestDomain /trac
        AuthUserFile /node/repos/trac/conf/trac.passwd
        Require valid-user
    </LocationMatch>

</VirtualHost>
