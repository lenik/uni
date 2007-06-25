#!/usr/bin/perl

use strict;
use cmt::db_ora;

print "ora_home: " . ora_home . "\n";
print "get_tnsnames: " . join(',', ora_tnsnames) . "\n";
