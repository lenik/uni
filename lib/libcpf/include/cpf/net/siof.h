#ifndef _SIOF_utim_c6bjdE5Hdi55DE5k_
#define _SIOF_utim_c6bjdE5Hdi55DE5k_

#include <cpf/config.h>
#include <cpf/dt/raw.h>

#ifdef WIN32
#include <winsock.h>
#endif

typedef struct _sioctx_t sioctx_t;

typedef (_cc *siopktf_t)(sioctx_t *, size_t, void *);

/* For server, the most parts of sioctx_t are used as a template struct to
 *   create new sioctx(s) when incoming connection request is accepted, and the
 *   sioctx::sd is set to the listenning socket.
 */
#define SIOCTXF_SERVER 1

/* The passive mode affects whether the ctx is used in the sioloop.
 *   For server, this enables auto accept.
 *   For client, this enables auto recv/dispatch.
 */
#define SIOCTXF_PASSIVE 2               /* CS auto recv */

struct _sioctx_t {
    SOCKET sd;
    u32_t flags;
    siopktf_t pkt_in;                   /* for server */
    siopktf_t pkt_out;
    /* stat *statinfo; ... */
};

sioctx_t * _cc siolisten(const sockaddr *addr, int passive, siopktf_t pkt_in);
sioctx_t * _cc siolistenport(u16_t port, int passive, siopktf_t pkt_in);
sioctx_t * _cc sioconnect(const sockaddr *addr, int passive, siopktf_t pkt_in);
sioctx_t * _cc sioupon(SOCKET sd, int server, int passive, siopktf_t pkt_in);
void       _cc siofree(sioctx_t *ctx);
int        _cc siostart();
int        _cc siostop();
int        _cc sioend();

#endif
