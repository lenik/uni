
// frame control

install({
    "echo": 
        function() { frame.echo = true; },
    
    "noecho": 
        function() { frame.echo = false; },
    
    "skip": 
        function(expr) {
            frame.skip = true; 
        },
    
    "noskip":
        function() { frame.skip = null; },
});

// inline template
install({
    "template":
        function(qName, args) {
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
        function(args) {
            frame.echo = false;
            frame.skip = false;
        }
});

// text stream
install({
    "include": 
        function(path, args) {
            include(path, args);
        },
        
    "sinclude":
        function(path, args) {
            if (pathExists(path))
                include(path, args);
        },
        
    "mixin": 
        function(fqn, args) {
            var path = resolveQName(fqn);
            include(path, args);
        },
        
    "import":
        function(fqn, args) {
            if (parser.imported.contains(fqn))
                return;
            var path = resolveQName(fqn);
            include(path, args);
        },
    
    "eof":
        function(args) {
            parser.stop();
        }
});

// utils
install({
    "error":
        function(args) {
            logger.error(args);
        }
});

// filter framework
install({
    "begin": // filter
        function(args) {
        },
    
    "end":  // endfilter
        function(args) {
        },
        
    "map":
        function(args) {
            var fn = eval(args);
            frame.addFilter(fn);
        }
});

installFilters({
    "toupper":
        function(s) { return s.toUpper(); },
    "tolower":
        function(s) { return s.toLower(); },
    "nop":
        function(s) { return s; }
});

\map function(s) { return s.toUpper(); }

function indent(level) {
    return function(s) {
        return level + s;
    }
}

function tabular(stops) {
    return function(s) {
        return s;
    }
}

