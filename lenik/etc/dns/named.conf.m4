m4_include(`m4x/util.m4')
m4_include(`dnsutil.m4')

acl Lan1 {
    127.0.0.0/24;
    192.168.0.0/16;
};

M4X_BEGIN()

    m4_define(`MASTER_ZONE', `
    zone "$1" {
        type master;
        file "$2";
    }; ')

M4X_END()

MASTER_ZONE(zoo,            var/zoo.db)
MASTER_ZONE(zoo.bodz.net,   var/zoo.db)

MASTER_ZONE(ebja.com,       var/ebja.db)
MASTER_ZONE(ebja.net,       var/ebja.db)
MASTER_ZONE(ebja.org,       var/ebja.db)
MASTER_ZONE(ebja.cn,        var/ebja.db)
MASTER_ZONE(ebja.com.cn,    var/ebja.db)

MASTER_ZONE(99jsj.com,      com/99jsj.db)
MASTER_ZONE(labja.com,      com/labja.db)
MASTER_ZONE(lapiota.com,    com/lapiota.db)
MASTER_ZONE(ooojjj.com,     com/ooojjj.db)

MASTER_ZONE(0yy.net,        net/0yy.db)
MASTER_ZONE(1na.net,        net/1na.db)
MASTER_ZONE(6sn.net,        net/6sn.db)
MASTER_ZONE(bodz.net,       net/bodz.db)
MASTER_ZONE(pfpf.net,       net/pfpf.db)
MASTER_ZONE(sixor.net,      net/sixor.db)

MASTER_ZONE(bela.cn,        inc/default.db)
MASTER_ZONE(bia.cn,         cn/bia.db)
MASTER_ZONE(kie.cn,         inc/default.db)
MASTER_ZONE(kiu.cn,         inc/default.db)
MASTER_ZONE(mia.cn,         cn/mia.db)
MASTER_ZONE(xor.cn,         cn/xor.db)
