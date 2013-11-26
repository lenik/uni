#ifndef __FILTER_H
#define __FILTER_H

#include <sys/types.h>
#include <glib.h>

typedef int bool;

enum {
    false = 0,
    true = 1,
};

enum {
    FILT_UNINIT = 0,
    FILT_ACTIVE,
    FILT_NOP,
    FILT_ERROR,
    FILT_FATAL,
};

extern int run_level;

/* Determine whether a feature is enabled or not. Return 1 for enable, or 0 for
   disable. */
typedef int (*gate_fn)(pid_t);

/* Encode the contents in the fixed-size buf. */
typedef void (*encode_fn)(char *, size_t);

/* Execution mode bits. */
typedef int xmode_t;

#define F_ALLOW 1
#define F_DENY 2
#define F_INTR 128

typedef struct _program_conf_t {
    const char *name;                   /* program name */
    int mode_all;                       /* the default execution mode */
    int mask;                           /* execution mode mask */
    GHashTable *modes;                  /* norm path => exection mode */
} program_conf_t;

/* The program map: normalized path => rules.  This map could be memory mapped
   to share between processes. */
extern GHashTable *pmap;

/* The config applied to the current program. This conf is lazy initialized to
   the corresponding entry in the pmap when the program starts. */
extern program_conf_t *pconf;

/* Eagerly load the pmap.  Die immediately if error occurred. */
bool pmap_load();

/* Dump the actual config parsed from config files. These configs are then
   merged into a single in-memory structure.

   The function can be used to test the parser function, and diagnostic if there
   is any syntax error in the config files.*/
void pmap_dump();

/* Get the execution mode applied for the specific target, which is identitied
   by the normalized path.

   If the parameter `conf` is set to NULL, the config for current process is
   used.

   The return value is encoded in bit flags. F_ALLOW means the entry is explicit
   allowed, and F_DENY means the entry is explicit denied. The default bahavior
   is undefined. */
xmode_t get_execution_mode(program_conf_t *conf, const char *norm_path);

/* Set the execution mode applied for the speific target to specific value. */
void set_execution_mode(program_conf_t *conf, const char *norm_path, xmode_t mode);

/* Get merged execution mode recursively. This is the AND-ed mode bits from all
   ancestors of the process given by pid. The obj param contains the target
   launching path, which may not have been normalized yet. */
xmode_t get_execution_mode_rec(const char *src, pid_t pid, const char *obj);

#endif
