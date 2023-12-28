
export function buildRandomHSL(lightnessRange, saturationRange, hueRange) {
    if (lightnessRange == null) lightnessRange = [ 50, 90 ];
    if (saturationRange == null) saturationRange = [ 60, 60 ];
    if (hueRange == null) hueRange = [ 0, 360 ];

    const [ l0, l1 ] = lightnessRange;
    const [ s0, s1 ] = saturationRange;
    const [ h0, h1 ] = hueRange;

    var hue = Math.floor(Math.random() * (h1 - h0)) + h0;
    var sat = Math.floor(Math.random() * (s1 - s0)) + s0;
    var val = Math.floor(Math.random() * (l1 - l0)) + l0;
    return "hsl(" + hue + ", " + sat + "%, " + val +"%)";
}
