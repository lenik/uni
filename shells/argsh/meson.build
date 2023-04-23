project(
    'argsh', [ 'c', 'cpp' ],
    version: '0.0.1',
    license: 'GPL2+',
)

projver = meson.project_version()
ver = projver.split('.')

#projdir = meson.current_source_dir()
#destdir = meson.current_build_dir()

dep_incl = declare_dependency(
    include_directories: [
        include_directories('include'),
        include_directories('src'),
    ]
)

install_headers('include/lextoken.h')

subdir('src/argdom')
subdir('src/parser')
subdir('src/test')
