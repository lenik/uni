
//require Secure Context.

if (navigator.getGamepads) {
    var win: any = window;
    win.gamepads = navigator.getGamepads();
}
