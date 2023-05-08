include coolmake/m2proj.mf

sql-keywords.lst: sql-keywords.txt
	sed \
        -e s/$$'\u200b'//g \
        -e s/non-reserved/NR/ \
        "$<" \
    | grep reserved \
    | cut -f1 \
    | awk '{ print tolower($$0) }' >$@

copy-keywords: sql-keywords.lst
	cat "$<" \
    | sed -e 's/^/"/' -e 's/$$/",/' \
    | tr -d '\n' \
    | xsel -b

