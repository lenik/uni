#define LOG_IDENT "t-log"
#define LOG_OPTION (LOG_DATETIME | LOG_COLOR | LOG_LEVNAME)
#define LOG_LEVEL 100
#include <bas/log.h>

int main() {
    log_emerg("Emerg: the electricity is lost!");
    log_alert("Alert: earthquack is coming!");
    log_crit("Critical condition meet");
    log_err("Something wrong in the machine..");
    log_warn("You haven't saved your file..");
    log_notice("You have another mail.");
    log_info("It's not important, but you'd better check it.");
    log_debug("this.size is 189");
    log_trace("p * q = 123");
}
