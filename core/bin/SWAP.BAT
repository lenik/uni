@echo off
    rem $Id: SWAP.BAT,v 1.2 2004-09-22 08:39:07 dansei Exp $

    ren %1 SwapTemp.$$$
    ren %2 %1
    ren SwapTemp.$$$ %2
    echo swapped %1 / %2 .
