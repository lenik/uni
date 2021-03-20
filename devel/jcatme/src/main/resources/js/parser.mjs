Object.prototype.toJson = function() {
    return JSON.stringify(this, null, 4);
};

var $ = {
    extend: function(){
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
};

export var parser = {
    get lang()          { return MainParser.getLang() },
    
    get opener()        { return MainParser.getOpener() },
    set opener(val)     {        MainParser.setOpener(val) },
    
    get closer()        { return MainParser.getCloser() },
    set closer(val)     {        MainParser.setCloser(val) },
    
    get slopener()      { return MainParser.getSimpleOpener() },
    set slopener(val)   {        MainParser.setSimpleOpener(val) },

    get escape()        { return MainParser.getEscapePrefix() },
    set escape(val)     {        MainParser.setEscapePrefix(val) },
    
    get cmdlinef()      { return MainParser.getCmdlineParser() },
    set cmdlinef(val)   {        MainParser.setCmdlineParser(val) },
    
    get out()           { return MainParser.getOut() }
};

export var out = parser.out;

export var frame = {
    get echo()          { return Frame.getEcho() },
    set echo(val) {
        if (typeof val == 'boolean')
            val = val ? 1 : 0;
        Frame.setEcho(val);
    },
    
    get skip()          { return Frame.getSkip() },
    set skip(val)       {        Frame.setSkip(val) },

    get skip2()         { return Frame.getSkipToPattern() },
    set skip2(val)      {        Frame.setSkipToPattern(val) },
    
    get path()          { return Frame.getPath() },
    get dir()           { return Frame.getDirName() },
    get base()          { return Frame.getBaseName() },
    get ext()           { return Frame.getExtension() },
    get dotExt()        { return Frame.getExtensionWithDot() }
};

$.extend(parser, MainParser);
$.extend(frame, Frame);

export var cmds = {};
export var filters = {};

