package com.xuecheng.media;

import com.xuecheng.base.exception.XueChengError;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
@Slf4j
public class ChunkTest {
    MinioClient minioClient =
            MinioClient.builder()
                    .endpoint("http://192.168.101.65:9000")
                    .credentials("minioadmin", "minioadmin")
                    .build();
    @Test
    public void chunktest() throws IOException {
        String Inputdir="D:\\c++资料\\xuechengtest\\testchunk.mp4";
        String Outputdir="D:\\c++资料\\xuechengtest\\Chunk\\";
        int chunksize=1024*1024*1;
        byte[] buffer=new byte[1024];
        File fin=new File(Inputdir);
        long finlen=fin.length();
        int chunknumber=(int)Math.ceil((double) finlen/chunksize);
        RandomAccessFile rafreader=new RandomAccessFile(fin,"rw");
        for(int i=0;i<chunknumber;i++){
            File fout=new File(Outputdir+i);
            fout.createNewFile();
            RandomAccessFile rafwriter=new RandomAccessFile(fout,"rw");
            int len=-1;
            int pos=0;
            while((len=rafreader.read(buffer))!=-1){
                rafwriter.write(buffer,0,len);
                pos+=len;
                if(pos>=chunksize){
                    break;
                }
            }
            rafwriter.close();
            System.out.println("chunk"+i+"completeted");
        }
        rafreader.close();
    }
    @Test
    public void mergetest2() throws IOException {
        String Outputdir="D:\\c++资料\\xuechengtest\\Merge\\";
        String chunkdir="D:\\c++资料\\xuechengtest\\Chunk\\";
        File fin=new File(chunkdir);
        File[] fileArray=fin.listFiles();
        List<File> fins = Arrays.asList(fileArray);
        // 从小到大排序
        Collections.sort(fins, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return Integer.parseInt(o1.getName()) - Integer.parseInt(o2.getName());
            }
        });
        File fin2=new File(Outputdir+"merged.mp4");
        fin2.createNewFile();
        RandomAccessFile rawriter=new RandomAccessFile(fin2,"rw");
        byte[] buffer=new byte[1024];
        for(int i=0;i<fins.size();i++){
            RandomAccessFile rafreader=new RandomAccessFile(fins.get(i),"rw");
            int len=-1;
            while((len=rafreader.read(buffer))!=-1){
                //rawriter.seek(rawriter.length());
                rawriter.write(buffer,0,len);
            }
            rafreader.close();
        }
        rawriter.close();
        try (

                FileInputStream fileInputStream = new FileInputStream("D:\\c++资料\\xuechengtest\\testchunk.mp4");
                FileInputStream mergeFileStream = new FileInputStream(fin2);

        ) {
            //取出原始文件的md5
            String originalMd5 = DigestUtils.md5Hex(fileInputStream);
            //取出合并文件的md5进行比较
            String mergeFileMd5 = DigestUtils.md5Hex(mergeFileStream);
            if (originalMd5.equals(mergeFileMd5)) {
                System.out.println("合并文件成功");
            } else {
                System.out.println("合并文件失败");
            }

        }

    }
    @Test
    public void uploadToMinio() {
        String Inputdir="D:\\c++资料\\xuechengtest\\Chunk\\";
        File fin=new File(Inputdir);
        File[] filearray=fin.listFiles();
        List<File> fileList=Arrays.asList(filearray);
        Collections.sort(fileList,(f1,f2)->(Integer.parseInt(f1.getName())-Integer.parseInt(f2.getName())));
        for(int i=0;i<fileList.size();i++) {
            try{
            UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder().
                    bucket("testbucket").
                    object("chunk/" +i).
                    filename(fileList.get(i).
                            getAbsolutePath()).build();
            minioClient.uploadObject(uploadObjectArgs);
            }catch (Exception e) {
                e.printStackTrace();
                log.error("上传文件到minio出错,bucket:{},objectName:{},错误原因:{}","testbucket","object"+i,e.getMessage(),e);
                XueChengError.cast("上传文件到文件系统失败");
            }

        }
        System.out.println("上传成功");
    }
}
