<VirtualHost www.secca-project.com:80>
	ServerAdmin admin@secca-project.com

	DocumentRoot /mnt/secca/public/www
	<Directory /mnt/secca/public/www/>
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

</VirtualHost>
