package com.xuecheng.content.feignclient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@Slf4j
public class MediaServiceClientFallbackFactory implements FallbackFactory<MediaServiceClient> {
    @Override
    public MediaServiceClient create(Throwable cause) {
        return new MediaServiceClient() {
            @Override
            public String uploadFile(MultipartFile upload, String objectName) {
                log.debug("调用媒资管理服务上传文件时发生熔断，异常信息:{}",cause.toString(),cause);
                return null;
            }
        };
    }
}
