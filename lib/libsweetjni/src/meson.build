
# add_languages('java', required: true)
jni_dep = dependency('jni', required: false)
#have_jni = cpp.has_header('jni.h', dependencies: jni_dep, args: extra_include_args)

r = run_command('find', '-name', '*.cxx', check: true)
sources = r.stdout().splitlines()
#message(sources)
#
libsweetjni = both_libraries(
    'sweetjni', sources,
    dependencies: [ jni_dep ],
    install: true
)

#subdir('java/lang')

r = run_command('find', '-name', '*.hxx', check: true)
headers = r.stdout().splitlines()

#install_headers('sweetjni.hxx')

install_headers(
    headers,
    preserve_path: true
)

pkg = import('pkgconfig')
pkg.generate(libsweetjni)

