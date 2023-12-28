
export function sleep(t, fn) {
    setTimeout(fn, t * 1000);
}

export function slowly(fn, msg) {
    beginWait();
    var afterJob = function() {
        endWait();
    };
    //sleep(.5, function() {
    if (fn.length) {
        return fn(afterJob);
    } else {
        var ret = fn();
        afterJob();
        return ret;
    }
    //});
}

export function beginWait(msg) {
    if (msg == null)
        msg = "Wait...";
    $("#waitbox .text").text(msg);
    $("#waitbox").show();
}

export function endWait() {
    $("#waitbox").hide();
}
