provideCommands({
    "dnl/*":
        function(opts, args) {
        },
        
    "error/*":
        function(opts, args) {
            if (args == null) throw "null args";
            var msg = args.join(' ');
            logger.error(msg);
        },
    
    "eval/*":
        function(opts, code) {
            if (opts == null) throw "null opts";
            if (code == null) throw "null code";
            eval(code);
        },
    
    "shell/@":
        function(opts, cmdarray) {
            Parser.shell(cmdarray);
        },
        
    "use/$@":
        function(opts, moduleName, args) {
            let path = Frame.resolveModule(moduleName);
            if (path == null)
                throw "Can't resolve module: " + moduleName;
            load(path);
        }
});

