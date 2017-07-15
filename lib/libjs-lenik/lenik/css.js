function buildRandomHSL() {
    var hue = Math.floor(Math.random() * 360);
    var val = Math.floor(Math.random() * 40) + 50;
    return "hsl(" + hue + ", 60%, " + val +"%)";
}
