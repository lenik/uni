
classes = \
    z-article.cls \
    z-book.cls \
    z-beamer.cls \
    z-extarticle.cls

all: $(classes)

z-%.cls: z-template.in
	sed -e s/\\\\CLASS/$*/g <z-template.in >z-$*.cls

