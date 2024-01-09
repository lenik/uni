
interface RouteItem {
    path: string
    component: any
}

export function fs2Routes(root: any[], rootPath?: string): RouteItem[] {
    let items: RouteItem[] = [];
    convert(items, rootPath || '/', root);
    return items;
}

function convert(items: RouteItem[], path: string, node: any[]) {
    let [val, children] = node;

    let item = {
        path: path,
        component: val
    };
    items.push(item);

    if (path.endsWith('/'))
        path = path.substring(0, path.length - 1);

    if (children != null)
        for (let k in children) {
            let child = children[k];
            let childPath = path + '/' + k;
            convert(items, childPath, child);
        }
}