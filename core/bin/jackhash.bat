@echo off
if not "%1"=="" goto cont
echo - jackhash.bat passwordfilename.ext
goto end
:cont
jpp -gecos:5 -lower %1 | jack -stdin %1
jpp -gecos:5 %1 | jack -stdin %1
jpp -gecos:1 -dot:0 %1 | jpp -translate:.1 | jack -stdin %1
jpp -gecos:1 -dot:7 %1 | jpp -translate:.1 | jack -stdin %1
jpp -gecos:1 -lower -dot:0 %1 | jpp -translate:.1 | jack -stdin %1
jpp -gecos:1 -lower -dot:7 %1 | jpp -translate:.1 | jack -stdin %1
jpp -gecos:1 -dot:0 %1 | jpp -translate:.2 | jack -stdin %1
jpp -gecos:1 -dot:7 %1 | jpp -translate:.2 | jack -stdin %1
jpp -gecos:1 -lower -dot:0 %1 | jpp -translate:.2 | jack -stdin %1
jpp -gecos:1 -lower -dot:7 %1 | jpp -translate:.2 | jack -stdin %1
jpp -gecos:1 -dot:0 %1 | jpp -translate:.3 | jack -stdin %1
jpp -gecos:1 -dot:7 %1 | jpp -translate:.3 | jack -stdin %1
jpp -gecos:1 -lower -dot:0 %1 | jpp -translate:.3 | jack -stdin %1
jpp -gecos:1 -lower -dot:7 %1 | jpp -translate:.3 | jack -stdin %1
jpp -gecos:1 -dot:0 %1 | jpp -translate:.4 | jack -stdin %1
jpp -gecos:1 -dot:7 %1 | jpp -translate:.4 | jack -stdin %1
jpp -gecos:1 -lower -dot:0 %1 | jpp -translate:.4 | jack -stdin %1
jpp -gecos:1 -lower -dot:7 %1 | jpp -translate:.4 | jack -stdin %1
jpp -gecos:1 -dot:0 %1 | jpp -translate:.5 | jack -stdin %1
jpp -gecos:1 -dot:7 %1 | jpp -translate:.5 | jack -stdin %1
jpp -gecos:1 -lower -dot:0 %1 | jpp -translate:.5 | jack -stdin %1
jpp -gecos:1 -lower -dot:7 %1 | jpp -translate:.5 | jack -stdin %1
jpp -gecos:1 -dot:0 %1 | jpp -translate:.6 | jack -stdin %1
jpp -gecos:1 -dot:7 %1 | jpp -translate:.6 | jack -stdin %1
jpp -gecos:1 -lower -dot:0 %1 | jpp -translate:.6 | jack -stdin %1
jpp -gecos:1 -lower -dot:7 %1 | jpp -translate:.6 | jack -stdin %1
jpp -gecos:1 -dot:0 %1 | jpp -translate:.7 | jack -stdin %1
jpp -gecos:1 -dot:7 %1 | jpp -translate:.7 | jack -stdin %1
jpp -gecos:1 -lower -dot:0 %1 | jpp -translate:.7 | jack -stdin %1
jpp -gecos:1 -lower -dot:7 %1 | jpp -translate:.7 | jack -stdin %1
jpp -gecos:1 -dot:0 %1 | jpp -translate:.8 | jack -stdin %1
jpp -gecos:1 -dot:7 %1 | jpp -translate:.8 | jack -stdin %1
jpp -gecos:1 -lower -dot:0 %1 | jpp -translate:.8 | jack -stdin %1
jpp -gecos:1 -lower -dot:7 %1 | jpp -translate:.8 | jack -stdin %1
jpp -gecos:1 -dot:0 %1 | jpp -translate:.9 | jack -stdin %1
jpp -gecos:1 -dot:7 %1 | jpp -translate:.9 | jack -stdin %1
jpp -gecos:1 -lower -dot:0 %1 | jpp -translate:.9 | jack -stdin %1
jpp -gecos:1 -lower -dot:7 %1 | jpp -translate:.9 | jack -stdin %1
jpp -gecos:1 -dot:0 %1 | jpp -translate:.0 | jack -stdin %1
jpp -gecos:1 -dot:7 %1 | jpp -translate:.0 | jack -stdin %1
jpp -gecos:1 -lower -dot:0 %1 | jpp -translate:.0 | jack -stdin %1
jpp -gecos:1 -lower -dot:7 %1 | jpp -translate:.0 | jack -stdin %1
jpp -gecos:1 -dot:0 %1 | jpp -translate:.` | jack -stdin %1
jpp -gecos:1 -dot:7 %1 | jpp -translate:.` | jack -stdin %1
jpp -gecos:1 -lower -dot:0 %1 | jpp -translate:.` | jack -stdin %1
jpp -gecos:1 -lower -dot:7 %1 | jpp -translate:.` | jack -stdin %1
jpp -gecos:1 -dot:0 %1 | jpp -translate:.~ | jack -stdin %1
jpp -gecos:1 -dot:7 %1 | jpp -translate:.~ | jack -stdin %1
jpp -gecos:1 -lower -dot:0 %1 | jpp -translate:.~ | jack -stdin %1
jpp -gecos:1 -lower -dot:7 %1 | jpp -translate:.~ | jack -stdin %1
jpp -gecos:1 -dot:0 %1 | jpp -translate:.! | jack -stdin %1
jpp -gecos:1 -dot:7 %1 | jpp -translate:.! | jack -stdin %1
jpp -gecos:1 -lower -dot:0 %1 | jpp -translate:.! | jack -stdin %1
jpp -gecos:1 -lower -dot:7 %1 | jpp -translate:.! | jack -stdin %1
jpp -gecos:1 -dot:0 %1 | jpp -translate:.A | jack -stdin %1
jpp -gecos:1 -dot:7 %1 | jpp -translate:.A | jack -stdin %1
jpp -gecos:1 -lower -dot:0 %1 | jpp -translate:.A | jack -stdin %1
jpp -gecos:1 -lower -dot:7 %1 | jpp -translate:.A | jack -stdin %1
jpp -gecos:1 -dot:0 %1 | jpp -translate:.a | jack -stdin %1
jpp -gecos:1 -dot:7 %1 | jpp -translate:.a | jack -stdin %1
jpp -gecos:1 -lower -dot:0 %1 | jpp -translate:.a | jack -stdin %1
jpp -gecos:1 -lower -dot:7 %1 | jpp -translate:.a | jack -stdin %1
jpp -gecos:1 -dot:0 %1 | jpp -translate:.q | jack -stdin %1
jpp -gecos:1 -dot:7 %1 | jpp -translate:.q | jack -stdin %1
jpp -gecos:1 -lower -dot:0 %1 | jpp -translate:.q | jack -stdin %1
jpp -gecos:1 -lower -dot:7 %1 | jpp -translate:.q | jack -stdin %1
jpp -gecos:2 -dot:0 %1 | jpp -translate:.1 | jack -stdin %1
jpp -gecos:2 -dot:7 %1 | jpp -translate:.1 | jack -stdin %1
jpp -gecos:2 -lower -dot:0 %1 | jpp -translate:.1 | jack -stdin %1
jpp -gecos:2 -lower -dot:7 %1 | jpp -translate:.1 | jack -stdin %1
jpp -gecos:2 -dot:0 %1 | jpp -translate:.2 | jack -stdin %1
jpp -gecos:2 -dot:7 %1 | jpp -translate:.2 | jack -stdin %1
jpp -gecos:2 -lower -dot:0 %1 | jpp -translate:.2 | jack -stdin %1
jpp -gecos:2 -lower -dot:7 %1 | jpp -translate:.2 | jack -stdin %1
jpp -gecos:2 -dot:0 %1 | jpp -translate:.3 | jack -stdin %1
jpp -gecos:2 -dot:7 %1 | jpp -translate:.3 | jack -stdin %1
jpp -gecos:2 -lower -dot:0 %1 | jpp -translate:.3 | jack -stdin %1
jpp -gecos:2 -lower -dot:7 %1 | jpp -translate:.3 | jack -stdin %1
jpp -gecos:2 -dot:0 %1 | jpp -translate:.4 | jack -stdin %1
jpp -gecos:2 -dot:7 %1 | jpp -translate:.4 | jack -stdin %1
jpp -gecos:2 -lower -dot:0 %1 | jpp -translate:.4 | jack -stdin %1
jpp -gecos:2 -lower -dot:7 %1 | jpp -translate:.4 | jack -stdin %1
jpp -gecos:2 -dot:0 %1 | jpp -translate:.5 | jack -stdin %1
jpp -gecos:2 -dot:7 %1 | jpp -translate:.5 | jack -stdin %1
jpp -gecos:2 -lower -dot:0 %1 | jpp -translate:.5 | jack -stdin %1
jpp -gecos:2 -lower -dot:7 %1 | jpp -translate:.5 | jack -stdin %1
jpp -gecos:2 -dot:0 %1 | jpp -translate:.6 | jack -stdin %1
jpp -gecos:2 -dot:7 %1 | jpp -translate:.6 | jack -stdin %1
jpp -gecos:2 -lower -dot:0 %1 | jpp -translate:.6 | jack -stdin %1
jpp -gecos:2 -lower -dot:7 %1 | jpp -translate:.6 | jack -stdin %1
jpp -gecos:2 -dot:0 %1 | jpp -translate:.7 | jack -stdin %1
jpp -gecos:2 -dot:7 %1 | jpp -translate:.7 | jack -stdin %1
jpp -gecos:2 -lower -dot:0 %1 | jpp -translate:.7 | jack -stdin %1
jpp -gecos:2 -lower -dot:7 %1 | jpp -translate:.7 | jack -stdin %1
jpp -gecos:2 -dot:0 %1 | jpp -translate:.8 | jack -stdin %1
jpp -gecos:2 -dot:7 %1 | jpp -translate:.8 | jack -stdin %1
jpp -gecos:2 -lower -dot:0 %1 | jpp -translate:.8 | jack -stdin %1
jpp -gecos:2 -lower -dot:7 %1 | jpp -translate:.8 | jack -stdin %1
jpp -gecos:2 -dot:0 %1 | jpp -translate:.9 | jack -stdin %1
jpp -gecos:2 -dot:7 %1 | jpp -translate:.9 | jack -stdin %1
jpp -gecos:2 -lower -dot:0 %1 | jpp -translate:.9 | jack -stdin %1
jpp -gecos:2 -lower -dot:7 %1 | jpp -translate:.9 | jack -stdin %1
jpp -gecos:2 -dot:0 %1 | jpp -translate:.0 | jack -stdin %1
jpp -gecos:2 -dot:7 %1 | jpp -translate:.0 | jack -stdin %1
jpp -gecos:2 -lower -dot:0 %1 | jpp -translate:.0 | jack -stdin %1
jpp -gecos:2 -lower -dot:7 %1 | jpp -translate:.0 | jack -stdin %1
jpp -gecos:4 -dot:0 %1 | jpp -translate:.1 | jack -stdin %1
jpp -gecos:4 -dot:7 %1 | jpp -translate:.1 | jack -stdin %1
jpp -gecos:4 -lower -dot:0 %1 | jpp -translate:.1 | jack -stdin %1
jpp -gecos:4 -lower -dot:7 %1 | jpp -translate:.1 | jack -stdin %1
jpp -gecos:4 -dot:0 %1 | jpp -translate:.2 | jack -stdin %1
jpp -gecos:4 -dot:7 %1 | jpp -translate:.2 | jack -stdin %1
jpp -gecos:4 -lower -dot:0 %1 | jpp -translate:.2 | jack -stdin %1
jpp -gecos:4 -lower -dot:7 %1 | jpp -translate:.2 | jack -stdin %1
jpp -gecos:4 -dot:0 %1 | jpp -translate:.3 | jack -stdin %1
jpp -gecos:4 -dot:7 %1 | jpp -translate:.3 | jack -stdin %1
jpp -gecos:4 -lower -dot:0 %1 | jpp -translate:.3 | jack -stdin %1
jpp -gecos:4 -lower -dot:7 %1 | jpp -translate:.3 | jack -stdin %1
jpp -gecos:4 -dot:0 %1 | jpp -translate:.4 | jack -stdin %1
jpp -gecos:4 -dot:7 %1 | jpp -translate:.4 | jack -stdin %1
jpp -gecos:4 -lower -dot:0 %1 | jpp -translate:.4 | jack -stdin %1
jpp -gecos:4 -lower -dot:7 %1 | jpp -translate:.4 | jack -stdin %1
jpp -gecos:4 -dot:0 %1 | jpp -translate:.5 | jack -stdin %1
jpp -gecos:4 -dot:7 %1 | jpp -translate:.5 | jack -stdin %1
jpp -gecos:4 -lower -dot:0 %1 | jpp -translate:.5 | jack -stdin %1
jpp -gecos:4 -lower -dot:7 %1 | jpp -translate:.5 | jack -stdin %1
jpp -gecos:4 -dot:0 %1 | jpp -translate:.6 | jack -stdin %1
jpp -gecos:4 -dot:7 %1 | jpp -translate:.6 | jack -stdin %1
jpp -gecos:4 -lower -dot:0 %1 | jpp -translate:.6 | jack -stdin %1
jpp -gecos:4 -lower -dot:7 %1 | jpp -translate:.6 | jack -stdin %1
jpp -gecos:4 -dot:0 %1 | jpp -translate:.7 | jack -stdin %1
jpp -gecos:4 -dot:7 %1 | jpp -translate:.7 | jack -stdin %1
jpp -gecos:4 -lower -dot:0 %1 | jpp -translate:.7 | jack -stdin %1
jpp -gecos:4 -lower -dot:7 %1 | jpp -translate:.7 | jack -stdin %1
jpp -gecos:4 -dot:0 %1 | jpp -translate:.8 | jack -stdin %1
jpp -gecos:4 -dot:7 %1 | jpp -translate:.8 | jack -stdin %1
jpp -gecos:4 -lower -dot:0 %1 | jpp -translate:.8 | jack -stdin %1
jpp -gecos:4 -lower -dot:7 %1 | jpp -translate:.8 | jack -stdin %1
jpp -gecos:4 -dot:0 %1 | jpp -translate:.9 | jack -stdin %1
jpp -gecos:4 -dot:7 %1 | jpp -translate:.9 | jack -stdin %1
jpp -gecos:4 -lower -dot:0 %1 | jpp -translate:.9 | jack -stdin %1
jpp -gecos:4 -lower -dot:7 %1 | jpp -translate:.9 | jack -stdin %1
jpp -gecos:4 -dot:0 %1 | jpp -translate:.0 | jack -stdin %1
jpp -gecos:4 -dot:7 %1 | jpp -translate:.0 | jack -stdin %1
jpp -gecos:4 -lower -dot:0 %1 | jpp -translate:.0 | jack -stdin %1
jpp -gecos:4 -lower -dot:7 %1 | jpp -translate:.0 | jack -stdin %1
jpp -gecos:8 -dot:0 %1 | jpp -translate:.1 | jack -stdin %1
jpp -gecos:8 -dot:7 %1 | jpp -translate:.1 | jack -stdin %1
jpp -gecos:8 -lower -dot:0 %1 | jpp -translate:.1 | jack -stdin %1
jpp -gecos:8 -lower -dot:7 %1 | jpp -translate:.1 | jack -stdin %1
jpp -gecos:8 -dot:0 %1 | jpp -translate:.2 | jack -stdin %1
jpp -gecos:8 -dot:7 %1 | jpp -translate:.2 | jack -stdin %1
jpp -gecos:8 -lower -dot:0 %1 | jpp -translate:.2 | jack -stdin %1
jpp -gecos:8 -lower -dot:7 %1 | jpp -translate:.2 | jack -stdin %1
jpp -gecos:8 -dot:0 %1 | jpp -translate:.3 | jack -stdin %1
jpp -gecos:8 -dot:7 %1 | jpp -translate:.3 | jack -stdin %1
jpp -gecos:8 -lower -dot:0 %1 | jpp -translate:.3 | jack -stdin %1
jpp -gecos:8 -lower -dot:7 %1 | jpp -translate:.3 | jack -stdin %1
jpp -gecos:8 -dot:0 %1 | jpp -translate:.4 | jack -stdin %1
jpp -gecos:8 -dot:7 %1 | jpp -translate:.4 | jack -stdin %1
jpp -gecos:8 -lower -dot:0 %1 | jpp -translate:.4 | jack -stdin %1
jpp -gecos:8 -lower -dot:7 %1 | jpp -translate:.4 | jack -stdin %1
jpp -gecos:8 -dot:0 %1 | jpp -translate:.5 | jack -stdin %1
jpp -gecos:8 -dot:7 %1 | jpp -translate:.5 | jack -stdin %1
jpp -gecos:8 -lower -dot:0 %1 | jpp -translate:.5 | jack -stdin %1
jpp -gecos:8 -lower -dot:7 %1 | jpp -translate:.5 | jack -stdin %1
jpp -gecos:8 -dot:0 %1 | jpp -translate:.6 | jack -stdin %1
jpp -gecos:8 -dot:7 %1 | jpp -translate:.6 | jack -stdin %1
jpp -gecos:8 -lower -dot:0 %1 | jpp -translate:.6 | jack -stdin %1
jpp -gecos:8 -lower -dot:7 %1 | jpp -translate:.6 | jack -stdin %1
jpp -gecos:8 -dot:0 %1 | jpp -translate:.7 | jack -stdin %1
jpp -gecos:8 -dot:7 %1 | jpp -translate:.7 | jack -stdin %1
jpp -gecos:8 -lower -dot:0 %1 | jpp -translate:.7 | jack -stdin %1
jpp -gecos:8 -lower -dot:7 %1 | jpp -translate:.7 | jack -stdin %1
jpp -gecos:8 -dot:0 %1 | jpp -translate:.8 | jack -stdin %1
jpp -gecos:8 -dot:7 %1 | jpp -translate:.8 | jack -stdin %1
jpp -gecos:8 -lower -dot:0 %1 | jpp -translate:.8 | jack -stdin %1
jpp -gecos:8 -lower -dot:7 %1 | jpp -translate:.8 | jack -stdin %1
jpp -gecos:8 -dot:0 %1 | jpp -translate:.9 | jack -stdin %1
jpp -gecos:8 -dot:7 %1 | jpp -translate:.9 | jack -stdin %1
jpp -gecos:8 -lower -dot:0 %1 | jpp -translate:.9 | jack -stdin %1
jpp -gecos:8 -lower -dot:7 %1 | jpp -translate:.9 | jack -stdin %1
jpp -gecos:8 -dot:0 %1 | jpp -translate:.0 | jack -stdin %1
jpp -gecos:8 -dot:7 %1 | jpp -translate:.0 | jack -stdin %1
jpp -gecos:8 -lower -dot:0 %1 | jpp -translate:.0 | jack -stdin %1
jpp -gecos:8 -lower -dot:7 %1 | jpp -translate:.0 | jack -stdin %1
:end
