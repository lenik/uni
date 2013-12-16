#!/usr/bin/dprog -vig.

import std.stdio;
import lenik.bas.log;                   /* @link */
import lenik.bas.esc.csisgr;            /* @link */

mixin Log;

void main() {
    log.emerg("Emerg");
    log.alert("Alert");
    log.crit("Crit");
    log.err("Error %s", "test");
    log.warn("Warn");
    log.notice("Notice");
    log.info("Info");
    log.dbg("Debug");
    log.trace("Trace");
}
