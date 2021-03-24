Object.prototype.toJson = function() {
    return JSON.stringify(this, null, 4);
};

var app = {
    get _frame()         { return eval('Frame') },
    get _parser()        { return eval('MainParser') }
};

var parser = {
    get cmdlinef()      { return app._parser.getCmdlineParser() },
    set cmdlinef(val)   {        app._parser.setCmdlineParser(val) },
    
    get out()           { return app._parser.getOut() }
};

var frame = {
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

extend(frame, Frame);
extend(parser, MainParser);

