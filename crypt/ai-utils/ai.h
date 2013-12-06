#ifndef __AI_H
#define __AI_H

#include <sys/types.h>
#include <unistd.h>

/* ai image header:
 *
 *   u16 flags;
 *   u8 reserved;
 *   u8 app;
 *   u32 size in bytes;
 */
typedef struct _ai_header {
    u_int16_t flags;
    int8_t reserved;
    int8_t app;
    u_int32_t size;
} ai_header;

#define AIH_SIZE sizeof(ai_header)
#define AIF_ENCRYPT 1

enum {
    AIH_APP_NONE = 0,
    AIH_APP_GZIP,
    AIH_APP_BZIP2,
    AIH_APP_XZ,
};

#endif
