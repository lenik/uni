
var nav: any = navigator;
if (nav.getBattery != undefined)
    nav.getBattery().then(function(batteryManager: any) {
        let win: any = window;
        win.batteryManager = batteryManager;
    });
