$TTL            3

; <self-evolution mark='*' decl='%'>
; %EVAL { use cmt::dnsutil }

@               IN      SOA     vns1.zoo.bodz.net. domain.bodz.net. (
                                        2       ; serial
                                        1M      ; refresh
                                        15M     ; retry
                                        12H     ; expiry
                                        3 )     ; minimum
                        NS      @
                        NS      ns1.pfpf.net.
                        NS      ns2.pfpf.net.
                        NS      ns3.pfpf.net.
                        NS      ns4.pfpf.net.
                        NS      ns5.pfpf.net.
                        NS      ns6.pfpf.net.
                        NS      vns1.zoo.
                        NS      vns2.zoo.
                        NS      vns3.zoo.
                        NS      vns1.zoo.bodz.net.
                        NS      vns2.zoo.bodz.net.
                        NS      vns3.zoo.bodz.net.

                        TXT     "t=zoo owner=lenik"
                        TXT     "t=zoo mode=test"

                        MX      10      main
                        MX      9       vmail.zoo.bodz.net.
                        MX      9       vmail.zoo.
                        TXT     "t=spf1 A MX ~all"      ; SPF Framework

mail                    CNAME   main
www                     CNAME   main
ftp                     CNAME   main

; testing purpose
volatile        3       A       1.2.3.4
volatile2       30      CNAME   volatile
volatile3       3000    CNAME   volatile2

; </self-evolution>
