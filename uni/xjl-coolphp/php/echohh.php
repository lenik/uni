<?php

    @session_start();

    function dumpMap($name, $map) {
        echo "  <$name>\n";
        foreach ($map as $k => $v) {
            $ks = @htmlspecialchars($k);
            $vs = @htmlspecialchars($v);
            echo "    <item key=\"$ks\">$vs</item>\n";
        }
        echo "  </$name>\n";
    }

    header("Content-Type: text/xml");

    echo "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n";
?>

<!-- A sample implementation for echohh, in php way -->
<!-- $Id$ -->
<!DOCTYPE echohh [
	<!ELEMENT echohh (env?, server?, request?, session?, user?)>
	<!ELEMENT env (item*)>
	<!ELEMENT server (item*)>
	<!ELEMENT request (item*)>
	<!ELEMENT session (item*)>
	<!ELEMENT user (item*)>
	<!ELEMENT item (#PCDATA)>
	<!ATTLIST item key CDATA #REQUIRED>
	<!ATTLIST item value CDATA #IMPLIED>
]>
<echohh>
    <?php dumpMap('env', $_ENV); ?>
    <?php dumpMap('server', $_SERVER); ?>
    <?php dumpMap('request', $_REQUEST); ?>
    <?php dumpMap('session', $_SESSION); ?>
</echohh>
