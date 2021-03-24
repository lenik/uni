load('js/wrappers.js');

function parseCommandLine() {
    let nEscape = frame.escape.length;
    var status;
    var cmd = null;
    var opts = [];
    var argsbuf = [];
    for (let i = 0; i < arguments.length; i++) {
        let arg = arguments[i];
        if (cmd == null) {
            if (! arg.startsWith(frame.escape))
                throw "Expected escape-seq(" + frame.escape + ") before " + arg;
            let name = arg.substr(nEscape);
            let pos = name.indexOf('(');
            if (pos != -1) {
                if (! name.endsWith(')'))
                    throw "Unmatched parenthesis: " + name;
                opts = name.substring(pos + 1, name.length - 1);
                opts = opts.split(/\s+/);
                name = name.substr(0, pos);
            }
            cmd = frame.getCommand(name);
            if (cmd == null)
                throw "Unknown command: " + name;
            if (! cmd.greedy) {
                cmd.fn(opts, []);
                cmd = null;
            }
            continue;
        }
        argsbuf.push(arg);
    }
    if (cmd != null) {
        let n = cmd.fn.length;
        let expand = n - 2;
        let list = [opts];
        let remains = [];
        for (let i = 0; i < expand; i++) {
            list.push(argsbuf[i]);
        }
        for (let i = expand; i < argsbuf.length; i++) {
            remains.push(argsbuf[i]);
        }
        list.push(remains);
        cmd.fn.apply(cmd.name, list);
    }
};

function provideCommands(cmds) {
    for (const [k, fn] of Object.entries(cmds))
        provideCommand(k, fn);
}

function provideCommand(k, fn) {
    let FnValueCommand = Java.type("net.bodz.uni.catme.js.FnValueCommand");
    let cmd = new FnValueCommand();
    let greedy = k.endsWith("*");
    let name = greedy ? k.substr(0, k.length - 1) : k;
    cmd.greedy = greedy;
    cmd.fn = fn;
    frame.addCommand(name, cmd);
}

function provideFilters(filters) {
    for (const [k, fn] of Object.entries(filters))
        provideFilter(k, fn);
}

function provideFilter(k, fn) {
    let FnValueFilter = Java.type("net.bodz.uni.catme.js.FnValueFilter");
    let filter = new FnValueFilter(fn);
    let name = k;
    frame.addFilter(name, filter);
}

load('js/builtins/frame-control.js');
load('js/builtins/modular.js');
load('js/builtins/filter.js');
load('js/builtins/self-mod.js');
load('js/builtins/utils.js');

MainParser.setCmdlineParser(parseCommandLine);

