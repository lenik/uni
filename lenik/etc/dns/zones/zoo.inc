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

                        MX      10      @
                        MX      5       vmail.zoo.bodz.net.
                        MX      5       vmail.zoo.
                        TXT     "t=spf1 A MX ~all"

mail                    CNAME   @
www                     CNAME   @
ftp                     CNAME   @

; </self-evolution>
