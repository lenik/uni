#
# This is the main Apache HTTP server configuration file.  It contains the
# configuration directives that give the server its instructions.
# See <URL:http://httpd.apache.org/docs/2.2> for detailed information.
# In particular, see
# <URL:http://httpd.apache.org/docs/2.2/mod/directives.html>
# for a discussion of each configuration directive.
#
# Do NOT simply read the instructions in here without understanding
# what they do.  They're here only as hints or reminders.  If you are unsure
# consult the online docs. You have been warned.
#
# Configuration and logfile names: If the filenames you specify for many
# of the server's control files begin with "/" (or "drive:/" for Win32), the
# server will use that explicit path.  If the filenames do *not* begin
# with "/", the value of ServerRoot is prepended -- so "/var/log/apache2/foo.log"
# with ServerRoot set to "/usr" will be interpreted by the
# server as "/usr//var/log/apache2/foo.log".

#
# ServerRoot: The top of the directory tree under which the server's
# configuration, error, and log files are kept.
#
# Do not add a slash at the end of the directory path.  If you point
# ServerRoot at a non-local disk, be sure to point the LockFile directive
# at a local disk.  If you wish to share the same ServerRoot for multiple
# httpd daemons, you will need to change at least LockFile and PidFile.
#
ServerRoot "/usr"

#
# Listen: Allows you to bind Apache to specific IP addresses and/or
# ports, instead of the default. See also the <VirtualHost>
# directive.
#
# Change this to Listen on specific IP addresses as shown below to
# prevent Apache from glomming onto all bound IP addresses.
#
#Listen 12.34.56.78:80
Listen PORT

#
# Dynamic Shared Object (DSO) Support
#
# To be able to use the functionality of a module which was built as a DSO you
# have to place corresponding `LoadModule' lines at this location so the
# directives contained in it are actually available _before_ they are used.
# Statically compiled modules (those listed by `httpd -l') do not need
# to be loaded here.
#
# Example:
# LoadModule foo_module modules/mod_foo.so
#
LoadModule authn_file_module lib/apache2/mod_authn_file.so
LoadModule authn_dbm_module lib/apache2/mod_authn_dbm.so
LoadModule authn_anon_module lib/apache2/mod_authn_anon.so
LoadModule authn_dbd_module lib/apache2/mod_authn_dbd.so
LoadModule authn_default_module lib/apache2/mod_authn_default.so
LoadModule authz_host_module lib/apache2/mod_authz_host.so
LoadModule authz_groupfile_module lib/apache2/mod_authz_groupfile.so
LoadModule authz_user_module lib/apache2/mod_authz_user.so
LoadModule authz_dbm_module lib/apache2/mod_authz_dbm.so
LoadModule authz_owner_module lib/apache2/mod_authz_owner.so
LoadModule authz_default_module lib/apache2/mod_authz_default.so
LoadModule auth_basic_module lib/apache2/mod_auth_basic.so
LoadModule auth_digest_module lib/apache2/mod_auth_digest.so
LoadModule dbd_module lib/apache2/mod_dbd.so
LoadModule dumpio_module lib/apache2/mod_dumpio.so
LoadModule ext_filter_module lib/apache2/mod_ext_filter.so
LoadModule include_module lib/apache2/mod_include.so
LoadModule filter_module lib/apache2/mod_filter.so
LoadModule deflate_module lib/apache2/mod_deflate.so
LoadModule log_config_module lib/apache2/mod_log_config.so
LoadModule log_forensic_module lib/apache2/mod_log_forensic.so
LoadModule logio_module lib/apache2/mod_logio.so
LoadModule env_module lib/apache2/mod_env.so
LoadModule mime_magic_module lib/apache2/mod_mime_magic.so
LoadModule cern_meta_module lib/apache2/mod_cern_meta.so
LoadModule expires_module lib/apache2/mod_expires.so
LoadModule headers_module lib/apache2/mod_headers.so
LoadModule ident_module lib/apache2/mod_ident.so
LoadModule usertrack_module lib/apache2/mod_usertrack.so
LoadModule unique_id_module lib/apache2/mod_unique_id.so
LoadModule setenvif_module lib/apache2/mod_setenvif.so
LoadModule version_module lib/apache2/mod_version.so
LoadModule proxy_module lib/apache2/mod_proxy.so
LoadModule proxy_connect_module lib/apache2/mod_proxy_connect.so
LoadModule proxy_ftp_module lib/apache2/mod_proxy_ftp.so
LoadModule proxy_http_module lib/apache2/mod_proxy_http.so
LoadModule proxy_ajp_module lib/apache2/mod_proxy_ajp.so
LoadModule proxy_balancer_module lib/apache2/mod_proxy_balancer.so
LoadModule ssl_module lib/apache2/mod_ssl.so
LoadModule mime_module lib/apache2/mod_mime.so
LoadModule dav_module lib/apache2/mod_dav.so
LoadModule status_module lib/apache2/mod_status.so
LoadModule autoindex_module lib/apache2/mod_autoindex.so
LoadModule asis_module lib/apache2/mod_asis.so
LoadModule info_module lib/apache2/mod_info.so
LoadModule cgi_module lib/apache2/mod_cgi.so
LoadModule dav_fs_module lib/apache2/mod_dav_fs.so
LoadModule vhost_alias_module lib/apache2/mod_vhost_alias.so
LoadModule negotiation_module lib/apache2/mod_negotiation.so
LoadModule dir_module lib/apache2/mod_dir.so
LoadModule imagemap_module lib/apache2/mod_imagemap.so
LoadModule actions_module lib/apache2/mod_actions.so
LoadModule speling_module lib/apache2/mod_speling.so
LoadModule userdir_module lib/apache2/mod_userdir.so
LoadModule alias_module lib/apache2/mod_alias.so
LoadModule rewrite_module lib/apache2/mod_rewrite.so

