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
    get cmdlinef()      { return MainParser.getCmdlineParser() },
    set cmdlinef(val)   {        MainParser.setCmdlineParser(val) },
    
    get out()           { return MainParser.getOut() }
};

export var out = parser.out;

export var frame = {
    get echo()          { return Frame.getEchoLines() },
    set echo(val) {
        if (typeof val == 'boolean')
            val = val ? 1 : 0;
        Frame.setEchoLines(val);
    },
    
    get skip()          { return Frame.getSkipLines() },
    set skip(val)       {        Frame.setSkipLines(val) },

    get skip2()         { return Frame.getSkipToPattern() },
    set skip2(val)      {        Frame.setSkipToPattern(val) },
    
    get nfilter()       { return Frame.getFilterCount() },
    
    get path()          { return Frame.getPath() },
    get dir()           { return Frame.getDirName() },
    get base()          { return Frame.getBaseName() },
    get ext()           { return Frame.getExtension() },
    get dotExt()        { return Frame.getExtensionWithDot() },
    
    get closest()       { return Frame.getClosestFileFrame() },
    
    get lang()          { return Frame.getLang() },
    
    get opener()        { return Frame.getOpener() },
    set opener(val)     {        Frame.setOpener(val) },
    
    get closer()        { return Frame.getCloser() },
    set closer(val)     {        Frame.setCloser(val) },
    
    get slopener()      { return Frame.getSimpleOpener() },
    set slopener(val)   {        Frame.setSimpleOpener(val) },

    get escape()        { return Frame.getEscapePrefix() },
    set escape(val)     {        Frame.setEscapePrefix(val) }
};

$.extend(parser, MainParser);
$.extend(frame, Frame);

export var cmds = {};
export var filters = {};

