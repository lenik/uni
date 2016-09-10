; vim: set filetype=php :
; Example   IN  3600    MX  10  Parameters...

$TTL            30D

@           IN          SOA     (
                                orig.lenik.net.  ; db creator host
                                admin.lenik.net. ; contact e-mail
                                1       ; serial
                                1H      ; sync refresh interval
                                10M     ; sync retry interval
                                240H    ; sync expire
                                1H      ; minimum TTL
                                )

$INCLUDE inc/dns-usage.db

; z- 10.0.0.0/8
;
; a- 172.16.0.0/12
; q- 192.168.0.0/24
;   q0-q255

<?php
    $spc = '                                ';
    function range1($prefix, $net) {
        global $spc;
        for ($i = 0; $i < 256; $i++) {
            $ipad = substr("$prefix$i$spc", 0, 8);
            echo "$ipad                A       $net.$i\n";
        }
    }

    function range2($prefix, $net) {
        global $spc;
        for ($i = 0; $i < 26; $i++) {
            for ($j = 0; $j < 255; $j++) {
                $ij = chr(0x61 + $i) . $j;
                $ijpad = substr("$prefix$ij$spc", 0, 8);
                echo "$ijpad                A       $net.$i.$j\n";
            }
        }
    }

    range1('q', '192.168.0');
    range1('w', '192.168.1');
    range1('e', '192.168.2');
    range1('r', '192.168.3');
    range1('t', '192.168.4');
    range1('y', '192.168.5');
    range1('u', '192.168.6');
    range1('i', '192.168.7');
    range1('o', '192.168.8');
    range1('p', '192.168.9');

    range2('a', '172.16');
    range2('s', '172.17');
    range2('d', '172.18');
    range2('f', '172.19');
    range2('g', '172.20');
    range2('h', '172.21');
    range2('j', '172.22');
    range2('k', '172.23');
    range2('l', '172.24');

    range2('z', '10.0');
    range2('x', '10.1');
    range2('c', '10.2');
    range2('v', '10.3');
    range2('b', '10.4');
    range2('n', '10.5');
    range2('m', '10.6');
?>
