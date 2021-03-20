export var cmds = {
    "error*":
        function(opts, args) {
            var msg = args.join(' ');
            logger.error(msg);
        }
};

