
# add_languages('java', required: true)
jni_dep = dependency('jni', required: false)
#have_jni = cpp.has_header('jni.h', dependencies: jni_dep, args: extra_include_args)

libsweetjni = both_libraries(
    'sweetjni',
    [
        'java/lang/Object_class.cxx',
        'java/lang/Object.cxx',
        'java/lang/String_class.cxx',
        'java/lang/String.cxx',
        'java/lang/Throwable_class.cxx',
        'java/lang/Throwable.cxx',
        'sweetjni.cxx'
    ],
    dependencies: [ jni_dep ],
    install: true
)

subdir('java/lang')

#install_headers('sweetjni.hxx')

install_headers(
    'java/lang/Object_class.hxx',
    'java/lang/Object.hxx',
    'java/lang/String_class.hxx',
    'java/lang/String.hxx',
    'java/lang/Throwable_class.hxx',
    'java/lang/Throwable.hxx',
    'sweetjni.hxx',
    preserve_path: true
)

pkg = import('pkgconfig')
pkg.generate(libsweetjni)

