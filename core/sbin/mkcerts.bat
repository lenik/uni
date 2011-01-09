@echo off

    setlocal

    if "%~1"=="clean" goto clean

    REM 10 years
    set CERT_OPTS=-days 3650

rem CA Root (/)

	for %%i in (*.about) do (
		rem type %%i

		set C_NAME=%%~ni

		if exist "!C_NAME!.pre.bat" call "!C_NAME!.pre.bat"

		rem 1) PRIVATE KEY
		rem ------------------------------------------------------------------
			rem generate 4k rand file
				if not exist "!C_NAME!.rand" (
					echo [RAND] !C_NAME!
					openssl rand -out "!C_NAME!.rand" 4096
					if exist "!C_NAME!.private.pem" del "!C_NAME!.private.pem"
				)

			rem generate private key, use [RSA 4096 bits]
				if not exist "!C_NAME!.private.pem" (
					echo [PRIV] !C_NAME!
					openssl genrsa -rand "!C_NAME!.rand" -out "!C_NAME!.private.pem" 4096
					if exist "!C_NAME!.private.prot.pem" del "!C_NAME!.private.prot.pem"
					if exist "!C_NAME!.private.pvk" del "!C_NAME!.private.pvk"
					if exist "!C_NAME!.private.pk8" del "!C_NAME!.private.pk8"
					if exist "!C_NAME!.csr" del "!C_NAME!.csr"
				)

			rem protect the private key, use [aes256] and [.phrase file]
				if not exist "!C_NAME!.private.prot.pem" (
					echo [PRIV.PWD] !C_NAME!
					openssl rsa -in "!C_NAME!.private.pem" -out "!C_NAME!.private.prot.pem" -aes256 -passout "file:!C_NAME!.phrase"
				)

			rem convert to PVK format
				if not exist "!C_NAME!.private.pvk" (
					echo [PRIV.PVK] !C_NAME!
					pvk -in "!C_NAME!.private.pem" -strong -out "!C_NAME!.private.pvk" -topvk -nocrypt
				)

			rem convert to PKCS#8
				if not exist "!C_NAME!.private.pk8" (
					echo [PKCS#8] !C_NAME!
					openssl pkcs8 -nocrypt -in "!C_NAME!.private.pem" -topk8 -out "!C_NAME!.private.pk8"
				)


		rem 2) CERTIFICATE
		rem ------------------------------------------------------------------
			rem request to be certificated, [self]
				if not exist "!C_NAME!.csr" (
					echo [REQ] !C_NAME!
					openssl req -new -out "!C_NAME!.csr" -key "!C_NAME!.private.pem" -config "!C_NAME!.config"

					rem re-build following:
					if exist "!C_NAME!.crt" del "!C_NAME!.crt"
					if exist "!C_NAME!.spc" del "!C_NAME!.spc"
					if exist "!C_NAME!.p12" del "!C_NAME!.p12"
					for %%q in (*.req) do (
						if exist "%%~nq.crt" del "%%~nq.crt"
					)
					if exist "!C_NAME!" (
						pushd "!C_NAME!"
							for %%e in (*.about) do (
								if exist "%%~ne.csr" del "%%~ne.csr"
							)
						popd
					)
				)

			rem do certificate (depends on whether this is a root-cert or a sub-certs)
				if not exist "!C_NAME!.crt" (
					if "%1"=="" (
						rem certificate by [self], expired after [DEF_EXPIRE days]
						echo [CERT.SELF] !C_NAME!
						openssl x509 -req -in "!C_NAME!.csr" -out "!C_NAME!.crt" -signkey "!C_NAME!.private.pem" %CERT_OPTS%
						copy /y "!C_NAME!.crt" "!C_NAME!.cert[self].pem" >nul
					) else (
						rem certificate by [..\%1], expired after [DEF_EXPIRE days]
						echo [CERT.BY] !C_NAME! /By %1
						openssl x509 -req -in "!C_NAME!.csr" -out "!C_NAME!.crt" -signkey "!C_NAME!.private.pem" -CA "..\%1.crt" -CAkey "..\%1.private.pem" -CAcreateserial %CERT_OPTS%
						copy /y "!C_NAME!.crt" "!C_NAME!.cert[%1].pem" >nul
					)
				)

			rem convert to PKCS#7
				if not exist "!C_NAME!.spc" (
					echo [PKCS#7] !C_NAME!
					openssl crl2pkcs7 -nocrl -certfile "!C_NAME!.crt" -outform DER -out "!C_NAME!.spc"
				)

			rem convert to PKCS#12, protect by .(dot) and private passphrase
				if not exist "!C_NAME!.p12" (
					echo [PKCS#12] !C_NAME!
					openssl pkcs12 -export -in "!C_NAME!.crt" -inkey "!C_NAME!.private.pem" -out "!C_NAME!.p12" -name "PKCS#12 !C_NAME!" -passout "pass:."
					openssl pkcs12 -export -in "!C_NAME!.crt" -inkey "!C_NAME!.private.pem" -out "!C_NAME!.prot.p12" -name "PKCS#12 !C_NAME!" -passout "file:!C_NAME!.phrase"
				)

			rem create shell utility scripts
				echo pvkimprt -import "!C_NAME!.spc" "!C_NAME!.private.pvk" >"!C_NAME!.pvkimprt.bat"

		rem 3) ISSUING
		rem ------------------------------------------------------------------
			rem scan requests to this
				echo [SCAN-REQ] !C_NAME!
				for %%q in (*.req) do (
					rem certificate by [this], expired after [365 days]
					if not exist "%%~nq.crt" (
						echo [CERT.TO] %%q
						openssl x509 -req -in "%%q" -out "%%~nq.crt" -CA "!C_NAME!.crt" -CAkey "!C_NAME!.private.pem" -CAcreateserial -days 365
						copy /y "%%~nq.crt" "%%~nq.cert[!C_NAME!].pem" >nul
					)
				)


			rem scan sub-certs
				if exist "!C_NAME!" (
					echo [SUB-CERT] For !C_NAME!

					rem for non-reentrant .cmd script, re-init the var before call self.
					set C_NAME=%%~ni

					rem if not exist "%%d\%~nx0"
					copy "%~dpnx0" "!C_NAME!" >nul

					pushd "!C_NAME!"
					call "%~nx0" !C_NAME!
					del "%~nx0"
					popd

				)

		rem run .more script at last
			if exist "!C_NAME!.post.bat" call "!C_NAME!.post.bat"

	)


