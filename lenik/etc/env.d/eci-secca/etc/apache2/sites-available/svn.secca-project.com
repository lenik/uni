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
        AuthUserFile /repos/svn/conf/auth-file
        Require valid-user
    </Location>

</VirtualHost>
