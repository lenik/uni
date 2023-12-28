
if (navigator.getBattery)
    navigator.getBattery().then(function(batteryManager) {
        window.batteryManager = batteryManager;
    });
