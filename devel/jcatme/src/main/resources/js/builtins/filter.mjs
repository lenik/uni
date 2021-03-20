export var cmds = {
    "begin*": // filter
        function(opts, filterName, args) {
        },
    
    "end":  // endfilter
        function(opts, filterName, args) {
        },
        
    "map*":
        function(opts, argsAsCode) {
            var code = argsAsCode.join(' ');
            var fn = eval(code);
            var name = 'anonymous';
            frame.addFilter(anonymous, fn);
        }
};

export var filters = {
    "toupper":
        function(s) { return s.toUpper(); },
        
    "tolower":
        function(s) { return s.toLower(); },
        
    "nop":
        function(s) { return s; },
    
    "indent":
        function(s, level, args) {
            return level + s;
        },
        
    "tabular": 
        function(s, stops, args) {
            return s;
        }
};

