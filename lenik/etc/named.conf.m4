m4_include(`m4x/util.m4')
M4X_BEGIN()
    m4_define(`NAMED_CONF', `C:\lam\home\lenik\etc')
    m4_define(`NAMED_VAR',  `C:\lam\home\lenik\var\named')
    m4_define(`MASTER_ZONE', `
    zone "$1" {
        type master;
        file "$2";
    }; ')
M4X_END()

include "NAMED_CONF\rndc.key";

options {
    directory           "NAMED_CONF";
    dump-file           "NAMED_VAR\cache_dump.db";
    statistics-file     "NAMED_VAR\stats.txt";
    memstatistics-file  "NAMED_VAR\mem_stats.txt";

    // forward all unresolvable request to these addresses
    forwarders          { 202.98.0.68;202.98.5.68; };
};

acl Lan1 {
    127.0.0.0/24;
    192.168.0.0/16;
};

logging {
    channel ch_default {
	file "NAMED_VAR\named.log";
	severity info;
    };
    channel ch_stat {
        file "NAMED_VAR\stat.log";
        severity info;
    };
    channel ch_access {
        file "NAMED_VAR\access.log";
        severity info;
    };

    // categories: client config database default delegation-only dispatch
    // categories: dnssec general lame-servers network notify queries resolver
    // categories: security unmatched update update-security xfer-in xfer-out
    category default { ch_default; };
    // category statistics { ch_stat; };
    category queries { ch_access; };
};

MASTER_ZONE(test,           zones/var/test.db)
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
