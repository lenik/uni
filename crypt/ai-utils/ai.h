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
    int8_t flags;
    int8_t app;
    u_int16_t checksum;
    u_int32_t size;
} ai_header;

#define AIH_SIZE sizeof(ai_header)
#define AI_ENCRYPT 1

enum {
    AI_APP_NONE = 0,
    AI_APP_GZIP,
    AI_APP_BZIP2,
    AI_APP_XZ,
};

#endif
