package cloneproject.Instagram.util;

import java.util.UUID;

import com.google.common.base.Enums;

import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import cloneproject.Instagram.exception.NotSupportedImageTypeException;
import cloneproject.Instagram.vo.Image;
import cloneproject.Instagram.vo.ImageType;

public class ImageUtil {
    public static Image convertMultipartToImage(MultipartFile file){
        
        String url = "C:\\spring\\upload";
        String originalName = file.getOriginalFilename();
        String name = FilenameUtils.getBaseName(originalName);
        String type = FilenameUtils.getExtension(originalName).toUpperCase();

        if(!Enums.getIfPresent(ImageType.class, type).isPresent())
            throw new NotSupportedImageTypeException();

        Image image = Image.builder()
                .imageUrl(url)
                .imageType(ImageType.valueOf(type))
                .imageName(name)
                .imageUUID(UUID.randomUUID().toString())
                .build();

        return image;
    }
}
