#ifndef __FILTER_H
#define __FILTER_H

#include <glib.h>

/* program => pattern => config */

#define F_ALLOW 1
#define F_DENY 2

/* The program map: normalized path => rules.  This map could be memory mapped
   to share between processes. */
extern GHashTable *pmap;

/* The rules applied to the current program. This map is lazy initialized to the
   corresponding entry in the pmap when the program starts. */
extern GHashTable *rules;

/* Eagerly load the pmap.  Die immediately if error occurred. */
void pmap_load();

void pmap_dump();

/* Get the rule config applied for the specific target, which is identitied by
   the normalized path.

   If the parameter `rules` is set to NULL, the rules for current process is used.

   The return value is encoded in bit flags. F_ALLOW means the entry is explicit
   allowed, and F_DENY means the entry is explicit denied. The default bahavior
   is undefined. */
int get_config(GHashTable *rules, const char *norm_path);

/* Set the rule config applied for the speific target to specific value. */
void set_config(GHashTable *rules, const char *norm_path, int config);

#endif
