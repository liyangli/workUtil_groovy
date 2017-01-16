package com.bohui.utils;


import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

/**
 * Zip工具类，支持通过文件目录进行压缩
 * User: liyangli
 * Date: 2015/4/14
 * Time: 13:48
 */
public class ZipUtils {


    /**
     * 处理文件进行执行压缩
     * @param filePath 压缩文件路径
     * @param zipFile 压缩后的文件
     */
    public static void fileToZip(String filePath,String zipFile) throws Exception{
        File file = new File(filePath);
        if(!file.exists()){
            throw new FileNotFoundException("文件没有发现");
        }
        FileOutputStream out = new FileOutputStream(zipFile);
        ZipOutputStream zipOut = new ZipOutputStream(out);
        fileToZip(zipOut,file,"");
        zipOut.setEncoding("UTF-8");
        zipOut.close();
    }

    /**
     * 批量文件进行压缩到指定文件中
     * @param filePaths  需要压缩的文件
     * @param zipPath 压缩成的文件名称
     * @throws Exception
     */
    public static void fileToZip(List<String> filePaths,String zipPath) throws Exception{
        File zipFile = new File(zipPath);
        File zipParent = zipFile.getParentFile();
        if(!zipParent.exists()){
            zipParent.mkdirs();
        }
        FileOutputStream out = new FileOutputStream(zipPath);
        ZipOutputStream zipOut = new ZipOutputStream(out);
        for(String filePath : filePaths){
            makeZip(zipOut, "", "",new  File(filePath));
        }
        zipOut.close();
    }

    private static void fileToZip(ZipOutputStream out,File parentFile,String path) throws Exception{
        File[] files = parentFile.listFiles();
        String parentName = parentFile.getName();
        for(File file:files){
            makeZip(out, path, parentName, file);
        }
    }

    private static void makeZip(ZipOutputStream out, String path, String parentName, File file) throws Exception {
        String fileName = file.getName().replace(parentName,"");

        if(file.isDirectory()){
            //目录
            fileToZip(out,file,path+"/"+fileName);
            return;
        }
        ZipEntry zipEntry = new ZipEntry(path+fileName);

        out.putNextEntry(zipEntry);
        FileInputStream in = new FileInputStream(file);
        int b;
        byte[] by = new byte[1024];
        while ((b = in.read(by)) != -1) {
            out.write(by, 0, b);
        }
        in.close();
    }

    public static void main(String[] args) throws Exception{
        //文件压缩
        ZipUtils.fileToZip("E:\\demo\\db","E:\\demo\\test.zip");
    }

}
