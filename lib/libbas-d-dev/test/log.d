#!/usr/bin/dprog -vig.

import std.stdio;
import lenik.bas.log;                   /* @link */
import lenik.bas.esc.csisgr;            /* @link */

mixin Log;

void main() {
    log.emerg("Emerg");
    log.alert("Alert");
    log.crit("Crit");
    log.enter();
        log.err(new Exception("something error"), "Error %s", "test");
        log.enter();
            log.warn("Warn");
            log.notice("Notice");
        log.leave();
        log.info("Info");
        log.dbg("Debug");
    log.leave();
    log.trace("Trace");
}
