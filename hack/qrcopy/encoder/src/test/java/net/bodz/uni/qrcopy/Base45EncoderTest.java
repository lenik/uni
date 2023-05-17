package net.bodz.uni.qrcopy;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;

import org.junit.Test;

import com.google.zxing.WriterException;
import com.google.zxing.common.BitArray;

public class Base45EncoderTest {

    @Test
    public void stringEncodingTest()
            throws IOException {
        // Init test data
        final String testStr = "Some cool input data! !@#$%^&*()_+";

        // Encode
        final String encodedStr = Base45Encoder.encodeToBase45QrPayload(testStr.getBytes("UTF-8"));

        // Decode
        final byte[] decodedBytes = Base45Encoder.decodeBase45QrPayload(encodedStr);
        final String decodedStr = new String(decodedBytes, "UTF-8");

        // Output
        final boolean matches = testStr.equals(decodedStr);
        assert (matches);
        System.out.println("They match!");
    }

    @Test
    public void binaryEncodingAccuracyTest()
            throws IOException {
        // Init test data
        final int maxBytes = 10_000;
        for (int x = 1; x <= maxBytes; x++) {
            System.out.print("x: " + x + "\t");

            // Encode
            final byte[] inputArray = getTestBytes(x);
            final String encodedStr = Base45Encoder.encodeToBase45QrPayload(inputArray);

            // Decode
            final byte[] decodedBytes = Base45Encoder.decodeBase45QrPayload(encodedStr);

            // Output
            for (int y = 0; y < x; y++) {
                assertEquals(inputArray[y], decodedBytes[y]);
            }
            System.out.println("Passed!");
        }
    }

    @Test
    public void binaryEncodingEfficiencyTest()
            throws IOException, WriterException, NoSuchMethodException, InvocationTargetException,
            IllegalAccessException {
        // Init test data
        final byte[] inputData = new byte[2048];
        new Random().nextBytes(inputData);

        // Encode
        final String encodedStr = Base45Encoder.encodeToBase45QrPayload(inputData);

        // Write to QR Code Encoder // Have to use Reflection to force access, since the function is
        // not public.
        final BitArray qrCode = new BitArray();
        final Method appendAlphanumericBytes = com.google.zxing.qrcode.encoder.Encoder.class
                .getDeclaredMethod("appendAlphanumericBytes", CharSequence.class, BitArray.class);
        appendAlphanumericBytes.setAccessible(true);
        appendAlphanumericBytes.invoke(null, encodedStr, qrCode);

        // Output
        final int origSize = inputData.length;
        final int qrSize = qrCode.getSizeInBytes();
        System.out.println("Raw Binary Size:\t\t" + origSize + "\nEncoded String Size:\t" + encodedStr.length()
                + "\nQR Code Alphanum Size:\t" + qrSize);

        // Calculate Storage Efficiency Loss
        final int delta = origSize - qrSize;
        final double efficiency = ((double) delta) / origSize;
        System.out.println("Storage Efficiency Loss: " + String.format("%.3f", efficiency * 100) + "%");
    }

    public static byte[] getTestBytes(int numBytes) {
        final Random rand = new Random();
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        for (int x = 0; x < numBytes; x++) {
            // bos.write(255);// -1 (byte) = 255 (int) = 1111 1111

            byte b = (byte) rand.nextInt();
            bos.write(b);
        }
        return bos.toByteArray();
    }

}
