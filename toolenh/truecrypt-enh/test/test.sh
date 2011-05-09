#!/bin/sh

make fstab-install.tc passwd-install.tc

lineconf fstab-install.tc  fstab_tc  fstab_tc=fstab.tc
lineconf passwd-install.tc passwd_tc passwd_tc=passwd.tc

chmod a+x fstab-install.tc passwd-install.tc

# add
cp fstab.tc.0 fstab.tc
cp passwd.tc.0 passwd.tc

../fstab-install.tc  -vf fstab-delta
../passwd-install.tc -vf passwd-delta

mv fstab.tc  fstab-vf
mv passwd.tc passwd-vf

echo "Add fstab -vf: "
diff -u fstab.tc.0  fstab-vf  | sed -e 's/^/    /'
echo "Add passwd -vf: "
diff -u passwd.tc.0 passwd-vf | sed -e 's/^/    /'

# remove
cp fstab.tc.0 fstab.tc
cp passwd.tc.0 passwd.tc

../fstab-install.tc  -vfu fstab-delta
../passwd-install.tc -vfu passwd-delta

mv fstab.tc  fstab-vfu
mv passwd.tc passwd-vfu

echo "Remove fstab -vfu: "
diff -u fstab.tc.0  fstab-vfu  | sed -e 's/^/    /'
echo "Remove passwd -vfu: "
diff -u passwd.tc.0 passwd-vfu | sed -e 's/^/    /'
