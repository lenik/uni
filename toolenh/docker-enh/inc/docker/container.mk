SHELL = /bin/bash

GUEST_SHELL = bash -i
REPO = localhost:5000

DOCKER_RUN_OPTS = --dns 192.168.201.178

# ID: container id
IMAGE = $(ID)
VERSION = latest

build: prebuild
	docker build \
	    --rm=false \
	    -t $(IMAGE):$(VERSION) \
	    .
	if [ -x postbuild.sh ]; then ./postbuild.sh; fi

prebuild:
	if [ -x prebuild.sh ]; then ./prebuild.sh; fi

run: -run-docker -tty-cleanup

-tty-cleanup:
	@settermtitle "IMG $${PWD##*/}"

start:
	docker start $(ID)

stop:
	docker stop $(ID)

rm: stop
	docker rm $(ID)

shell: -shell -tty-cleanup

-shell:
	@docker exec -it $(ID) $(GUEST_SHELL)

addr:
	@_hostip=$$(ip route get 1 | awk '{print $$NF;exit}'); \
	IFS=: read _ip _port < <(docker port $(ID) 1521); \
	echo $$_hostip:$$_port

ip-int:
	@docker inspect --format '{{.NetworkSettings.IPAddress}}' $(ID) 
	#@IFS=: read _IPAddress _ip < \
	#    <(docker inspect $(ID) |grep '"IPAddress"'); \
	#_ip=$${_ip//[ \",]/}; \

