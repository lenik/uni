import { parser, frame, out } from './parser.mjs';

var CommandClass = Java.type("net.bodz.uni.catme.Command");
var nEscape = parser.escape.length;

async function init() {
    let builtins = [
            'frame-control',
            'modular',
            'filter',
            'self-mod',
            'utils'
            ];
    for (let name of builtins) {
        let href = 'builtins/' + name + '.mjs';
        let mod = await import(href);
        // console.log(JSON.stringify(mod, null, 4));
        for (const [k, fn] of Object.entries(mod.cmds)) {
            let cmd = new CommandClass();
            let greedy = k.endsWith("*");
            cmd.name = greedy ? k.substr(0, k.length - 1) : k;
            cmd.greedy = greedy;
            cmd.fn = fn;
            frame.register(cmd);
        }
    }
}

await init();

parser.cmdlinef = function() {
    var status;
    var cmd = null;
    var opts = null;
    var argv = [];
    for (let i = 0; i < arguments.length; i++) {
        let arg = arguments[i];
        if (cmd == null) {
            if (! arg.startsWith(parser.escape))
                throw "Expected " + parser.escape + " before " + arg;
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
                cmd.fn(opts);
                cmd = null;
            }
            continue;
        }
        argv.push(arg);
    }
    if (cmd != null) {
        let n = cmd.fn.length;
        let expand = n - 2;
        let list = [opts];
        let remains = [];
        for (let i = 0; i < expand; i++) {
            list.push(argv[i]);
        }
        for (let i = expand; i < argv.length; i++) {
            remains.push(argv[i]);
        }
        list.push(remains);
        cmd.fn.apply(cmd.name, list);
    }
};

