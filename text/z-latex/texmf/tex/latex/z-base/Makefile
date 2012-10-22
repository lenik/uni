
all: z-article.cls z-book.cls z-beamer.cls

%.cls: z-template.in
	for a in article book beamer; do \
	    sed -e s/\\\\CLASS/$$a/g <z-template.in >z-$$a.cls; \
	done
