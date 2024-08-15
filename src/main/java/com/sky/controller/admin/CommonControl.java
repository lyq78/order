package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/admin/common")
public class CommonControl {

    @Autowired
    private AliOssUtil aliOssUtil;
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file){
        //文件上传到阿里云
        try {
            String fileName = file.getOriginalFilename();
            String newname = UUID.randomUUID().toString()+fileName.substring(fileName.lastIndexOf("."));
            String url = aliOssUtil.upload(file.getBytes(),newname);
            return Result.success(url);
        } catch (IOException e) {
            log.error("文件上传失败：{}",e);
//          throw Result.error(Messag eConstant.UPLOAD_FAILED);
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}