<IfModule !mpm_netware_module>
#
# If you wish httpd to run as a different user or group, you must run
# httpd as root initially and it will switch.
#
# User/Group: The name (or #number) of the user/group to run httpd as.
# It is usually good practice to create a dedicated user and group for
# running httpd, as with most system services.
#
# User daemon
# Group daemon
</IfModule>

# 'Main' server configuration
#
# The directives in this section set up the values used by the 'main'
# server, which responds to any requests that aren't handled by a
# <VirtualHost> definition.  These values also provide defaults for
# any <VirtualHost> containers you may define later in the file.
#
# All of these directives may appear inside <VirtualHost> containers,
# in which case these default settings will be overridden for the
# virtual host being defined.
#

#
# ServerAdmin: Your address, where problems with the server should be
# e-mailed.  This address appears on some server-generated pages, such
# as error documents.  e.g. admin@your-domain.com
#
ServerAdmin you@example.com

#
# ServerName gives the name and port that the server uses to identify itself.
# This can often be determined automatically, but we recommend you specify
# it explicitly to prevent problems during startup.
#
# If your host doesn't have a registered DNS name, enter its IP address here.
#
ServerName SERVER:PORT

#
# DocumentRoot: The directory out of which you will serve your
# documents. By default, all requests are taken from this directory, but
# symbolic links and aliases may be used to point to other locations.
#
DocumentRoot "/srv/www/htdocs"

#
# Each directory to which Apache has access can be configured with respect
# to which services and features are allowed and/or disabled in that
# directory (and its subdirectories).
#
# First, we configure the "default" to be a very restrictive set of
# features.
#
<Directory />
    Options FollowSymLinks
    AllowOverride None
    Order deny,allow
    Deny from all
</Directory>

#
# Note that from this point forward you must specifically allow
# particular features to be enabled - so if something's not working as
# you might expect, make sure that you have specifically enabled it
# below.
#

#
# This should be changed to whatever you set DocumentRoot to.
#
<Directory "/srv/www/htdocs">
    #
    # Possible values for the Options directive are "None", "All",
    # or any combination of:
    #   Indexes Includes FollowSymLinks SymLinksifOwnerMatch ExecCGI MultiViews
    #
    # Note that "MultiViews" must be named *explicitly* --- "Options All"
    # doesn't give it to you.
    #
    # The Options directive is both complicated and important.  Please see
    # http://httpd.apache.org/docs/2.2/mod/core.html#options
    # for more information.
    #
    Options Indexes FollowSymLinks

    #
    # AllowOverride controls what directives may be placed in .htaccess files.
    # It can be "All", "None", or any combination of the keywords:
    #   Options FileInfo AuthConfig Limit
    #
    AllowOverride None

    #
    # Controls who can get stuff from this server.
    #
    Order allow,deny
    Allow from all

