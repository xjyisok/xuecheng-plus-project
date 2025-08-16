package com.xuecheng.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.xuecheng.base.exception.XueChengError;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.media.mapper.MediaFilesMapper;
import com.xuecheng.media.mapper.MediaProcessMapper;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.model.po.MediaProcess;
import com.xuecheng.media.service.MediaFileService;
import io.minio.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @description TODO
 * @author Mr.M
 * @date 2022/9/10 8:58
 * @version 1.0
 */
@Service
@Slf4j
@EnableAspectJAutoProxy(exposeProxy = true)
public class MediaFileServiceImpl implements MediaFileService {

    @Autowired
    MediaFilesMapper mediaFilesMapper;
    @Autowired
    MinioClient minioClient;
//    @Autowired
//    MediaFileService mediaProxy;
    @Autowired
    MediaProcessMapper mediaProcessMapper;
    @Override
    public PageResult<MediaFiles> queryMediaFiles(Long companyId,PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto) {

        //构建查询条件对象
        LambdaQueryWrapper<MediaFiles> queryWrapper = new LambdaQueryWrapper<>();

        //分页对象
        Page<MediaFiles> page = new Page<>(0, 40);
        // 查询数据内容获得结果
        Page<MediaFiles> pageResult = mediaFilesMapper.selectPage(page, queryWrapper);
        // 获取数据列表
        List<MediaFiles> list = pageResult.getRecords();
        // 获取数据总数
        long total = pageResult.getTotal();
        // 构建结果集
        PageResult<MediaFiles> mediaListResult = new PageResult<>(list, total, pageParams.getPageNo(), pageParams.getPageSize());
        return mediaListResult;

    }
    private String getDefaultFolderPath() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String folder = sdf.format(new Date()).replace("-", "/")+"/";
        return folder;
    }

    //获取文件的md5
    private String getFileMd5(File file) {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            String fileMd5 = DigestUtils.md5Hex(fileInputStream);
            return fileMd5;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private String getMimeType(String extension){
        if(extension==null)
            extension = "";
        //根据扩展名取出mimeType
        ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(extension);
        //通用mimeType，字节流
        String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        if(extensionMatch!=null){
            mimeType = extensionMatch.getMimeType();
        }
        return mimeType;
    }
    /**
     * @description 将文件写入minIO
     * @param localFilePath  文件地址
     * @param bucket  桶
     * @param objectName 对象名称
     * @return void
     * @author Mr.M
     * @date 2022/10/12 21:22
     */
    public boolean addMediaFilesToMinIO(String localFilePath,String mimeType,String bucket, String objectName) {
        try {
            UploadObjectArgs testbucket = UploadObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .filename(localFilePath)
                    .contentType(mimeType)
                    .build();
            minioClient.uploadObject(testbucket);
            log.debug("上传文件到minio成功,bucket:{},objectName:{}",bucket,objectName);
            System.out.println("上传成功");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("上传文件到minio出错,bucket:{},objectName:{},错误原因:{}",bucket,objectName,e.getMessage(),e);
            XueChengError.cast("上传文件到文件系统失败");
        }
        return false;
    }

    /**
     * @description 将文件信息添加到文件表
     * @param companyId  机构id
     * @param fileMd5  文件md5值
     * @param uploadFileParamsDto  上传文件的信息
     * @param bucket  桶
     * @param objectName 对象名称
     * @return com.xuecheng.media.model.po.MediaFiles
     * @author Mr.M
     * @date 2022/10/12 21:22
     */
    @Transactional
    public MediaFiles addMediaFilesToDb(Long companyId,String fileMd5,UploadFileParamsDto uploadFileParamsDto,String bucket,String objectName){
        //从数据库查询文件
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
        if (mediaFiles == null) {
            mediaFiles = new MediaFiles();
            //拷贝基本信息
            BeanUtils.copyProperties(uploadFileParamsDto, mediaFiles);
            mediaFiles.setId(fileMd5);
            mediaFiles.setFileId(fileMd5);
            mediaFiles.setCompanyId(companyId);
            mediaFiles.setUrl("/" + bucket + "/" + objectName);
            mediaFiles.setBucket(bucket);
            mediaFiles.setFilePath(objectName);
            mediaFiles.setCreateDate(LocalDateTime.now());
            mediaFiles.setAuditStatus("002003");
            mediaFiles.setStatus("1");
            //保存文件信息到文件表
            int insert = mediaFilesMapper.insert(mediaFiles);
            if (insert < 0) {
                log.error("保存文件信息到数据库失败,{}",mediaFiles.toString());
                XueChengError.cast("保存文件信息失败");
            }
            addWaitingTask(mediaFiles);
            System.out.println("添加文件到数据库成功");
            log.debug("保存文件信息到数据库成功,{}",mediaFiles.toString());

        }
        return mediaFiles;

    }
    private void addWaitingTask(MediaFiles mediaFiles) {
        //文件名称
        String filename = mediaFiles.getFilename();
        //文件扩展名
        String extension = filename.substring(filename.lastIndexOf(".")+1);
        //文件mimeType
        //String mimeType = getMimeType(extension);
        //如果是avi视频添加到视频待处理表
        System.out.println(extension);
        if (extension.equals("avi")) {
            MediaProcess mediaProcess = new MediaProcess();
            BeanUtils.copyProperties(mediaFiles, mediaProcess);
            mediaProcess.setStatus("1");//未处理
            mediaProcess.setFailCount(0);//失败次数默认为0
            mediaProcessMapper.insert(mediaProcess);
            System.out.println("<UNK>");
        }
    }
    @Value("${minio.bucket.files}")
    private String bucket_Files;
    @Value("${minio.bucket.videofiles}")
    private String bucket_Videofiles;
    //@Transactional
    @Override
    public UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, String localFilePath
    ,String objectname) {
        File file = new File(localFilePath);
        if (!file.exists()) {
            XueChengError.cast("文件不存在");
        }
        //文件名称
        String filename = uploadFileParamsDto.getFilename();
        //文件扩展名
        String extension = filename.substring(filename.lastIndexOf("."));
        //文件mimeType
        String mimeType = getMimeType(extension);
        //文件的md5值
        String fileMd5 = getFileMd5(file);
        //文件的默认目录
        String defaultFolderPath = getDefaultFolderPath();
        //存储到minio中的对象名(带目录)
        if(objectname==null){
            objectname = defaultFolderPath + fileMd5 + extension;
        }
        //将文件上传到minio
        boolean b = addMediaFilesToMinIO(localFilePath, mimeType, bucket_Files, objectname);
        //文件大小
        uploadFileParamsDto.setFileSize(file.length());
        //将文件信息存储到数据库
        MediaFiles mediaFiles =((MediaFileService) AopContext.currentProxy()).addMediaFilesToDb(companyId, fileMd5, uploadFileParamsDto, bucket_Files, objectname);
        //准备返回数据
        UploadFileResultDto uploadFileResultDto = new UploadFileResultDto();
        BeanUtils.copyProperties(mediaFiles, uploadFileResultDto);
        return uploadFileResultDto;

    }

    @Override
    public RestResponse<Boolean> checkfile(String fileMd5) {
        MediaFiles mediaFiles=mediaFilesMapper.selectById(fileMd5);
        if(mediaFiles!=null){
            String bucket = mediaFiles.getBucket();
            String filepath = mediaFiles.getFilePath();
            InputStream inputStream=null;
            try{
                inputStream=minioClient.getObject(GetObjectArgs.builder().bucket(bucket).object(filepath).build());
                if(inputStream!=null){
                    return RestResponse.success(true);
                }
            }catch (Exception e){
//                e.printStackTrace();
//                log.error("文件检索错误");
            }

        }
        return RestResponse.success(false);
    }

    @Override
    public RestResponse<Boolean> checkChunk(String fileMd5, int chunkIndex) {
        String chunkpath=getchunkfolder(fileMd5)+chunkIndex;
        InputStream inputStream=null;
        try{
            inputStream=minioClient.getObject(GetObjectArgs.builder().bucket(bucket_Videofiles).object(chunkpath).build());
            if(inputStream!=null){
                inputStream.close();
                return RestResponse.success(true);
            }
        }catch (Exception e){
//            e.printStackTrace();
//            log.error("文件分块检索错误");
        }
        return RestResponse.success(false);
    }
    public String getchunkfolder(String fileMd5){
        return fileMd5.substring(0,1)+"/"+fileMd5.substring(1,2)+"/"+fileMd5+"chunk"+"/";
    }
    public String getmergefolderByMD5(String fileMd5,String extention){
        return fileMd5.substring(0,1)+"/"+fileMd5.substring(1,2)+"/"+fileMd5+"/"+fileMd5+extention;
    }
    @Override
    public RestResponse<Boolean> uploadchunkfiles(String fileMd5, int chunkIndex, String localFilePath) {
        String chunkpath=getchunkfolder(fileMd5)+chunkIndex;
        String mimetype=getMimeType(null);
        boolean isuploaded=addMediaFilesToMinIO(localFilePath, mimetype, bucket_Videofiles, chunkpath);
        if(!isuploaded){
            log.debug("上传分块文件失败:{}", chunkpath);
            return RestResponse.validfail(false,"上传分块失败");
        }
        log.info("上传分块文件成功{}", chunkpath);
        return RestResponse.success(true);
    }

    @Override
    public RestResponse<Boolean> mergechunk(Long companyId,String fileMd5,UploadFileParamsDto uploadFileParamsDto,int chunkTotal) {
        List<ComposeSource>chunklist=new ArrayList<ComposeSource>();
        for(int i=0;i<chunkTotal;i++){
            String chunkpath=getchunkfolder(fileMd5)+i;
            ComposeSource composeSource= ComposeSource.builder().bucket(bucket_Videofiles).object(chunkpath).build();
            chunklist.add(composeSource);
        }
        String filename=uploadFileParamsDto.getFilename();
        String extension = filename.substring(filename.lastIndexOf("."));
        String mergedfolder=getmergefolderByMD5(fileMd5,extension);
        try{
            ComposeObjectArgs composeObjectArgs=ComposeObjectArgs.builder().bucket(bucket_Videofiles).object(mergedfolder).
                    sources(chunklist).build();
            minioClient.composeObject(composeObjectArgs);
            log.debug("合并文件成功:{}",mergedfolder);
            System.out.println("合并文件成功");
        }catch (Exception e){
            log.debug("合并文件失败,fileMd5:{},异常:{}",fileMd5,e.getMessage(),e);
            return RestResponse.validfail(false, "合并文件失败。");
        }
        File mergedfile=downloadFile(mergedfolder,bucket_Videofiles);
        if(mergedfile!=null) {
            try {
                InputStream inputStream = new FileInputStream(mergedfile.getAbsolutePath());
                String Md5Merged = DigestUtils.md5Hex(inputStream);
                if (!Md5Merged.equals(fileMd5)) {
                    return RestResponse.validfail(false, "合并文件校验和失败");
                }
                inputStream.close();
                uploadFileParamsDto.setFileSize(mergedfile.length());
            } catch (Exception e) {
                log.debug("校验文件失败,fileMd5:{},异常:{}",fileMd5,e.getMessage(),e);
                System.out.println("校验和失败");
                return RestResponse.validfail(false, "文件合并校验失败，最终上传失败。");
            }
            finally {
                if(mergedfile!=null){
                    mergedfile.delete();
                }
            }
        }
        ((MediaFileService) AopContext.currentProxy()).addMediaFilesToDb(companyId,fileMd5,uploadFileParamsDto,bucket_Videofiles,mergedfolder);
        deletechunks(fileMd5,chunkTotal);
        return RestResponse.success(true);
    }
    public File downloadFile(String filepath,String bucket){
        OutputStream outputStream=null;
        try{
            File tempfile=File.createTempFile("minio",".merge");
            outputStream=new FileOutputStream(tempfile);
            InputStream inputStream =minioClient.getObject(GetObjectArgs.builder().bucket(bucket).object(filepath).build());
            if(inputStream!=null){
                IOUtils.copy(inputStream, outputStream);
                inputStream.close();
            }
            return tempfile;

        }catch(Exception e) {
            e.printStackTrace();
        }finally {
            if(outputStream!=null){
                try{
                outputStream.close();}
                catch(Exception e){
                    e.printStackTrace();
                }

            }
        }
        return null;
    }
    public void deletechunks(String fileMd5,int totalchunk){
        List<DeleteObject>deletechunks=new ArrayList<>();
        for(int i=0;i<totalchunk;i++){
            String chunkpath=getchunkfolder(fileMd5)+i;
            DeleteObject deleteObject=new DeleteObject(chunkpath);
            deletechunks.add(deleteObject);
        }
        try{
            RemoveObjectsArgs removeObjectArgs=RemoveObjectsArgs.builder().bucket(bucket_Videofiles).objects(deletechunks).build();
            Iterable<Result<DeleteError>> results = minioClient.removeObjects(removeObjectArgs);
            results.forEach(r->{
                DeleteError deleteError = null;
                try {
                    deleteError = r.get();
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("清楚分块文件失败,objectname:{}",deleteError.objectName(),e);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            log.error("清楚分块文件失败,chunkFileFolderPath:{}",getchunkfolder(fileMd5),e);
        }
    }
    @Override
    public MediaFiles getFileById(String md5){
        return mediaFilesMapper.selectById(md5);
    }
}
