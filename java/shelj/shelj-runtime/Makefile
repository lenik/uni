include coolmake/m2proj.mf

remotehost = a10
remotedir = cam19

bin_files += \
    src/

src_excludes += \
    Makefile \
    backup \
    doc \
    home

bin:
	cp -a /mnt/istore/pro/uni/java/m2proj/bin .

target-demo:
	cd target/classes/coin/cam19; \
	rm -fr web; \
	ln -snf ../../../../src/main/resources/coin/cam19/web .

target-demo2:
	cd target/classes/coin/cam19/web; \
	rm -fr js js1 css; \
	ln -snf ../../../../../src/main/resources/coin/cam19/web/*/ .

rsync-workingcopy:
	rsync -amv --delete . ${remotehost}:${remotedir}

work:
	@lsof-n ~/work/eclipse/job/.metadata/.lock | wc \
	    | (read n w c; if [ $$n == 0 ]; then eclipse & \
		else echo eclipse already started; fi )
	@ps fax | grep pgadmin3 | wc \
	    | (read n w c; if [ $$n -lt 4 ]; then pgadmin3 & \
		else echo pgadmin3 already started; fi )
	@ps fax | grep atom | wc \
	    | (read n w c; if [ $$n -lt 4 ]; then atom . & \
		else echo atom already started; fi )
	@echo "Don't forget to start scss auto builder."

lib-updater:
	mjava lib-updater

lib-updater-O:
	mjava -O lib-updater

run:
	screen main Cam19

debug:
	screen main -d Cam19

