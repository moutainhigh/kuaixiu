package com.common.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

/**
 * 二维码生成工具类.
 * 
 * @author: lijx
 * @CreateDate: 2016-10-7 下午11:33:23
 * @version: V 1.0
 */
public class QRCodeUtil {
    
    private static Base64 encoder = new Base64();    
    
    private static final String CHARSET = "utf-8";
    // 二维码尺寸
    private static final int QRCODE_SIZE = 200;
    // LOGO宽度
    private static final int WIDTH = 60;
    // LOGO高度
    private static final int HEIGHT = 60;
    
    private static final int BLACK = 0xFF000000;
    private static final int WHITE = 0xFFFFFFFF;
    
    /**
     * 
     * @param content
     * @throws WriterException
     * @author: lijx
     * @CreateDate: 2016-10-8 上午12:05:10
     */
    public static String createQRCodeOfBase64(String content) throws WriterException{
        BitMatrix bitMatrix = createQRCode(content);
        BufferedImage img = toBufferedImage(bitMatrix);
        return toBase64(img);
    }
    
    /**
     * 
     * @param content
     * @return
     * @throws WriterException
     * @author: lijx
     * @CreateDate: 2016-10-8 上午12:05:04
     */
    public static BitMatrix createQRCode(String content) throws WriterException{
        Hashtable hints = new Hashtable();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, CHARSET);
        hints.put(EncodeHintType.MARGIN, 1);
        MultiFormatWriter mfw = new MultiFormatWriter();
        BitMatrix bitMatrix = mfw.encode(content, BarcodeFormat.QR_CODE, QRCODE_SIZE, QRCODE_SIZE, hints);
        return bitMatrix;
    }
    
    /**
     * 转成BufferedImage
     * @param matrix
     * @return
     * @author: lijx
     * @CreateDate: 2016-10-7 下午11:55:19
     */
    public static BufferedImage toBufferedImage(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? BLACK : WHITE);
            }
        }
        return image;
    }
    
    /**
     * 将图片转成base64格式
     * @param bi
     * @return
     * @author: lijx
     * @CreateDate: 2016-10-8 上午12:02:20
     */
    public static String toBase64(BufferedImage bi){
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();    
            ImageIO.write(bi, "png", baos);    
            byte[] bytes = baos.toByteArray();  
            return encoder.encodeToString(bytes).trim();    
        }
        catch (IOException e) {
            e.printStackTrace();    
        }    
        return null;    
    }
    
    /**
     * @param args
     * @author: lijx
     * @CreateDate: 2016-10-7 下午11:33:13
     */
    public static void main(String[] args) {
        try {
            System.out.println(createQRCodeOfBase64("weixin://wxpay/bizpayurl?pr=sUBHNIT"));
        } 
        catch (WriterException e) {
            e.printStackTrace();
        }
    }

}
