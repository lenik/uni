package net.bodz.uni.qrcopy;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import net.bodz.bas.c.java.io.FilePath;
import net.bodz.bas.c.java.util.Arrays;
import net.bodz.bas.data.util.Crc32;
import net.bodz.bas.io.res.builtin.FileResource;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.bas.meta.build.ProgramName;
import net.bodz.bas.program.skel.BasicCLI;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

/**
 * Base45 decoder
 */
@ProgramName("base45d")
public class Base45Decoder
        extends BasicCLI {

    static final Logger logger = LoggerFactory.getLogger(Base45Decoder.class);

    boolean fragments = true;

    /**
     * Write to the output dir instead of stdout.
     *
     * @option --outdir -O =DIR
     */
    File outDir;

    @Override
    protected void mainImpl(String... args)
            throws IOException {
        String nameOverride = null;
        long totalSize = -1;

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            logger.info("decode: " + arg);

            String base45;
            if (arg.endsWith(".png") || arg.endsWith(".jpg")) {
                Map<DecodeHintType, Object> hintMap = new HashMap<>();
                hintMap.put(DecodeHintType.TRY_HARDER, true);
                hintMap.put(DecodeHintType.POSSIBLE_FORMATS, Arrays.asList(//
                        BarcodeFormat.QR_CODE));
                try {
                    base45 = readQRCode(new File(arg), hintMap);
                } catch (NotFoundException e) {
                    logger.error("not found QRCode. skipped.");
                    continue;
                }
            } else {
                File file = new File(arg);
                base45 = new FileResource(file).read().readString();
            }

            byte[] chunk = Base45Encoder.decodeBase45QrPayload(base45);

            int headerSize = 8;
            if (chunk.length < headerSize) {
                logger.error("Bad header. Skip the file.");
                continue;
            }

            int index = readInt(chunk, 0);
            int crcExpect = readInt(chunk, 4);

            int len = chunk.length - headerSize;
            if (len < 0)
                len = 0;

            Crc32 crc32 = new Crc32();
            crc32.update(chunk, headerSize, len);
            int crcActual = crc32.getValue();
            if (crcActual != crcExpect) {
                logger.error("CRC failed: " + arg);
                continue;
            }

            int off = headerSize;
            if (i == 0) {
                int colon1 = Arrays.indexOf(chunk, off, chunk.length, (byte) ':');
                if (colon1 != -1) {
                    nameOverride = new String(chunk, off, colon1 - off);
                    off = colon1 + 1;

                    int colon2 = Arrays.indexOf(chunk, off, chunk.length, (byte) ':');
                    if (colon2 != -1) {
                        String sizeInfo = new String(chunk, off, colon2 - off);
                        totalSize = Long.parseLong(sizeInfo);
                        off = colon2 + 1;
                    }
                }
            }

            if (outDir == null) {
                System.out.write(chunk, headerSize, len);
                continue;
            }

            outDir.mkdirs();

            String outName;
            if (nameOverride != null) {
                outName = nameOverride + "-" + index;
            } else {
                String argBase = FilePath.getBaseName(arg);
                String argName = FilePath.splitExtension(argBase).getName();
                outName = argName + "-" + index;
            }

            File outFile = new File(outDir, outName);
            logger.info("write to " + outFile);

            try (OutputStream out = new FileOutputStream(outFile)) {
                out.write(chunk, off, chunk.length - off);
            }
        }
    }

    int readInt(byte[] buf, int off) {
        int data = 0;
        for (int i = 0; i < 4; i++) {
            int elm = buf[off + i] & 0xFF;
            data = (data << 8) | elm;
        }
        return data;
    }

    String readQRCode(File file, Map<DecodeHintType, ?> hintMap)
            throws IOException, NotFoundException {
        BufferedImage image = ImageIO.read(file);
        BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        Result result = new MultiFormatReader().decode(bitmap, hintMap);
        return result.getText();
    }

    public static void main(String[] args)
            throws Exception {
        new Base45Decoder().execute(args);
    }

}
