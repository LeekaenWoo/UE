package com.ry.editor.template.ueditor.upload;

import com.ry.editor.ffmpeg2video.VideoConverter;
import com.ry.editor.srv.utils.KingUtil;
import com.ry.editor.template.ueditor.ConfigManager;
import com.ry.editor.template.ueditor.PathFormat;
import com.ry.editor.template.ueditor.define.AppInfo;
import com.ry.editor.template.ueditor.define.BaseState;
import com.ry.editor.template.ueditor.define.FileType;
import com.ry.editor.template.ueditor.define.State;
import com.sun.media.jai.codec.*;
import com.tt.fastdfs.FastdfsFacade;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.*;

public class BinaryUploader {
	private static Logger logger = LoggerFactory.getLogger(BinaryUploader.class);

	private Properties getPropertyFile(String propertyName){
        Properties props=new Properties();
        try {
            props=PropertiesLoaderUtils.loadAllProperties(propertyName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return props;
    }

	public static final State save(HttpServletRequest request, Map<String, Object> conf) {
		FileItemStream fileStream = null;

		boolean isAjaxUpload = request.getHeader( "X_Requested_With" ) != null;
		boolean isMultipartContent=ServletFileUpload.isMultipartContent(request);
		if (!isMultipartContent) {
			BaseState bs=new BaseState(false, AppInfo.NOT_MULTIPART_CONTENT);
			return bs;
		}
		ServletFileUpload upload = new ServletFileUpload( new DiskFileItemFactory());

        if ( isAjaxUpload ) {
            upload.setHeaderEncoding( "UTF-8" );
        }
        InputStream input = null;
        String originalFilename=null;
		try {
			MultipartHttpServletRequest req= (MultipartHttpServletRequest) request;
			Iterator<String> fileNameIte =req.getFileNames();
			 MultipartFile file = null;
			while(fileNameIte.hasNext()){
	        	String fileName = fileNameIte.next();
	        	file = req.getFile(fileName);
	            originalFilename = file.getOriginalFilename();
	            // 获得输入流：
	            input = file.getInputStream();
	        }


			/*FileItemIterator iterator = upload.getItemIterator(request);

			while (iterator.hasNext()) {
				fileStream = iterator.next();

				if (!fileStream.isFormField())
					break;
				fileStream = null;
			}*/
			//fileStream = (FileItemStream) input;
			if (input == null) {
				return new BaseState(false, AppInfo.NOTFOUND_UPLOAD_DATA);
			}
			String savePath = (String) conf.get("savePath");
			String originFileName = originalFilename;
			String suffix = FileType.getSuffixByFilename(originFileName);

			originFileName = originFileName.substring(0, originFileName.length() - suffix.length());
			savePath = savePath + suffix;

			long maxSize = ((Long) conf.get("maxSize")).longValue();

			if (!validType(suffix, (String[]) conf.get("allowFiles"))) {
				return new BaseState(false, AppInfo.NOT_ALLOW_FILE_TYPE);
			}

			savePath = PathFormat.parse(savePath, originFileName);
			//modified by Ternence
            String rootPath = ConfigManager.getRootPath(request,conf);
            String physicalPath = rootPath + savePath;

			InputStream is = input;
			State storageState = StorageManager.saveFileByInputStream(is, physicalPath, maxSize);
			is.close();
			String pngImgPath = "";
			try{
				/*if(".tiff".equals(suffix)||".tif".equals(suffix)){
					pngImgPath = physicalPath.replaceFirst(suffix,".png");
					logger.info("physicalPath:"+physicalPath+"-----pngImgPath:"+pngImgPath);
					tiff2PNGByJAI(physicalPath, pngImgPath);
					File oldFile = new File(physicalPath);
					//这里删除不掉文件
					if (oldFile.isFile()) {
						oldFile.delete();
						delAllFile(physicalPath);
						logger.info("File:"+oldFile.getPath());
					}

					savePath = savePath.replaceFirst(suffix,".png");
				}*/
                boolean boo = false;
                String formart = "";
                if (".tiff".equals(suffix)) {
                    formart = ".png";
                    pngImgPath = physicalPath.replaceFirst(suffix, formart);
                    boo = tiff2PNGByJAI(physicalPath, pngImgPath);
                    savePath = savePath.replaceFirst(suffix, formart);
                    new File(physicalPath).delete();
                } else if (".tif".equals(suffix)) {
                    formart = ".jpeg";
                    pngImgPath = physicalPath.replaceFirst(suffix, formart);
                    boo = tif2JPEGByJAI(physicalPath, pngImgPath);
                    savePath = savePath.replaceFirst(suffix, formart);
                    new File(physicalPath).delete();
                }
                if (boo) {
                    logger.info("上传图片成功实现" + suffix + "格式转换成" + formart + "格式！");
                }

			}catch (Exception e) {
				e.printStackTrace();
			}
			if(".avi".equals(suffix)){
				try {
                    VideoConverter videoConverter = new VideoConverter();
                    String returnPath = videoConverter.process(physicalPath, "");
                    if (returnPath != null && returnPath.length() != 0) {
						String format = returnPath.substring(returnPath.lastIndexOf(".") + 1, returnPath.length());
						savePath = savePath.replaceFirst(suffix, "." + format);
					}

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            logger.info("上传图片后缀suffix:" + suffix + "-----物理路径physicalPath:" + physicalPath + "-----保存路径savePath:" + savePath);
            String FASTDFS = null, fastdfsRes = null, fdfs_tracker_prefix = null;
            byte[] imgByte = null;
            try {

				FASTDFS = request.getParameter("WEB_URL_FASTDFS");
				if(!isEmpty(FASTDFS) && "1".equals(FASTDFS)){
                    Properties prop = KingUtil.getProper("/fdfs_client.conf");
                    String tracker_prefix = prop.getProperty( "tracker_server");
                    String tracker_http_port = prop.getProperty("http.tracker_http_port");
					fdfs_tracker_prefix ="http://"+tracker_prefix.split(":")[0]+":"+tracker_http_port;
					FastdfsFacade facade = new FastdfsFacade();
					if(".tiff".equals(suffix)||".tif".equals(suffix)){
						imgByte = getBytesByImgPath(pngImgPath);
					}else{
						imgByte = file.getBytes();
					}
					fastdfsRes = facade.uploadFile(imgByte, "fastdfs", "png");
                    if (!isEmpty(fastdfsRes)) {
                        logger.info("成功fastDFS上传图片，即将执行删除原图！");
                        new File(pngImgPath).delete();
                        new File(physicalPath).delete();
                    }
                    logger.info("结束fastDFS上传图片-----fdfs_tracker_prefix:" + fdfs_tracker_prefix + "-----Res:" + fastdfsRes);
                }
            } catch (Exception e) {
                logger.info("异常fastDFS上传图片---------fdfs_tracker_prefix:" + fdfs_tracker_prefix + "-----Exception:" + e);
            }

			if (storageState.isSuccess()) {
				if(!isEmpty(fastdfsRes)){

                    storageState.putInfo("url", fastdfsRes);//fastdfs上传
                    storageState.putInfo("isFastdfsRes", 1); //是否是fastDFS

					storageState.putInfo("tracker_prefix", fdfs_tracker_prefix);//http://172.18.41.140:8080
				}else{
					storageState.putInfo("url", PathFormat.format(savePath));//本地上传
					storageState.putInfo("isFastdfsRes", 0);
				}
				storageState.putInfo("type", suffix);
				storageState.putInfo("original", originFileName + suffix);
			}
            /*logger.debug("physicalPath:"+physicalPath);
            // 记录info级别的信息
			logger.info("physicalPath:"+physicalPath);
			// 记录warn级别的信息
			logger.warn("physicalPath:"+physicalPath);
			// 记录error级别的信息

			logger.error("physicalPath:"+physicalPath);

			logger.info("----------图片save结束----------State="+storageState);*/
            return storageState;
        } catch (IOException e) {
        }
        return new BaseState(false, AppInfo.IO_ERROR);
    }

	private static boolean validType(String type, String[] allowTypes) {
		List<String> list = Arrays.asList(allowTypes);

		return list.contains(type);
	}
	private static boolean isEmpty(String str) {
		return str == null || str =="" || str.isEmpty();
	}
	public static boolean delAllFile(String path) {
		boolean flag = false;
		File file = new File(path);
		if (!file.exists()) {
			return flag;
		}
		if (!file.isDirectory()) {
			return flag;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
				delFolder(path + "/" + tempList[i]);// 再删除空文件夹
				flag = true;
			}
		}
		return flag;
	}
	public static void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); // 删除完里面所有内容
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			myFilePath.delete(); // 删除空文件夹
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 获得指定文件的byte数组
	 */
	public static byte[] getBytesByImgPath(String filePath){
		byte[] buffer = null;
		try {
			File file = new File(filePath);
			FileInputStream fis = new FileInputStream(file);
			ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
			byte[] b = new byte[1000];
			int n;
			while ((n = fis.read(b)) != -1) {
				bos.write(b, 0, n);
			}
			fis.close();
			bos.close();
			buffer = bos.toByteArray();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buffer;
	}
	/**
	 * tiff2PNG
	 */
    public static boolean tiff2PNGByJAI(String input, String output) {
        boolean boo = true;
        OutputStream outputStream = null;
        FileSeekableStream stream = null;
        try {
            stream = new FileSeekableStream(input);
            RenderedOp renderedOp = JAI.create("stream", stream);
            outputStream = new FileOutputStream(output);
            PNGEncodeParam.RGB pngEncodeParam = new PNGEncodeParam.RGB();
            ImageEncoder imageEncoder = ImageCodec.createImageEncoder("PNG", outputStream, pngEncodeParam);//指定格式类型，png 属于 JPEG 类型
            imageEncoder.encode(renderedOp);
        } catch (Exception e) {
            boo = false;
            e.printStackTrace();
        } finally {
            try {
                stream.close();
                if (outputStream != null) outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return boo;
    }

    /**
     * tif2JPEGByJAI
     */
    public static boolean tif2JPEGByJAI(String input, String output) {
        boolean boo = true;
        OutputStream outputStream = null;
        FileSeekableStream stream = null;
        try {
            stream = new FileSeekableStream(input);
            RenderedOp renderedOp = JAI.create("stream", stream);
            outputStream = new FileOutputStream(output);
            JPEGEncodeParam param = new JPEGEncodeParam();
            ImageEncoder imageEncoder = ImageCodec.createImageEncoder("JPEG", outputStream, param);//指定格式类型，png 属于 JPEG 类型
            imageEncoder.encode(renderedOp);
            stream.close();
        } catch (Exception e) {
            boo = false;
            e.printStackTrace();
        } finally {
            try {
                stream.close();
                if (outputStream != null) outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return boo;
    }

	/*public static boolean tiff2PNG(String input,String output){
		boolean boo = false;
		try {
			File inputFile = new File(input);
			final BufferedImage tiff = ImageIO.read(inputFile);
			File outputFile = new File(output);
			boo = ImageIO.write(tiff, "png", outputFile);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
		return boo;
	}
	public static boolean tiff2PNG(InputStream inputStream,String output){
		boolean boo = false;
		try {
			final BufferedImage tiff = ImageIO.read(inputStream);
			if (tiff == null) {return boo;}
			boo = ImageIO.write(tiff, "png",  new File(output));

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
		return boo;
	}*/

	public static void main(String[] args) {
		String physicalPath="C:\\Users\\KING\\Desktop\\demo\\old_test.tiff";
		String imgPath="C:\\Users\\KING\\Desktop\\demo\\old_test.png";
		//tiff2PNGByJAI(physicalPath, imgPath);
		logger.debug("physicalPath:"+physicalPath+"-----imgPath:"+imgPath);
		// 记录info级别的信息
		logger.info("physicalPath:"+physicalPath+"-----imgPath:"+imgPath);
		// 记录warn级别的信息
		logger.warn("physicalPath:"+physicalPath+"-----imgPath:"+imgPath);
		// 记录error级别的信息

		logger.error("physicalPath:"+physicalPath+"-----imgPath:"+imgPath);


	}


}
