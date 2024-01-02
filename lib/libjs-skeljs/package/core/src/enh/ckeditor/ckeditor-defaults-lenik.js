export default {
    toolbar: {
        items: [
            'heading',
            '|',
            'fontFamily',
            'fontSize',
            'alignment',
            'bold',
            'italic',
            'link',
            'bulletedList',
            'numberedList',
            'imageUpload',
            'blockQuote',
            'insertTable',
            'mediaEmbed',
            'undo',
            'redo',
        ]
    },
    image: {
        toolbar: [
            'imageStyle:full',
            'imageStyle:side',
            '|',
            'imageTextAlternative'
        ]
    },
    table: {
        contentToolbar: [
            'tableColumn',
            'tableRow',
            'mergeTableCells'
        ]
    },
    fontFamily: {
        options: [
            'default',
            'Arial, Helvetica, sans-serif',
            'Courier New, Courier, monospace',
            'Georgia, serif',
            'Lucida Sans Unicode, Lucida Grande, sans-serif',
            'Tahoma, Geneva, sans-serif',
            'Times New Roman, Times, serif',
            'Trebuchet MS, Helvetica, sans-serif',
            'Verdana, Geneva, sans-serif'
        ]
    },
    fontSize: {
        options: [ 9, 11, 13, 'default', 17, 19, 21 ]
    },
    // This value must be kept in sync with the language defined in webpack.config.js.
    language: 'zh-cn'
};

