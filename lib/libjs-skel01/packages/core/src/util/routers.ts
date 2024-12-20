
interface IRoute {
    path: string,
    component: any
}

export function mergeRoutes(...routesList: IRoute[][]) {
    let merged: IRoute[] = [];
    let map: any = {};
    for (const routes of routesList) {
        for (const route of routes) {
            let { path, component } = route;
            if (map[path])
                continue;
            map[path] = 1;
            merged.push(route);
        }
    }
    return merged;
}
