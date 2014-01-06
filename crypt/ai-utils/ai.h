#ifndef __AI_H
#define __AI_H

#include <sys/types.h>
#include <stdbool.h>

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

extern const char *ascii_stab;

/* map8: maximum 256 chars required. */
char *parse_stab(char *map8, const char *stab);

/* stab: maximum 256 chars required. */
char *format_stab(char *stab, const char *map8);

void *ai_decode(const char *in, size_t size, size_t *out_size,
                const char *keydata, size_t keysize);

void *ai_encode(const char *in, size_t size, size_t *out_size,
                const char *keydata, size_t keysize,
                const char *stab, const char *frame_image);

bool ai_decode_file(const char *in_path, FILE *in,
                    const char *out_path, FILE *out,
                    const char *keydata, size_t keysize);

bool ai_encode_file(const char *in_path, FILE *in,
                    const char *out_path, FILE *out,
                    const char *keydata, size_t keysize,
                    const char *stab,   /* 8-bit substition table  */
                    const char *frame_image);

#endif
