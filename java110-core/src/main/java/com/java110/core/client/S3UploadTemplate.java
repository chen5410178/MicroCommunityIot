package com.java110.core.client;

import com.java110.core.factory.LoggerFactory;
import com.java110.core.utils.Base64Convert;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.S3Util;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.*;
import java.util.UUID;

@Component
public class S3UploadTemplate {
    private static Logger logger = LoggerFactory.getLogger(S3UploadTemplate.class);

    private static String ftpPath = "uploadFiles";

    private static String LOCAL_CHARSET = "GBK";
    private static String SERVER_CHARSET = "ISO-8859-1";
    private final static String localpath = "F:/";
    private final static String fileSeparator = System.getProperty("file.separator");

    private final static String DEFAULT_IMG_SUFFIX = ".jpg";

    private final static String IMAGE_DEFAULT_PATH = "img/";

    /*
     *图片上传工具方法
     * 默认上传至 img 文件下的当前日期下
     */
    public String upload(String imageBase64,  String ftpPath) {
        String fileName = "";
        S3Client s3Client = null;
        ByteArrayInputStream is = null;
        String urlPath = "";
        try {
            s3Client = S3Util.getS3Client();
            fileName = UUID.randomUUID().toString();
            if (imageBase64.contains("data:image/png;base64,")) {
                imageBase64 = imageBase64.replace("data:image/png;base64,", "");
                fileName += ".png";
            } else if (imageBase64.contains("data:image/jpeg;base64,")) {
                imageBase64 = imageBase64.replace("data:image/jpeg;base64,", "");
                fileName += ".jpg";
            } else if (imageBase64.contains("data:image/webp;base64,")) {
                imageBase64 = imageBase64.replace("data:image/webp;base64,", "");
                fileName += ".jpg";
            } else if (imageBase64.contains("data:application/octet-stream;base64,")) {
                imageBase64 = imageBase64.replace("data:application/octet-stream;base64,", "");
                fileName += ".jpg";
            } else {
                fileName += ".jpg";
            }
            urlPath = IMAGE_DEFAULT_PATH + DateUtil.getNowII() + "/" + fileName;
            byte[] context = Base64Convert.base64ToByte(imageBase64);
            is = new ByteArrayInputStream(context);

            S3Util.uploadByInputStream(s3Client, is, ftpPath + urlPath);
        } catch (Exception e) {
            logger.error("上传文件失败", e);
            throw new IllegalArgumentException("上传文件失败");
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return urlPath;
    }


    /*
     *文件上传工具方法
     */
    public String upload(MultipartFile uploadFile, String ftpPath) {
        String fileName = "";
        S3Client s3Client = null;
        InputStream is = null;
        try {
            s3Client = S3Util.getS3Client();
            fileName = UUID.randomUUID().toString() + "." + uploadFile.getOriginalFilename().substring(uploadFile.getOriginalFilename().lastIndexOf(".") + 1);
            fileName = IMAGE_DEFAULT_PATH + DateUtil.getNowII() + "/" + fileName;
            is = uploadFile.getInputStream();
            S3Util.uploadByInputStream(s3Client, is, ftpPath + fileName);
        } catch (Exception e) {
             logger.error("上传文件失败", e);
            throw new IllegalArgumentException("上传文件失败");
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return fileName;
    }

    public String upload(InputStream inputStream, String ftpPath) {
        S3Client s3Client = null;
        try {
            s3Client = S3Util.getS3Client();
            S3Util.uploadByInputStream(s3Client, inputStream, ftpPath);
        } catch (Exception e) {
            throw new IllegalArgumentException("上传文件失败");
        } finally {
        }
        return ftpPath;
    }

    /*
     *文件下载工具方法
     */
    public byte[] downFileByte(String remotePath, String fileName, String server, int port, String userName, String userPassword) {
        byte[] return_arraybyte = null;
        S3Client s3Client = null;
        ByteArrayOutputStream byteOut = null;
        InputStream ins = null;
        ByteArrayInputStream fis = null;
        try {
            s3Client = S3Util.getS3Client();
            ins = S3Util.getInputStreamByS3(s3Client, remotePath + fileName);
            byteOut = new ByteArrayOutputStream();
            byte[] buf = new byte[2048];
            int bufsize = 0;
            while (ins != null && (bufsize = ins.read(buf, 0, buf.length)) != -1) {
                byteOut.write(buf, 0, bufsize);
            }
            fis = new ByteArrayInputStream(byteOut.toByteArray());
            byteOut.flush();
            byteOut.close();
            byte[] buffer = new byte[fis.available()];
            int offset = 0;
            int numRead = 0;
            while (offset < buffer.length && (numRead = fis.read(buffer, offset, buffer.length - offset)) >= 0) {
                offset += numRead;
            }
            if (offset != buffer.length) {
                throw new IOException("Could not completely read file ");
            }

            return_arraybyte = buffer;

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("从ftp读取文件失败", e);
        } finally {
            if (byteOut != null) {
                try {
                    byteOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (ins != null) {
                try {
                    ins.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return return_arraybyte;
    }

    public String download(String remotePath, String fileName, String server, int port, String userName, String userPassword) {
        S3Client s3Client = null;
        ByteArrayOutputStream bos = null;
        InputStream is = null;
        ByteArrayInputStream fis = null;
        try {
            s3Client = S3Util.getS3Client();
            is = S3Util.getInputStreamByS3(s3Client, remotePath + fileName);
            if (null == is) {
                throw new FileNotFoundException(remotePath);
            }
            bos = new ByteArrayOutputStream();
            int length;
            byte[] buf = new byte[2048];
            while (-1 != (length = is.read(buf, 0, buf.length))) {
                bos.write(buf, 0, length);
            }
            fis = new ByteArrayInputStream(
                    bos.toByteArray());
            bos.flush();

            byte[] buffer = new byte[fis.available()];
            int offset = 0;
            int numRead = 0;
            while (offset < buffer.length && (numRead = fis.read(buffer, offset, buffer.length - offset)) >= 0) {
                offset += numRead;
            }
            if (offset != buffer.length) {
                throw new IOException("Could not completely read file ");
            }

            return Base64Convert.byteToBase64(buffer);
        } catch (Exception e) {
            logger.error("ftp通过文件名称获取远程文件流", e);
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


    public InputStream download(String fileName) {
        S3Client s3Client = null;
        InputStream is = null;
        try {
            s3Client = S3Util.getS3Client();
            is = S3Util.getInputStreamByS3(s3Client, fileName);
            if (null == is) {
                throw new FileNotFoundException(fileName);
            }
        } catch (Exception e) {
            logger.error("ftp通过文件名称获取远程文件流", e);
        }
        return is;
    }

}
