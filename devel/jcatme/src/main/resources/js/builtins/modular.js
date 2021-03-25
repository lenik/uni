function include(res, args) {
    if (res == null)
        throw "null resource.";
    var child = Frame.createChildFrame(res);
    // child.args = args;
    child.parse();
}

provideCommands({
    "include/$@": 
        function(opts, href, args) {
            let res = Frame.resolveHref(href);
            if (res == null)
                throw "Can't resolve href " + href;
            include(res, args);
        },
        
    "sinclude/$@":
        function(opts, href, args) {
            let res = Frame.resolveHref(href);
            if (res != null)
                include(res, args);
        },
        
    "mixin/$@": 
        function(opts, fqn, args) {
            let res = Frame.resolveQName(fqn);
            if (res == null)
                throw "Can't resolve name " + fqn;
            include(res, args);
        },
        
    "import/$@":
        function(opts, fqn, args) {
            if (Parser.isImported(fqn))
                return;
            Parser.addImported(fqn);
            let res = Frame.resolveQName(fqn);
            if (res == null)
                throw "Can't resolve name " + fqn;
            include(res, args);
        },
    
    "eof":
        function(opts, args) {
            parser.stop();
        }
});
