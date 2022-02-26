package com.sucker.oss.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.sucker.oss.service.OssService;
import com.sucker.oss.utils.ConstantPropertiesUtils;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Service
public class OssServiceImpl implements OssService {
    //上传头像
    @Override
    public String uploadFileAvatar(MultipartFile file) {
        //获取阿里云存储相关常量
        String endpoint = ConstantPropertiesUtils.END_POINT;
        String accessKeyId = ConstantPropertiesUtils.ACCESS_KEY_ID;
        String accessKeySecret = ConstantPropertiesUtils.ACCESS_KEY_SECRET;
        String bucketName = ConstantPropertiesUtils.BUCKET_NAME;

        try {
            // 创建OSSClient实例。
            OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

            // 填写本地文件的完整路径。如果未指定本地路径，则默认从示例程序所属项目对应本地路径中上传文件流。
            InputStream inputStream = file.getInputStream();

            //获取文件名称
            String fileName = file.getOriginalFilename();

            //文件名处理，在名称前面加上唯一值防止oss上传覆盖
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            fileName = uuid+fileName;

            //把文件按日期分类
            String datePath = new DateTime().toString("yyyy/MM/dd");
            //拼接后oss会自动创建日期文件夹  2022/02/01/efafadf1.png
            fileName = datePath+"/"+fileName;

            // 依次填写Bucket名称 和Object完整路径（例如exampledir/exampleobject.txt）
            ossClient.putObject(bucketName, fileName, inputStream);

            // 关闭OSSClient。
            ossClient.shutdown();

            //需要把上传的路径 手动拼接出来
            String url="https://"+bucketName+"."+endpoint+"/"+fileName;
            return url;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}