rem collect all certificates, for root call.
	if "%1"=="" (
		echo [COLLECT]
		if not exist all-certs (
			md all-certs
			type nul >all-certs\all-certs.id
		)

		set _CERT_COUNT=0
		for /r %%c in (*.crt) do (
			if not exist "%%~dpcall-certs.id" (
				echo [RESULT] %%c
				copy "%%c" "all-certs" >nul
				set /a _CERT_COUNT=!_CERT_COUNT!+1
			)
		)

		echo total !_CERT_COUNT! certificates created.
	)


rem clean after build, for root call
	rem if "%1"=="" goto clean
    exit /b


:clean
	for %%i in (*.about) do (
		set C_NAME=%%~ni

		for %%j in ("!C_NAME!.*") do (
			set _SKIP=0
			if "%%~xj"==".about" set _SKIP=1
			if "%%~xj"==".config" set _SKIP=1
			if "%%~xj"==".phrase" set _SKIP=1
			if "%%~xj"==".rand" set _SKIP=1
			if "%%j"=="!C_NAME!.private.pem" set _SKIP=1
			if !_SKIP!==0 (
				echo [REMOVE] %%j
				del %%j
			)
		)

		rem scan sub-directories
			for /d %%d in (*) do (
				if not "%%d"=="all-certs" (
					echo [SCAN-SUB] %%d

					rem for non-reentrant .cmd script, re-init the var before call self.
					set C_NAME=%%~ni

					rem if not exist "%%d\%~nx0"
					copy "%~dpnx0" "%%d" >nul

					pushd "%%d"
					call "%~nx0" clean !C_NAME!
					del "%~nx0"
					popd
				)
			)
	)