</Directory>

#
# DirectoryIndex: sets the file that Apache will serve if a directory
# is requested.
#
<IfModule dir_module>
    DirectoryIndex index.html
</IfModule>

#
# The following lines prevent .htaccess and .htpasswd files from being
# viewed by Web clients.
#
<FilesMatch "^\.ht">
    Order allow,deny
    Deny from all
    Satisfy All
</FilesMatch>

#
# ErrorLog: The location of the error log file.
# If you do not specify an ErrorLog directive within a <VirtualHost>
# container, error messages relating to that virtual host will be
# logged here.  If you *do* define an error logfile for a <VirtualHost>
# container, that host's errors will be logged there and not here.
#
ErrorLog /lapiota/var/log/apache2/error_log

#
# LogLevel: Control the number of messages logged to the error_log.
# Possible values include: debug, info, notice, warn, error, crit,
# alert, emerg.
#
LogLevel warn

<IfModule log_config_module>
    #
    # The following directives define some format nicknames for use with
    # a CustomLog directive (see below).
    #
    LogFormat "%h %l %u %t \"%r\" %>s %b \"%{Referer}i\" \"%{User-Agent}i\"" combined
    LogFormat "%h %l %u %t \"%r\" %>s %b" common

    <IfModule logio_module>
      # You need to enable mod_logio.c to use %I and %O
      LogFormat "%h %l %u %t \"%r\" %>s %b \"%{Referer}i\" \"%{User-Agent}i\" %I %O" combinedio
    </IfModule>

    #
    # The location and format of the access logfile (Common Logfile Format).
    # If you do not define any access logfiles within a <VirtualHost>
    # container, they will be logged here.  Contrariwise, if you *do*
    # define per-<VirtualHost> access logfiles, transactions will be
    # logged therein and *not* in this file.
    #
    CustomLog /var/log/apache2/access_log common

    #
    # If you prefer a logfile with access, agent, and referer information
    # (Combined Logfile Format) you can use the following directive.
    #
    #CustomLog /var/log/apache2/access_log combined
</IfModule>

<IfModule alias_module>
    #
    # Redirect: Allows you to tell clients about documents that used to
    # exist in your server's namespace, but do not anymore. The client
    # will make a new request for the document at its new location.
    # Example:
    # Redirect permanent /foo http://www.example.com/bar

    #
    # Alias: Maps web paths into filesystem paths and is used to
    # access content that does not live under the DocumentRoot.
    # Example:
    # Alias /webpath /full/filesystem/path
    #
    # If you include a trailing / on /webpath then the server will
    # require it to be present in the URL.  You will also likely
    # need to provide a <Directory> section to allow access to
    # the filesystem path.

    #
    # ScriptAlias: This controls which directories contain server scripts.
    # ScriptAliases are essentially the same as Aliases, except that
    # documents in the target directory are treated as applications and
    # run by the server when requested rather than as documents sent to the
    # client.  The same rules about trailing "/" apply to ScriptAlias
    # directives as to Alias.
    #
    ScriptAlias /cgi-bin/ "/srv/www/cgi-bin/"

</IfModule>

<IfModule cgid_module>
    #
    # ScriptSock: On threaded servers, designate the path to the UNIX
    # socket used to communicate with the CGI daemon of mod_cgid.
    #
    #Scriptsock /var/run/cgisock
</IfModule>

#
# "/srv/www/cgi-bin" should be changed to whatever your ScriptAliased
# CGI directory exists, if you have that configured.
#
<Directory "/srv/www/cgi-bin">
    AllowOverride None
    Options None
    Order allow,deny
    Allow from all
</Directory>

#
# DefaultType: the default MIME type the server will use for a document
# if it cannot otherwise determine one, such as from filename extensions.
# If your server contains mostly text or HTML documents, "text/plain" is
# a good value.  If most of your content is binary, such as applications
# or images, you may want to use "application/octet-stream" instead to
# keep browsers from trying to display binary files as though they are
# text.
#
DefaultType text/plain

