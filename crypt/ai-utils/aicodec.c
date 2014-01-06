#include "config.h"

#include <sys/types.h>
#include <assert.h>
#include <stdlib.h>
#include <string.h>

#include <bas/file.h>
#include <bas/log.h>

#include "ai.h"

const char *ascii_stab =
    "O0S5Z2B8l1:;.,({)}i!"              /* high (10) */
    "KkgycouvUV"                        /* medium: similar (5) */
    "EFPRXY"                            /* medium: add/remove (3) */
    "pqbdsztf69[]"                      /* medium: opposites (6) */
    "JjMmNnWw"                          /* medium: case switch (4) */
    "~^`'I|x*&%-="                      /* low/unstable (6) */
    "aeA4DQT7H#G@3?"                    /* low (7) */
    // unused: +=_-$"<>hrAL
    ;

char *parse_stab(char *map8, const char *stab) {
    memset(map8, 0, 256);
    int len = strlen(stab);
    int i = 0;
    while (i < len) {
        char a = stab[i++];
        char b = stab[i++];
        map8[a] = b;
        map8[b] = a;
    }
    return map8;
}

char *format_stab(char *stab, const char *map8) {
    int a, i = 0;
    for (a = 0; a < 256; a++) {
        char b = map8[a];
        if (b == 0 || a >= b)
            continue;
        stab[i++] = a;
        stab[i++] = b;
    }
    stab[i] = '\0';
    return stab;
}

static void *_ai_decode(const char *in_buf, size_t size,
                        FILE *out, size_t *out_size,
                        const char *keydata, size_t keysize) {
    const char *end;                    /* end of the image */
    char *out_buf = NULL;               /* if out==NULL, write to this buf */
    char *outp;
    char *frame_image = NULL;
    int frame_bits = 0;                 /* Useful bits in a single frame */
    char map8[256];                     /* mapping rebuilt from first frames */
    char stab[256];                     /* format stab for debugging */
    int fid = 0;                        /* frame index */
    int i;                              /* frame byte index */
    size_t cb = 0;                      /* total bytes have been decoded */
    char byt = 0;                       /* output byte buffer */
    int bits = 0;                       /* count of bit filled in the byte */
    int ki = 0;                         /* key data offset */
    int kval = 0;
    ai_header hdr;                      /* the decoded header */
    char *p;
    u_int16_t sum = 0;                  /* checksum of the output file data */

    assert(in_buf);
    assert(out || out_size);

    end = in_buf + size;

    int preamble_len = strlen(in_buf);
    const char *frame0 = in_buf + preamble_len + 1;
    if (frame0 >= end) {
        log_err("Bad image file: frame#0 isn't available.");
        return NULL;
    }
    int frame_len = strlen(frame0);

    const char *frame1 = frame0 + frame_len + 1;
    if (frame1 >= end) {
        log_err("Bad image file: frame#1 isn't available.");
        return NULL;
    }
    if (strlen(frame1) != frame_len) {
        log_err("Bad image file: lengths of frame#0 and frame#1 are different.");
        return NULL;
    }

    /* Rebuilding the stab mapping. */
    frame_image = strdup(frame0);
    memset(map8, 0, sizeof(map8));
    for (i = 0; i < frame_len; i++) {
        char a = frame0[i];
        char b = frame1[i];
        if (a != b) {
            bool swapped = bits++ & 1;
            map8[a] = b;
            map8[b] = a;

            if (swapped)
                frame_image[i] = b;
            frame_bits++;
        }
    }
    log_debug("frame bits: %d", frame_bits);
    
    assert(frame_bits);
    format_stab(stab, map8);
    log_debug("stab: \"%s\"", stab);
    
    int esti_frames = size / (frame_len + 1);
    int esti_size = (esti_frames * frame_bits) / 8;
    log_debug("estimated frames: %d", esti_frames);
    log_debug("estimated size: %d", esti_size);
    
    if (out == NULL) {
        out_buf = (char *) malloc(esti_size);
        outp = out_buf;
    }
    
    memset(&hdr, 0, sizeof(ai_header));
    p = (char *) &hdr;
    bits = 0;

    /* Read the frames. */
    fid = 2;
    const char *frame = frame1;
    int len = frame_len;
    while (1) {
        frame += len + 1;
        if (frame >= end)
            break;

        len = strlen(frame);            /* if the frame len=0, just no data
                                           could be extracted */
        log_debug("Frame#%d length=%d", fid, len);

        for (i = 0; i < len; i++) {
            char a = frame[i];
            char b = map8[a];
            if (b != 0 && a != b) {
                byt <<= 1;
                if (a != frame_image[i])
                    byt |= 1;
                bits++;
                if (bits == 8) {           /* flush a full byte. */
                    if (cb < AIH_SIZE) {
                        /* file header, load and drop it. */
                        *p++ = byt;
                    } else {
                        /* real file data. */
                        if (hdr.flags & AI_ENCRYPT) {
                            if (keydata == NULL) {
                                log_err("File is encrypted. No keyfile specified.");
                                goto err;
                            }
                            kval = kval * 17;
                            kval ^= keydata[ki++] ^ 0xCC;
                            if (ki >= keysize) ki = 0;
                            /* decrypt the file byte */
                            byt ^= kval;
                        }
                        if (out)
                            fputc(byt, out);
                        else
                            *outp++ = byt;
                        sum *= 17;
                        sum ^= byt ^ 0xCC;
                    }
                    byt = 0;
                    bits = 0;
                    cb++;

                    if (cb == AIH_SIZE) {
                        if (hdr.flags & AI_ENCRYPT) {
                            if (keydata == NULL) {
                                log_err("File is encrypted. No keyfile specified.");
                                goto err;
                            }
                        }
                        log_debug("    hdr.flags:    %xh", hdr.flags);
                        log_debug("    hdr.app:      %d", hdr.app);
                        log_debug("    hdr.checksum: %xh", hdr.checksum);
                        log_debug("    hdr.size:     %d", hdr.size);

                        if (hdr.size > esti_size) { /* assert false */
                            log_err("File size %d exceeds the estimated size %d.",
                                    hdr.size, esti_size);
                            goto err;
                        }
                    }
                    if (cb >= AIH_SIZE && (cb - AIH_SIZE) >= hdr.size)
                        break;
                }
            }
        }
        fid++;
    }

    if (sum != hdr.checksum)
        log_err("File checksum failed.");

    log_debug("Decoded bytes=%d last byte=%02x bits=%d checksum=%xh",
              cb, byt, bits, sum);
    if (out_size)
        *out_size = outp - out_buf;

    free(frame_image);
    if (out)
        return out;
    else
        return out_buf;
    
 err:
    if (frame_image)
        free(frame_image);
    if (out_buf)
        free(out_buf);
    return NULL;
}

