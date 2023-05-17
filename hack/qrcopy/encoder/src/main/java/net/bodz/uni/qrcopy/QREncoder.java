package net.bodz.uni.qrcopy;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import net.bodz.bas.c.string.Strings;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.bas.meta.build.ProgramName;
import net.bodz.bas.program.skel.BasicCLI;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.decoder.Version;

/**
 * Encode file as QR images.
 */
@ProgramName("qrcopy")
public class QREncoder
        extends BasicCLI {

    static final Logger logger = LoggerFactory.getLogger(QREncoder.class);

    /**
     * Specify QR image size. Default auto determined.
     *
     * @option --size -s =SIZE
     */
    int imageSize = 0;

    /**
     * Scale the result image. To avoid blur. Default 16.
     *
     * @option --dot-scale =RATIO
     */
    int dotScale = 16;

    /**
     * Max version to use. Default 40 (max)
     *
     * @option --qrcode-version -V =QRVER
     */
    int qrVersion = 40;

    /**
     * Error correction level.
     *
     * @option --ec-level -e =LEVEL
     */
    ErrorCorrectionLevel ecLevel = ErrorCorrectionLevel.M;

    /**
     * Specify the output file name template.
     *
     * @option --output -o =FILE
     */
    String outputName;

    /**
     * Specify the image format.
     *
     * @option --format -F =FORMAT
     */
    String format = "png";

    @Override
    protected void mainImpl(String... args)
            throws Exception {
        if (outputName == null)
            throw new IllegalArgumentException("No output name specified.");

        if (args.length == 0)
            convert(System.in, "-", Integer.MAX_VALUE);
        else {
            File outDir = new File(outputName).getParentFile();
            outDir.mkdirs();

            for (String arg : args) {
                File file = new File(arg);
                long fileSize = file.length();
                try (InputStream in = new FileInputStream(file)) {
                    convert(in, file.getName(), fileSize);
                }
            }
        }
    }

    void convert(InputStream in, String name, long maxSize)
            throws WriterException, IOException {
        int blockSize = getBlockSize();

        int headerSize = 4;
        int fileBlockSize = blockSize - headerSize;
        long maxBlocks = maxSize / fileBlockSize;
        logger.debug("file block size: " + fileBlockSize);
        logger.debug("max blocks: " + maxBlocks);

        int seqWidth = String.valueOf(maxBlocks).length();
        if (seqWidth < 3)
            seqWidth = 3;
        String zeros = Strings.repeat(seqWidth, '0');

        byte[] block = new byte[blockSize];

        int off = headerSize;
        String title = (name + ":" + maxSize + ":");
        byte[] titleBytes = title.getBytes("utf-8");
        System.arraycopy(titleBytes, 0, block, off, titleBytes.length);
        off += titleBytes.length;

        int cbRead;
        int index = 0;
        while ((cbRead = in.read(block, off, blockSize - off)) != -1) {
            writeHeader(block, index);

            String seq = (zeros + index);
            seq = seq.substring(seq.length() - seqWidth);
            File file = new File(outputName + "-" + seq + "." + format);

            String base45 = Base45Encoder.encodeToBase45QrPayload( //
                    block, 0, off + cbRead);

            logger.info("Generate block[%d]: %s", index, base45);

            byte[] imageData = createQRImage(base45);

            Files.write(file.toPath(), imageData);
            index++;
            off = headerSize;
        }
    }

    void writeHeader(byte[] buf, int index) {
        for (int i = 0; i < 4; i++) {
            buf[i] = (byte) (index & 0xFF);
            index >>= 8;
        }
    }

    Version getQRVersion() {
        return Version.getVersionForNumber(this.qrVersion);
    }

    int getBlockSize() {
        Version qrVer = getQRVersion();
        int numBytes = qrVer.getTotalCodewords();
        // getNumECBytes = 130

        Version.ECBlocks ecBlocks = qrVer.getECBlocksForLevel(ecLevel);
        int numEcBytes = ecBlocks.getTotalECCodewords();

        int numDataBytes = numBytes - numEcBytes;
        return numDataBytes;
    }

    byte[] createQRImage(byte[] data, int off, int len)
            throws WriterException, IOException {
        String base45 = Base45Encoder.encodeToBase45QrPayload(data, off, len);
        byte[] image = createQRImage(base45);
        return image;
    }

    byte[] createQRImage(String base45)
            throws WriterException, IOException {

        // Create the ByteMatrix for the QR-Code that encodes the given String
        Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<>();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ecLevel);

        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        int size = imageSize;
        if (size == 0) {
            size = getQRVersion().getDimensionForVersion() * dotScale;
        }
        int width = size, height = size;

        BitMatrix byteMatrix = qrCodeWriter.encode(base45, BarcodeFormat.QR_CODE, //
                width, height, hintMap);
        // Make the BufferedImage that are to hold the QRCode
        int matrixWidth = byteMatrix.getWidth();
        BufferedImage image = new BufferedImage(matrixWidth, matrixWidth, BufferedImage.TYPE_INT_RGB);
        image.createGraphics();

        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, matrixWidth, matrixWidth);
        // Paint and save the image using the ByteMatrix
        graphics.setColor(Color.BLACK);

        for (int i = 0; i < matrixWidth; i++) {
            for (int j = 0; j < matrixWidth; j++) {
                if (byteMatrix.get(i, j)) {
                    graphics.fillRect(i, j, 1, 1);
                }
            }
        }
        ByteArrayOutputStream buf = new ByteArrayOutputStream(100000);
        ImageIO.write(image, format, buf);
        byte[] imageData = buf.toByteArray();
        return imageData;
    }

    public static void main(String[] args)
            throws Exception {
        new QREncoder().execute(args);
    }

}
