#!/bin/bash

HEXTAB=0123456789ABCDEF
w=1000

function num2hex() {
    local ch=$(( $1 / 16 ))
    local cl=$(( $1 % 16 ))
    echo -n ${HEXTAB:ch:1}${HEXTAB:cl:1}
}

for i in {0..63}; do
    [ $((i % 8)) = 0 ] && echo -n "        "
    echo -n "'#"

    h=$(( RANDOM % w ))
    s=$w # $((RANDOM % 100))
    v=$w # $((RANDOM % 100))

    if [ $s = 0 ]; then
        r=$((v * 256))
        g=$((v * 256))
        b=$((v * 256))
    else
		section=$(( h * 6 ))
	        offset=$(( section % w ))
	        section=$(( section / w ))
	        compl=$(( w - offset ))

        # k1 = v * (1-s)
        #       = vw * (1w-sw)/w
        #       = v' * (w - s') / w
		k1=$(( v * (w - s) / w ))

		# k2 = v * (1 - s * offset)
		#       = vw * (1w - sw * offw/w)/w
		#       = v' * (w - s' * off' / w) / w
		k2=$(( v * (w - s * offset / w) / w ))

		# k3 = v * (1 - s * (1 - offset))
		#       = v * (1 - s * compl)
		#       = vw * (1w - sw * complw/w)/w
		#       = v' * (w - s' * compl'/w) / w
		k3=$(( v * (w - s * compl / w) / w ))

		case $section in
		    0)
		        r=$v; g=$k3; b=$k1;;
		    1)
		        r=$k2; g=$v; b=$k1;;
		    2)
		        r=$k1; g=$v; b=$k3;;
		    3)
		        r=$k1; g=$k2; b=$v;;
		    4)
		        r=$k3; g=$k1; b=$v;;
		    *)
		        r=$v; g=$k1; b=$k2;;
		esac
	fi

    (( r = r * 255 / w ))
    (( g = g * 255 / w ))
    (( b= b * 255 / w ))
    #echo -n $r,$g,$b
    num2hex $r; num2hex $g; num2hex $b

    echo -n "' "
    [ $((i % 8)) = 7 ] && echo
done
