<VirtualHost *:80>
    ServerName svn.secca-project.com
	ServerAdmin admin@secca-project.com

	ErrorLog /var/log/apache2/error.log

	# Possible values include: debug, info, notice, warn, error, crit,
	# alert, emerg.
	LogLevel warn

	CustomLog /var/log/apache2/access.log combined

    <Location />
        DAV svn
        SVNParentPath /repos/svn
        AuthType Basic
        AuthName "SECCA SVN REPOSITORY"
        AuthUserFile /node/type/etc/authdb/svndav.htpasswd
        Require valid-user
    </Location>

</VirtualHost>
