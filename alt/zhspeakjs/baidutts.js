#!/usr/bin/nodejs

    var https = require('https');
    var http = require('http');
    var lame = require('lame');
    var Speaker = require('speaker');
    var decoder = new lame.Decoder();
    var stream = require('stream');
    var exec = require('child_process').exec;

    var client_id = 'nGHECpU6Up5xP3BZ7z1Xnr92';
    var client_secret = 'NBk7z8s0evEiGOpwxSONSRC9PTwb8MqW';
    var cuid = "aaaaaaaa";

    var speaker = new Speaker();
    var text = "";
    process.stdin.setEncoding('utf8');
    process.stdin.on('readable', function() {
        const line = process.stdin.read();
        if (line != null)
            text += line;
    });
    
    var lan = "zh";
    var url = "https://openapi.baidu.com/oauth/2.0/token"
        + "?grant_type=client_credentials"
        + `&client_id=${client_id}`
        + `&client_secret=${client_secret}&`;
    https.get(url, function(resp) {
        data = '';
        resp.on('data', function(trunk) {
            data += trunk;
            console.log("token: ", data);
        });

        resp.on('end', function(trunk) {
            var json = JSON.parse(data);
            var tok = json['access_token'];
            
            process.stdin.on('end', function() {
                console.log("Text: ", text);
                var tex = encodeURIComponent(text);
                var soundUrl = "http://tsn.baidu.com/text2audio"
                    + `?tex=${tex}&lan=${lan}&cuid=${cuid}&ctp=1&tok=${tok}`;
                console.log("url: ", soundUrl);
                http.get(soundUrl, function(wavresp) {
                    var voice = '';
                    wavresp.pipe(decoder).pipe(speaker);
                });
            });
        });
    });

