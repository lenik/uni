export var cmds = {
    "error*":
        function(opts, args) {
            var msg = args.join(' ');
            logger.error(msg);
        },
    
    "eval":
        function(opts, args) {
            console.log("opts: " + opts);
            console.log("args: " + args);
            
            var code = args.join(' ');
            eval(code);
        }
};