void *ai_decode(const char *in, size_t size, size_t *out_size,
                const char *keydata, size_t keysize) {
    return _ai_decode(in, size, NULL, out_size, keydata, keysize);
}

void *ai_encode(const char *in, size_t size, size_t *out_size,
                const char *keydata, size_t keysize,
                const char *stab, const char *frame_image) {
    // TODO Not implemented yet.
    // return _ai_encode(in, size, NULL, keydata, keysize, stab, frame_image);
    return NULL;
}

bool ai_decode_file(const char *in_path, FILE *in,
                    const char *out_path, FILE *out,
                    const char *keydata, size_t keysize) {
    char *in_buf;                       /* the text image */
    size_t size;                        /* size in bytes of the text image */
    size_t out_size = 0;
    
    /* padding 1 byte for terminate-NUL. */
    in_buf = _load_file(in, in_path, &size, 0, 1);
    in_buf[size] = '\0';
    log_info("Loaded file %s: size=%d", in_path, size);

    char *err = _ai_decode(in_buf, size, out, &out_size, keydata, keysize);
    
    free(in_buf);
    return err == NULL;
}

bool ai_encode_file(const char *in_path, FILE *in,
                    const char *out_path, FILE *out,
                    const char *keydata, size_t keysize,
                    const char *stab,   /* 8-bit substition table  */
                    const char *frame_image) {
    ai_header *hdr;
    char *data;                         /* all data to be encoded */
    size_t size;                        /* size in bytes of the input file */
    char *end;                          /* end of the data to be encoded */
    const char *t;
    char *p;
    int frame = 0;
    int frame_bits = 0;                 /* useful bit index w/i a frame */
    char byt;
    int bits = 0;                       /* remaining bits of the current byte */
    int ki = 0;                         /* key data offset */
    int kval = 0;

    char a, b;

    assert(frame_image);

    fputs(" \t   \t  \n", out);         /* binfmt magic for scripts. */
    fputc('\0', out);                   /* start the frames. */

    /* draw 2 frames, to dump the stab. */
    for (; frame < 2; frame++) {
        t = frame_image;
        frame_bits = 0;
        while (a = *t++) {
            if ((b = stab[a])) {        /* char is defined in the stab */
                bool swap = frame_bits++ & 1;
                if (frame == 1)
                    swap = !swap;
                if (swap)
                    a = b;
            }
            fputc(a, out);
        }
        fputc('\0', out);               /* end of frame */

        /* frame_bits <= total useful bit in a single frame. */
        if (frame_bits == 0) {
            log_err("No useful bit in the frame image.");
            return false;
        }
    }

    data = _load_file(in, in_path, &size, AIH_SIZE, 0);
    end = data + AIH_SIZE + size;
    hdr = (ai_header *) data;
    memset(hdr, 0, AIH_SIZE);
    hdr->size = size;

    u_int16_t sum = 0;         /* checksum of the input data */
    for (p = data + AIH_SIZE; p < end; p++) {
        byt = *p;
        sum *= 17;
        sum ^= byt ^ 0xCC;
    }
    hdr->checksum = sum;

    if (keydata)
        hdr->flags |= AI_ENCRYPT;

    {
        log_debug("    hdr.flags:    %xh", hdr->flags);
        log_debug("    hdr.app:      %d", hdr->app);
        log_debug("    hdr.checksum: %xh", hdr->checksum);
        log_debug("    hdr.size:     %d", hdr->size);
    }

    p = data;
    ki = 0;

    while (p < end || bits > 0) {
        t = frame_image;
        while (a = *t++) {
            if ((b = stab[a])) {
                /* a useful bit cell. */

                if (bits == 0) {        /* load the next byte. */
                    if (p < end) {
                        byt = *p;
                        if (p - data >= AIH_SIZE) {
                            /* encrypt the real file data. */
                            if (keydata) {
                                kval = kval * 17;
                                kval ^= keydata[ki++] ^ 0xCC;
                                if (ki >= keysize) ki = 0;
                                byt ^= kval;
                            }
                        }
                        bits = 8;
                        p++;
                    } else {
                        byt = 0;        /* padding 0 at the end */
                        bits = 1;
                    }
                }

                if (byt & 0x80)
                    fputc(b, out);
                else
                    fputc(a, out);

                byt <<= 1;
                bits--;
            } else {
                fputc(a, out);
            }
        }
        fputc('\0', out);               /* end of frame */
    }

    free(data);
    return true;
}
