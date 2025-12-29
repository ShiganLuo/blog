package com.baofeng.blog.common.util;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageFileUtil {

    // 存储幻数和对应扩展名的映射
    private static final Map<String, String> MAGIC_NUMBER_TO_EXTENSION = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(ImageFileUtil.class);

    static {
        // 十六进制字符串，用于与文件的二进制头进行比较
        MAGIC_NUMBER_TO_EXTENSION.put("FFD8FF", ".jpg");
        MAGIC_NUMBER_TO_EXTENSION.put("89504E47", ".png");
        MAGIC_NUMBER_TO_EXTENSION.put("47494638", ".gif");
        MAGIC_NUMBER_TO_EXTENSION.put("424D", ".bmp");
    }

    /**
     * 读取文件的二进制头，获取幻数。
     */
    private static String getMagicNumber(InputStream is) throws IOException {
        byte[] header = new byte[8];
        is.read(header);
        
        StringBuilder sb = new StringBuilder();
        for (byte b : header) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    /**
     * 通过幻数验证并为图片生成一个唯一文件名。
     * @param file 用户上传的MultipartFile
     * @return 唯一的、带正确扩展名的文件名，如果类型不支持则返回null
     */
    public static String generateUniqueImageName(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            logger.warn("图片为空");
            return null;
        }

        try (InputStream is = file.getInputStream()) {
            String fileHeader = getMagicNumber(is);

            for (Map.Entry<String, String> entry : MAGIC_NUMBER_TO_EXTENSION.entrySet()) {
                if (fileHeader.startsWith(entry.getKey())) {
                    String uuid = UUID.randomUUID().toString();
                    String extension = entry.getValue();
                    return uuid + extension;
                }
            }
        } catch (IOException e) {
            // 异常处理
            logger.warn("生成图片文件唯一名称时文件读取失败：",e);
        }
        logger.warn("如果没有找到匹配的幻数，返回null");
        return null;
    }
}