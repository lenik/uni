export var cmds = {
    "echo": 
        function() { frame.echo = true; },
    
    "noecho": 
        function() { frame.echo = false; },
    
    "skip": 
        function(opts, args) {
            frame.skip = true; 
        },
    
    "noskip":
        function() { frame.skip = null; },
};

