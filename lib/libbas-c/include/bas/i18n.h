#ifndef __BAS_I18N_H
#define __BAS_I18N_H

#include <libintl.h>
#include <locale.h>

#ifndef I18N_DOMAIN
#   ifdef PACKAGE_NAME
#       define I18N_DOMAIN PACKAGE_NAME
#   endif
#endif

#ifdef I18N_DOMAIN
#   define tr(s) dgettext(I18N_DOMAIN, s)
#else
#   define tr(s) gettext(s)
#endif

#endif
