provideCommands({
    "begin/$@": // filter
        function(opts, filterName, args) {
        },
    
    "end/$@":  // endfilter
        function(opts, filterName, args) {
        },
        
    "map/*":
        function(opts, code) {
            var code = args.join(' ');
            var fn = eval(code);
            var name = 'anonymous';
            Frame.addFilter(anonymous, fn);
        }
});

provideFilters({
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
});

