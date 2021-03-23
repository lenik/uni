Object.prototype.toJson = function() {
    return JSON.stringify(this, null, 4);
};

export var app = {
    get _frame()         { return eval('Frame') },
    get _parser()        { return eval('MainParser') }
};

export var parser = {
    get cmdlinef()      { return app._parser.getCmdlineParser() },
    set cmdlinef(val)   {        app._parser.setCmdlineParser(val) },
    
    get out()           { return app._parser.getOut() }
};

export var frame = {
    get echo()          { return app._frame.getEchoLines() },
    set echo(val) {
        if (typeof val == 'boolean')
            val = val ? 1 : 0;
        app._frame.setEchoLines(val);
    },
    
    get skip()          { return app._frame.getSkipLines() },
    set skip(val)       {        app._frame.setSkipLines(val) },

    get skip2()         { return app._frame.getSkipToPattern() },
    set skip2(val)      {        app._frame.setSkipToPattern(val) },
    
    get nfilter()       { return app._frame.getFilterCount() },
    
    get path()          { return app._frame.getPath() },
    get dir()           { return app._frame.getDirName() },
    get base()          { return app._frame.getBaseName() },
    get ext()           { return app._frame.getExtension() },
    get dotExt()        { return app._frame.getExtensionWithDot() },
    
    get closest()       { return app._frame.getClosestFileFrame() },
    
    get lang()          { return app._frame.getLang() },
    
    get opener()        { return app._frame.getOpener() },
    set opener(val)     {        app._frame.setOpener(val) },
    
    get closer()        { return app._frame.getCloser() },
    set closer(val)     {        app._frame.setCloser(val) },
    
    get slopener()      { return app._frame.getSimpleOpener() },
    set slopener(val)   {        app._frame.setSimpleOpener(val) },

    get escape()        { return app._frame.getEscapePrefix() },
    set escape(val)     {        app._frame.setEscapePrefix(val) }
};

function parseCommandLine() {
    var status;
    var cmd = null;
    var opts = null;
    var argv = [];
    for (let i = 0; i < arguments.length; i++) {
        let arg = arguments[i];
        if (cmd == null) {
            if (! arg.startsWith(frame.escape))
                throw "Expected " + frame.escape + " before " + arg;
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

export default function setUp() {
    global.app = app;
    global.parser = parser;
    global.frame = frame;
    global.cmdlinef = parseCommandLine;
}

