@echo off
ren %1 SwapTemp.$$$
ren %2 %1
ren SwapTemp.$$$ %2
echo swapped %1 / %2 .
