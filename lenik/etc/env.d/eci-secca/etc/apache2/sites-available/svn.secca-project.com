<VirtualHost *:80>
    ServerName svn.secca-project.com
	ServerAdmin admin@secca-project.com

	ErrorLog /var/log/apache2/error.log

	# Possible values include: debug, info, notice, warn, error, crit,
	# alert, emerg.
	LogLevel warn

	CustomLog /var/log/apache2/access.log combined

    <Location /artifacts>
        DAV svn
        SVNPath /repos/svn/artifacts
    </Location>

    <Location /sandbox>
        DAV svn
        SVNPath /repos/svn/sandbox
    </Location>

    <Location /ssp>
        DAV svn
        SVNPath /repos/svn/ssp
    </Location>

    <Location /test>
        DAV svn
        SVNPath /repos/svn/test
    </Location>

</VirtualHost>
