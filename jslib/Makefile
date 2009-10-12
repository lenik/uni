
WGET=wget -q

jquery:
	mkdir -p jquery-1.3 jquery-trunk
	mkdir -p jqueryui-1.7
	$(WGET) -O jquery-1.3/jquery.min.js        http://ajax.googleapis.com/ajax/libs/jquery/1.3.2/jquery.min.js
	$(WGET) -O jquery-1.3/jquery.js            http://ajax.googleapis.com/ajax/libs/jquery/1.3.2/jquery.js
	$(WGET) -O jquery-trunk/jquery.min.js      http://code.jquery.com/jquery-nightly.min.js
	$(WGET) -O jquery-trunk/jquery.js          http://code.jquery.com/jquery-nightly.js
	$(WGET) -O jqueryui-1.7/jquery-ui.min.js   http://ajax.googleapis.com/ajax/libs/jqueryui/1.7.2/jquery-ui.min.js
	$(WGET) -O jqueryui-1.7/jquery-ui.js       http://ajax.googleapis.com/ajax/libs/jqueryui/1.7.2/jquery-ui.js

clean:
	rm -fR jquery-1.3
	rm -fR jquery-trunk
	rm -fR jqueryui-1.7
