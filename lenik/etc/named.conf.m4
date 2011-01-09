m4_include(`m4x/util.m4')
M4X_BEGIN()
    m4_define(`MASTER_ZONE', `
    zone "$1" {
        type master;
        file "$2";
    }; ')
M4X_END()

options {
    directory           "C:\lam\home\lenik\etc";
    dump-file           "C:\lam\home\lenik\var\named\cache_dump.db";
    statistics-file     "C:\lam\home\lenik\var\named\stats.txt";
    memstatistics-file  "C:\lam\home\lenik\var\named\mem_stats.txt";

    ; forward all unresolvable request to these addresses
    forwarders          { 202.98.0.68;202.98.5.68; };
};
include "C:\lam\home\lenik\etc\rndc.key";

acl Lan1 {
    127.0.0.0/24;
    192.168.0.0/16;
};

MASTER_ZONE(zoo,            zones/var/zoo.db)
MASTER_ZONE(zoo.bodz.net,   zones/var/zoo.db)

MASTER_ZONE(ebja.com,       zones/var/ebja.db)
MASTER_ZONE(ebja.net,       zones/var/ebja.db)
MASTER_ZONE(ebja.org,       zones/var/ebja.db)
MASTER_ZONE(ebja.cn,        zones/var/ebja.db)
MASTER_ZONE(ebja.com.cn,    zones/var/ebja.db)

MASTER_ZONE(99jsj.com,      zones/com/99jsj.db)
MASTER_ZONE(labja.com,      zones/com/labja.db)
MASTER_ZONE(lapiota.com,    zones/com/lapiota.db)
MASTER_ZONE(ooojjj.com,     zones/com/ooojjj.db)

MASTER_ZONE(0yy.net,        zones/net/0yy.db)
MASTER_ZONE(1na.net,        zones/net/1na.db)
MASTER_ZONE(6sn.net,        zones/net/6sn.db)
MASTER_ZONE(bodz.net,       zones/net/bodz.db)
MASTER_ZONE(pfpf.net,       zones/net/pfpf.db)
MASTER_ZONE(sixor.net,      zones/net/sixor.db)

MASTER_ZONE(bela.cn,        zones/cn/bela.db)
MASTER_ZONE(bia.cn,         zones/cn/bia.db)
MASTER_ZONE(kie.cn,         zones/inc/default.db)
MASTER_ZONE(kiu.cn,         zones/inc/default.db)
MASTER_ZONE(mia.cn,         zones/cn/mia.db)
MASTER_ZONE(xor.cn,         zones/cn/xor.db)
