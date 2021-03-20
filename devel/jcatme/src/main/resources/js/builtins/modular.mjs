import { parser, frame, out } from '../parser.mjs';

export var cmds = {
    "include*": 
        function(opts, path, args) {
            include(path, args);
        },
        
    "sinclude*":
        function(opts, path, args) {
            if (pathExists(path))
                include(path, args);
        },
        
    "mixin*": 
        function(opts, fqn, args) {
            var path = parser.resolveQName(fqn);
            include(path, args);
        },
        
    "import*":
        function(opts, fqn, args) {
            if (parser.imported.contains(fqn)) {
                console.log("already imported: " + fqn);
                return;
            }
            var path = parser.resolveQName(fqn);
            include(path, args);
        },
    
    "eof":
        function(opts, args) {
            parser.stop();
        }
};

function include(path, args) {
    parser.parseChild(Frame, path);
}

