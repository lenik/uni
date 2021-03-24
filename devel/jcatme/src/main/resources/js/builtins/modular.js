function include(res, args) {
    if (res == null)
        throw "null resource.";
    var child = frame.createChildFrame(res);
    // child.args = args;
    child.parse();
}

provideCommands({
    "include*": 
        function(opts, href, args) {
            let res = frame.resolveHref(href);
            if (res == null)
                throw "Can't resolve href " + href;
            include(res, args);
        },
        
    "sinclude*":
        function(opts, href, args) {
            let res = frame.resolveHref(href);
            if (res != null)
                include(res, args);
        },
        
    "mixin*": 
        function(opts, fqn, args) {
            let res = frame.resolveQName(fqn);
            if (res == null)
                throw "Can't resolve name " + fqn;
            include(res, args);
        },
        
    "import*":
        function(opts, fqn, args) {
            if (parser.imported.contains(fqn)) {
                // console.debug("already imported: " + fqn);
                return;
            }
            parser.imported.add(fqn);
            let res = frame.resolveQName(fqn);
            if (res == null)
                throw "Can't resolve name " + fqn;
            include(res, args);
        },
    
    "eof":
        function(opts, args) {
            parser.stop();
        }
});
