
export function resolveChild(parent: HTMLElement,
    childClassOrId: string,
    pos?: HTMLElement | boolean | unknown,
    tagName: string = 'div'): HTMLElement {

    const className = childClassOrId.startsWith(".") ? childClassOrId.substring(1) : undefined;
    const id = childClassOrId.startsWith("#") ? childClassOrId.substring(1) : undefined;
    if (className == null && id == null)
        throw "invalid classOrId: either #class or #id must be specified.";

    const n = parent.children.length;
    for (let i = 0; i < n; i++) {
        const child = parent.children[i];
        if (className != null) {
            let classList = child.classList;
            if (classList.contains(className))
                return child as HTMLElement;
        }
        if (id != null) {
            if (child.id == id)
                return child as HTMLElement;
        }
    }
    // not found.
    let element = document.createElement(tagName);
    if (className != null)
        element.classList.add(className);
    if (id != null)
        element.id = id;

    S: switch (pos) {
        case undefined:
            parent.appendChild(element);
            break;

        case true:
        case false:
            if (id == null)
                throw "insert ordered by id, but id isn't specified.";

            const ascending = pos as boolean;

            for (let i = 0; i < n; i++) {
                const sibling = parent.children[i];
                if (className != null) {
                    let classList = sibling.classList;
                    if (!classList.contains(className))
                        continue;
                }
                if (ascending == (sibling.id >= id)) {
                    parent.insertBefore(sibling, element);
                    break S;
                }
            }
            if (ascending || n == 0)
                parent.appendChild(element);
            else
                parent.insertBefore(parent.children[0], element);
            break;

        default: // insert-before
            let before = pos as HTMLElement;
            parent.insertBefore(before, element);
    }

    return element;
}
