<VirtualHost svn.secca-project.com:80>
	ServerAdmin admin@secca-project.com

	DocumentRoot /var/www
	<Directory />
		Options FollowSymLinks
		AllowOverride None
	</Directory>

	ErrorLog /var/log/apache2/error.log

	# Possible values include: debug, info, notice, warn, error, crit,
	# alert, emerg.
	LogLevel warn

	CustomLog /var/log/apache2/access.log combined

    <Location / >
        DAV svn
        SVNPath /repos/svn
    </Location>

</VirtualHost>
