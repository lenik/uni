@echo off

        set dir_t=%dir_t_home%

        if exist "%dir_t%\2\.cirkonstancoj" (
            set dir_cir=%dir_t%\2
            echo Found built-in cirkonstancoj
            goto cont
        )

        for %%i in (c d e f g h i j k l m n o p q r s t u v w x y z) do (
            if exist %%i:\.cirkonstancoj\.cirkonstancoj (
                set dir_cir=%%i:\.cirkonstancoj
                echo Found cirkonstancoj installed at %dir_cir%
                goto cont
            )
        )

        goto end

:cont
    %dir_cir%\scheme\plt\mzscheme "%1" %*
