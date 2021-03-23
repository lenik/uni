var CommandClass = Java.type("net.bodz.uni.catme.Command");

async function init() {
    let builtins = [
            'parser',
            'builtins/frame-control',
            'builtins/modular',
            'builtins/filter',
            'builtins/self-mod',
            'builtins/utils'
            ];
    for (let name of builtins) {
        let href = name + '.mjs';
        let mod = await import(href);
        console.log('setup ' + href);
        mod.setUp();
        console.log('    done ' + href);
        // console.log(JSON.stringify(mod, null, 4));
        for (const [k, fn] of Object.entries(mod.cmds)) {
            let cmd = new CommandClass();
            let greedy = k.endsWith("*");
            cmd.name = greedy ? k.substr(0, k.length - 1) : k;
            cmd.greedy = greedy;
            cmd.fn = fn;
            frame.addCommand(cmd);
        }
    }
    App.bindings.inited = true;
}

await init();


function extend() {
    let arg0 = arguments[0];
    for(let i = 1; i < arguments.length; i++) {
        let arg = arguments[i];
        for(let key in arg) {
            if (arg.hasOwnProperty == null || arg.hasOwnProperty(key))
                arg0[key] = arg[key];
        }
    }
    return arg0;
}

global.setParser = function(parser) {
    global.parser = parser;
    extend(parser, app._parser);
    extend(frame, app._frame);
}

