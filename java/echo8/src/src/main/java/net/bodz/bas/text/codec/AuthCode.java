package net.bodz.bas.text.codec;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;

import net.bodz.bas.err.ParseException;

public class AuthCode {

    Date startTime;
    Date endTime;
    String payload;

    public AuthCode() {
    }

    public AuthCode(String payload, int months) {
        if (payload == null)
            throw new NullPointerException("payload");
        this.payload = payload;

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        calendar.clear();
        calendar.set(year, month, 0);
        startTime = calendar.getTime();
        calendar.add(Calendar.MONTH, months);
        endTime = calendar.getTime();
    }

    /**
     * ssee*mmm, where ss=start-time, ee=end-time, *=options, mmm=hmac (15-bit)
     */
    public static AuthCode parse(String authString)
            throws ParseException {
        if (authString == null)
            throw new NullPointerException("authString");
        int len = authString.length();
        if (len < 7)
            throw new IllegalArgumentException("auth string is too short: " + authString);

        String body = authString.substring(0, len - 3);
        String hmacActual = authString.substring(len - 3);
        String hmacExpected = rolHmac15bit(body);
        if (!hmacExpected.equals(hmacActual))
            throw new ParseException("Illegal auth code (hmac failed): " + authString);

        AuthCode code = new AuthCode();
        try {
            code.startTime = parseDate(authString.substring(0, 2));
            code.endTime = parseDate(authString.substring(2, 4));
        } catch (ParseException e) {
            throw new ParseException("Illegal auth-string: " + authString, e);
        }
        code.payload = authString.substring(4, len - 3);

        return code;
    }

    static String formatDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int y2k = calendar.get(Calendar.YEAR) - 2000;
        int month = calendar.get(Calendar.MONTH);
        int y2kMonth = y2k * 12 + month;
        String rolY2kMonth = RolCodec.encode(y2kMonth, 2);
        return rolY2kMonth;
    }

    static Date parseDate(String rolY2kMonth)
            throws ParseException {
        long y2kMonth;
        try {
            y2kMonth = RolCodec.decode(rolY2kMonth);
        } catch (IllegalArgumentException e) {
            throw new ParseException("Failed to parth y2k-month part of the auth-string", e);
        }
        int month = (int) (y2kMonth % 12);
        int year = 2000 + (int) (y2kMonth / 12);
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(year, month, 0);
        return calendar.getTime();
    }

    static final String salt = "Bee32";
    static final MessageDigest md5;
    static final Charset utf8;
    static {
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new Error(e.getMessage(), e);
        }
        utf8 = Charset.forName("utf-8");
    }

    static synchronized String rolHmac15bit(String str) {
        long hmac = hmac(str, 2); // 16-bit
        hmac >>= 1; // 15-bit
        String rolHmac = RolCodec.encode(hmac, 3);
        return rolHmac;
    }

    static synchronized long hmac(String str, int bytes) {
        byte[] in = (salt + str).getBytes(utf8);
        byte[] hmac = md5.digest(in);
        long n = 0;
        for (int i = 0; i < bytes; i++) {
            int byt = hmac[i % hmac.length] & 0xff;
            n = (n << 8) | byt;
        }
        return n;
    }

}
