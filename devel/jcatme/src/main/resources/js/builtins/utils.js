provideCommands({
    "error*":
        function(opts, args) {
            if (args == null) throw "null args";
            var msg = args.join(' ');
            logger.error(msg);
        },
    
    "eval*":
        function(opts, args) {
            if (opts == null) throw "null opts";
            if (args == null) throw "null args";
            var code = args.join(' ');
            eval(code);
        }
});

