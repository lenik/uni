export var model = {
    menu1: [
        {
            href: "../m-blank/index.html",
            iconfa: "fa-file-o",
            label: "空白页",
        },
        {
            href: "../m-lunar/index.html",
            iconfa: "fa-moon",
            label: "月历",
        },
    ],

    itemIm: function (item) {
        if (item.href == null)
            item.href = "/files/" + item.dir + "/" + item.name;
        return Url.alterHref(item.href);
    },
};
