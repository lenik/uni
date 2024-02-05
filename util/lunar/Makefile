
all: deploy

build:
	pnpm run-script build-only

deploy: build
	cd dist; \
	git add .; \
	git commit; \
	git push

