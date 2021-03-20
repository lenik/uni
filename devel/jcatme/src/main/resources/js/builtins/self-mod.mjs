export var cmds = {
    "template":
        function(opts, qName, args) {
            var path = resolveQName(fqn);
            if (path == null) {
                logger.error("Not existed: " + fqn);
                return;
            }
            frame.echo = true;
            frame.skip = true;
            include(path, args);
        },
    
    "endtemplate":
        function(opts, args) {
            frame.echo = false;
            frame.skip = false;
        }
};

