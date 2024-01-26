
import $ from 'jquery';

type Callback = () => void;
type ManagedCloser = (callback: Callback) => void;

export function sleep(t: number, fn: Callback) {
    setTimeout(fn, t * 1000);
}

export function slowly(fn: Callback | ManagedCloser, msg: string = "Wait...") {
    beginWait(msg);
    var afterJob = endWait;
    //sleep(.5, function() {
    if (fn.length) {
        let fn1 = fn as ManagedCloser;
        return fn1(afterJob);
    } else {
        let fn0 = fn as Callback;
        var ret = fn0();
        afterJob();
        return ret;
    }
    //});
}

export function beginWait(msg: string = "Wait...") {
    $("#waitbox .text").text(msg);
    $("#waitbox").show();
}

export function endWait() {
    $("#waitbox").hide();
}
