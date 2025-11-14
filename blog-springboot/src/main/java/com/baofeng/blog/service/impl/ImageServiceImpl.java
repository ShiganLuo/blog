package com.baofeng.blog.service.impl;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import lombok.RequiredArgsConstructor;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baofeng.blog.service.ImageService;
import com.baofeng.blog.service.MinioService;
import com.baofeng.blog.mapper.ImageMapper;
import com.baofeng.blog.vo.ApiResponse;
import com.baofeng.blog.vo.front.FrontImageVO.AlbumResponse;
import com.baofeng.blog.util.ImageFileUtil;
import com.baofeng.blog.entity.Image;
import com.baofeng.blog.enums.ResultCodeEnum;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final ImageMapper imageMapper;
    private final MinioService minioService;
    private static final Logger logger = LoggerFactory.getLogger(ImageService.class);

    @Override
    public ApiResponse<List<AlbumResponse>> getAllAlbum() {
        // 调用Mapper方法获取所有相册信息
        List<AlbumResponse> albumList = imageMapper.selectAllAlbumsAbstract(null);
        
        if (albumList.isEmpty()) {
            return ApiResponse.error(404, "没有找到相册信息");
        }
        
        return ApiResponse.success(albumList);
    }

    @Override
    public ApiResponse<String> uploadImage(MultipartFile file) {

        String uniqueFilename = ImageFileUtil.generateUniqueImageName(file);
        if (uniqueFilename == null) {
            return ApiResponse.error(ResultCodeEnum.BAD_REQUEST,"图片为空或图片类型错误, 目前仅支持jpg、png、gif、bmp");
        }

        try {
            // ===== 使用 MinioService 上传 =====
            minioService.uploadFile(
                    uniqueFilename,
                    file.getInputStream(),
                    file.getSize(),
                    file.getContentType()
            );

            // 获取可访问地址
            String imagePath = minioService.getPermanentFileUrl(uniqueFilename);
                // 7. 插入 images 表
            Image image = new Image();
            long bytes = file.getSize();
            long kilobytes = bytes / 1024;
            String contentType = file.getContentType();
            String username = null;

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null) {
                username = authentication.getName();
            }

            image.setFilePath(imagePath);
            image.setFileName(uniqueFilename);
            image.setFileSize(kilobytes);
            image.setMimeType(contentType);
            image.setCreatedBy(username);
            int rowsUpdated = imageMapper.insertImage(image);
            return rowsUpdated > 0
                ? ApiResponse.success(imagePath)
                : ApiResponse.error(ResultCodeEnum.INTERNAL_SERVER_ERROR);

        } catch (Exception e) {
            logger.warn("minio存储文件失败");
            return ApiResponse.error(ResultCodeEnum.INTERNAL_SERVER_ERROR, "文件存储失败");
        }


    }
    
}
