<VirtualHost *:80>
    ServerName issues.secca-project.com
    ServerAdmin admin@secca-project.com

    DocumentRoot /etc/bugzilla3/
    <Directory "/etc/bugzilla3">
        AllowOverride none
        Order allow,deny
        Allow from all
    </Directory>

    Alias /bugzilla3/doc/ /usr/share/doc/bugzilla3-doc/html/
    Alias /bugzilla3/ /usr/share/bugzilla3/web/
    <Directory "/usr/share/bugzilla3/web">
        Options Indexes
        AllowOverride none
        Order allow,deny
        Allow from all
    </Directory>
    <Directory "/usr/share/doc/bugzilla3-doc/html/">
        Options Indexes MultiViews FollowSymLinks
        AllowOverride None
        Order allow,deny
        Allow from all
    </Directory>

    ScriptAlias /cgi-bin/bugzilla3/ /usr/lib/cgi-bin/bugzilla3/
    <Directory "/usr/lib/cgi-bin">
        AllowOverride None
        Options +ExecCGI -MultiViews +SymLinksIfOwnerMatch
        Order allow,deny
        Allow from all
    </Directory>
    <Directory "/usr/lib/cgi-bin/bugzilla3">
        RewriteEngine on
        RewriteBase /cgi-bin/bugzilla3
        RewriteRule ^$ index.cgi
    </Directory>

    <Directory "/var/lib/bugzilla3/data">
        Options FollowSymLinks
        AllowOverride None
        Order allow,deny
        Allow from all
    </Directory>

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
        AuthUserFile /node/type/etc/authdb/trac.htpasswd
        Require valid-user
    </LocationMatch>

</VirtualHost>
