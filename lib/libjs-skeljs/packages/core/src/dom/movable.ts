import $ from 'jquery';

export function makeMovable(element: HTMLElement, handle?: HTMLElement) {
    if (handle == null) handle = element;
    element = $(element)[0];
    handle = $(handle)[0];
    if (element == null || handle == null) return;

    let oldLeft: number;
    let oldTop: number;

    function moveElement(e: MouseEvent) {
        let left = e.clientX - oldLeft;
        let top = e.clientY - oldTop;

        let useRight = false;
        if (useRight) {
            let width = element.offsetWidth;
            let absRight = left + width;

            let parentWidth = element.parentElement!.clientWidth;
            let rightToParent = parentWidth - absRight;

            element.style.right = rightToParent + 'px';
        } else {
            element.style.left = left + 'px';
        }

        element.style.top = top + 'px';
    }

    handle.addEventListener('mousedown', (e: MouseEvent) => {
        e.preventDefault();
        oldLeft = e.clientX - element.offsetLeft;
        oldTop = e.clientY - element.offsetTop;

        // get rid of right positioning.
        let style = element.computedStyleMap();
        let boxSizing = style.get('box-sizing');

        let styleWidth = element.offsetWidth;
        if (boxSizing == 'content-box') {
            let bl = style.get('border-left-width') as CSSUnitValue;
            let br = style.get('border-right-width') as CSSUnitValue;
            let bw = bl.add(br).to('px').value;
            styleWidth -= bw;
        }
        element.style.width = styleWidth + 'px';
        window.addEventListener('mousemove', moveElement, false);
    });

    handle.addEventListener('mouseup', (e: MouseEvent) => {
        window.removeEventListener('mousemove', moveElement, false);
    });

}
