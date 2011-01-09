<VirtualHost *:80>
    ServerName xjl.99jsj.com
	ServerAdmin xjl@99jsj.com

	LogLevel warn
	CustomLog /var/log/apache2/99jsj_access.log combined
	ErrorLog  /var/log/apache2/99jsj_error.log

    DocumentRoot /

    <Directory />
        Options +FollowSymLinks
        AllowOverride None

        Order allow,deny
        Allow from all

        #AuthType Basic
        #AuthName "phpPgAdmin"
        #AuthUserFile /node/type/etc/authdb/developer.htpasswd
        #Require valid-user
    </Directory>
</VirtualHost>
