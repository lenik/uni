$INCLUDE        zones/public.inc

; <self-evolution mark='*' decl='%'>
; %EVAL { use cmt::dnsutil }

; %vicp_lenik   { $_ = conf_replace_ip($_, resolv('lenik.vicp.net'))  }
; %vicp_lenik2  { $_ = conf_replace_ip($_, resolv('lenik2.vicp.net')) }
; %vicp_lenik3  { $_ = conf_replace_ip($_, resolv('lenik3.vicp.net')) }

@                       A       60.163.13.93    ; *vicp_lenik

main                    A       220.185.95.107    ; *vicp_lenik

hamachi                 A       5.69.77.113

; </self-evolution>
