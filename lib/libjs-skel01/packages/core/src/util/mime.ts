
const imageTypes = [
    'bmp',
    'gif',
    'heif',
    'j2c',
    'j2k',
    ['jp2', 'jp2', 'jpg2',],
    'jpc',
    'jpf',
    ['jpeg', 'jpg', 'jpeg',],
    'jpm',
    'jpx',
    'png',
    'svg',
    'webp',
];

const videoTypes = [
    '3g2',
    '3gp',
    'amv',
    'asf',
    'avi',
    'f4a',
    'f4b',
    'f4p',
    'f4v',
    'flv',
    'gifv',
    'm4p',
    'm4v',
    'mj2',
    'mkv',
    'mng',
    'mod',
    'mov',
    'mp2',
    'mp4',
    'mpe',
    ['mpeg', 'mpeg', 'mpg',],
    'mpv',
    'mxf',
    'nsv',
    'ogg',
    'ogv',
    'qt',
    'rm',
    'roq',
    'rrc',
    'svi',
    'vob',
    'webm',
    'wmv',
    'yuv',
];

const extensionMap = (() => {
    let map: any = {};

    function parse(cat: string, a: any) {
        if (typeof a == 'string') {
            map[a] = cat + '/' + a;
        } else {
            let [head, ...vals] = a;
            for (let v of vals)
                map[v] = cat + '/' + head;
        }
    }

    for (let n of imageTypes) parse('image', n);
    for (let n of videoTypes) parse('video', n);

    return map;
})();

export function contentTypeOfExtension(extension: string) {
    let type = extensionMap[extension];
    if (type != null) return type;
    return 'application/octet-stream';
}

export function isImageExt(ext: string) {
    let type = extensionMap[ext];
    if (type == null) return false;
    return type.startsWith('image/');
}

export function isVideoExt(ext: string) {
    let type = extensionMap[ext];
    if (type == null) return false;
    return type.startsWith('video/');
}

export function isImageName(filename: string) {
    let lastDot = filename.lastIndexOf('.');
    let ext = lastDot == -1 ? filename : filename.substring(lastDot + 1);
    return isImageExt(ext);
}

export function isVideoName(filename: string) {
    let lastDot = filename.lastIndexOf('.');
    let ext = lastDot == -1 ? filename : filename.substring(lastDot + 1);
    return isVideoExt(ext);
}