<IfModule mime_module>
    #
    # TypesConfig points to the file containing the list of mappings from
    # filename extension to MIME-type.
    #
    TypesConfig /etc/apache2/mime.types

    #
    # AddType allows you to add to or override the MIME configuration
    # file specified in TypesConfig for specific file types.
    #
    #AddType application/x-gzip .tgz
    #
    # AddEncoding allows you to have certain browsers uncompress
    # information on the fly. Note: Not all browsers support this.
    #
    #AddEncoding x-compress .Z
    #AddEncoding x-gzip .gz .tgz
    #
    # If the AddEncoding directives above are commented-out, then you
    # probably should define those extensions to indicate media types:
    #
    AddType application/x-compress .Z
    AddType application/x-gzip .gz .tgz

    #
    # AddHandler allows you to map certain file extensions to "handlers":
    # actions unrelated to filetype. These can be either built into the server
    # or added with the Action directive (see below)
    #
    # To use CGI scripts outside of ScriptAliased directories:
    # (You will also need to add "ExecCGI" to the "Options" directive.)
    #
    #AddHandler cgi-script .cgi

    # For type maps (negotiated resources):
    #AddHandler type-map var

    #
    # Filters allow you to process content before it is sent to the client.
    #
    # To parse .shtml files for server-side includes (SSI):
    # (You will also need to add "Includes" to the "Options" directive.)
    #
    #AddType text/html .shtml
    #AddOutputFilter INCLUDES .shtml
</IfModule>

#
# The mod_mime_magic module allows the server to use various hints from the
# contents of the file itself to determine its type.  The MIMEMagicFile
# directive tells the module where the hint definitions are located.
#
#MIMEMagicFile /etc/apache2/magic

#
# Customizable error responses come in three flavors:
# 1) plain text 2) local redirects 3) external redirects
#
# Some examples:
#ErrorDocument 500 "The server made a boo boo."
#ErrorDocument 404 /missing.html
#ErrorDocument 404 "/cgi-bin/missing_handler.pl"
#ErrorDocument 402 http://www.example.com/subscription_info.html
#

#
# EnableMMAP and EnableSendfile: On systems that support it,
# memory-mapping or the sendfile syscall is used to deliver
# files.  This usually improves server performance, but must
# be turned off when serving from networked-mounted
# filesystems or if support for these functions is otherwise
# broken on your system.
#
#EnableMMAP off
#EnableSendfile off

# Supplemental configuration
#
# The configuration files in the /etc/apache2/extra/ directory can be
# included to add extra features or to modify the default configuration of
# the server, or you may simply copy their contents here and change as
# necessary.

# Server-pool management (MPM specific)
#Include /etc/apache2/extra/httpd-mpm.conf

# Multi-language error messages
#Include /etc/apache2/extra/httpd-multilang-errordoc.conf

# Fancy directory listings
#Include /etc/apache2/extra/httpd-autoindex.conf

# Language settings
#Include /etc/apache2/extra/httpd-languages.conf

# User home directories
#Include /etc/apache2/extra/httpd-userdir.conf

# Real-time info on requests and configuration
#Include /etc/apache2/extra/httpd-info.conf

# Virtual hosts
#Include /etc/apache2/extra/httpd-vhosts.conf

# Local access to the Apache HTTP Server Manual
#Include /etc/apache2/extra/httpd-manual.conf

# Distributed authoring and versioning (WebDAV)
#Include /etc/apache2/extra/httpd-dav.conf

# Various default settings
#Include /etc/apache2/extra/httpd-default.conf

# Secure (SSL/TLS) connections
#Include /etc/apache2/extra/httpd-ssl.conf
#
# Note: The following must must be present to support
#       starting without SSL on platforms with no /dev/random equivalent
#       but a statically compiled-in mod_ssl.
#
<IfModule ssl_module>
SSLRandomSeed startup builtin
SSLRandomSeed connect builtin
</IfModule>

# Provide a way for additional packages to add configuration fragments
Include /etc/apache2/conf.d/*.conf
