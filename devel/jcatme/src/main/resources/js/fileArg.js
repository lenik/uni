load('js/wrappers.js');

function cmdDisp(cmd, optv, args) {
    let params = [ optv ];
    for (let arg of args)
        params.push(arg);
    cmd.execute(params);
}
Parser.setCommandDispatcher(cmdDisp);

function provideCommands(cmds) {
    for (const [k, fn] of Object.entries(cmds))
        provideCommand(k, fn);
}

function provideCommand(k, fn) {
    let FnValueCommand = Java.type("net.bodz.uni.catme.js.FnValueCommand");
    let pos = k.indexOf("/");
    let name = pos == -1 ? k : k.substring(0, pos);
    let flags = pos == -1 ? "" : k.substring(pos + 1);
    let cmd = new FnValueCommand(flags, fn);
    Frame.addCommand(name, cmd);
}

function provideFilters(filters) {
    for (const [k, fn] of Object.entries(filters))
        provideFilter(k, fn);
}

function provideFilter(k, fn) {
    let FnValueFilter = Java.type("net.bodz.uni.catme.js.FnValueFilter");
    let filter = new FnValueFilter(fn);
    let name = k;
    Frame.addFilter(name, filter);
}

load('js/builtins/frame-control.js');
load('js/builtins/modular.js');
load('js/builtins/filter.js');
load('js/builtins/self-mod.js');
load('js/builtins/utils.js');

