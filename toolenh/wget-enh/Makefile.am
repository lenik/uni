include libauto/project.am

do_subst += \
    -e 's,[@]cachedir[@],$(cachedir),g' \
    -e 's,[@]sudoersdir[@],$(sudoersdir),g'

cachedir = /var/cache/downloads
sudoersdir = /etc/sudoers.d

cache_DATA =

# TODO: Don't know how to configure sudoers.
#sudoers_DATA =
#	sudoers.d/wget

dist_bin_SCRIPTS = \
    urljoin \
    grepurl \
    wget-auto-referer \
    wgetc \
    wgetc-url

dist_aliases_DATA = \
    coolwget.sh

dist_man_MANS = \
    urljoin.1 \
    grepurl.1 \
    wgetc.1 \
    wgetc-url.1

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_bin_SCRIPTS} \
    ${dist_setup_SCRIPTS}
