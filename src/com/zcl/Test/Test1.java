package com.zcl.Test;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * 从https://gitcode.net/tasking/Encrypt-decrypt-files/-/raw/master/AES.zip 网址下载AES.zip，
 * 编写程序解压AES.zip，
 * 将得到若干经过先经过AES算法加密，
 * 然后经过Base64编码后的文件，
 * 秘钥为1111222233334444，加密模式为CBC，偏移量为5555666677778888，
 * 请解密文件中的内容，并且重新打包成一个压缩包。
 */
public class Test1 {
    public static final int IV_LENGTH_16 = 16;
    public static final int KEY_LENGTH_16 = 16;
    public static final int KEY_LENGTH_32 = 32;
    public static final String AES_CBC_NO_PADDING = "AES/CBC/NoPadding";
    public static final String CBC = "CBC";
    public static final String AES = "AES";

    public static final String KEY = "1111222233334444"; // 加密密钥
    public static final String VAI = "5555666677778888"; // 偏移量


    public static void main(String[] args) throws Exception {
        String url = "https://gitcode.net/tasking/Encrypt-decrypt-files/-/raw/master/AES.zip";
        // 远程下载zip和解压文件
        // DownAndReadFile(url);

        // 调用读取文件并解压的方法
        readeZip(new File("F:/2022C4java基础认证2/远程下载解压解密压缩任务/code/远程请求下载2022-03-13/1.txt")); // hello world
        readeZip(new File("F:/2022C4java基础认证2/远程下载解压解密压缩任务/code/远程请求下载2022-03-13/2.txt"));

        // 重新将数据进行压缩打包
        // 将写出的文件对象添加到一个列表
        /*List<File> fileL = new ArrayList<>();
        fileL.add(new File("F:\\2022C4java基础认证2\\远程下载解压解密压缩任务\\code\\远程请求下载2022-03-13\\解密后的1.txt"));
        fileL.add(new File("F:\\2022C4java基础认证2\\远程下载解压解密压缩任务\\code\\远程请求下载2022-03-13\\解密后的2.txt"));*/
        // File file = new File("F:/2022C4java基础认证2/远程下载解压解密压缩任务/code/重新压缩/重新压缩文件" + System.currentTimeMillis() + ".zip");

    }

    /**
     * 远程文件下载地址
     *
     * @param filePath 网络文件请求地址
     */
    public static void DownAndReadFile(String filePath) {
        long startTime = System.currentTimeMillis();
        // 获取的年月日对象信息
        String data = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        //创建一个下载文件的文件路径
        String dir = "F:\\2022C4java基础认证2\\远程下载解压解密压缩任务\\code\\远程请求下载" + data;
        // 声明文件对象
        File saverPath = new File(dir);
        // 判断文件是否存在
        if (!saverPath.exists()) {
            // 文件不存在就创建一个一级目录【远程请求下载】
            saverPath.mkdir();
        }
        // 根据/切割接受到的请求网络URL
        String[] urlName = filePath.split("/");
        // 获取到切割的字符串数组长度-1
        int len = urlName.length - 1;
        // 获取到请求下载文件的名称
        String uname = urlName[len];
        // System.out.println(uname); // AES.zip

        // 跳过try捕获错误
        try {
            // 创建保存文件对象
            File file = new File(saverPath + "//" + uname);//创建新文件
            if (file != null && !file.exists()) {
                file.createNewFile();
            }
            // 通过高效字节输出流输出创建的文件对象
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
            // 创建URL对象[请求路径]
            URL url = new URL(filePath);
            // 返回一个URLConnection实例，表示与URL引用的远程对象的URL
            HttpURLConnection uc = (HttpURLConnection) url.openConnection();
            uc.setDoInput(true); // 设置的值 doInput领域本 URLConnection指定值。
            uc.connect(); // 打开与此URL引用的资源的通信链接，如果此类连接尚未建立。
            // 获取服务端的字节输入流
            InputStream inputStream = uc.getInputStream();
            System.out.println("file size is：" + uc.getContentLength()); // 打印文件的长度
            // 声明字节数组存放读取的文件
            byte[] b = new byte[1024 * 4];
            int byteRead = -1; // 定义读取次数
            // 循环读取
            while ((byteRead = inputStream.read(b)) != -1) {
                bufferedOutputStream.write(b, 0, byteRead); // 将读取的文件跳过高效的字节流输出
            }
            // 关闭流和刷新流
            inputStream.close();
            bufferedOutputStream.close();
            long endTime = System.currentTimeMillis();
            System.out.println("下载耗时：" + (endTime - startTime) / 1000 * 1.0 + "s");
            System.out.println("文件下载成功！");

            // ---------- 解压文件 ----------
            StringBuffer strb = new StringBuffer();
            // 创建高效的字节输入管道
            BufferedInputStream fs = new BufferedInputStream(new FileInputStream(saverPath + "//" + uname));
            BufferedReader br = new BufferedReader(new InputStreamReader(fs, "UTF-8")); // 指定读取的编码格式); // 高效缓存字节读取
            String date = ""; // 记录读取一行的数据
            // 循环读取
            while ((date = br.readLine()) != null) {
                strb.append(data + "\n"); // 将读取的数据赋值给可变的字符串
            }
            // 关闭相关的流
            br.close();
            fs.close();
            System.out.println("解压文件中...");
            //解压
            unZipFiles(dir + "/AES.zip", dir + "/");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 解压的文件
     *
     * @param zipPath 压缩文件
     * @param descDir 解压存放的位置
     * @throws Exception
     */
    public static void unZipFiles(String zipPath, String descDir) throws Exception {
        System.out.println("解压文件的名称：" + zipPath + "\n解压的文件存放路径：" + descDir);
        unZipFiles(new File(zipPath), descDir); // 调用方法
    }

    /**
     * 解压文件到指定的位置
     *
     * @param zipFile 解压文件
     * @param descDir 存放目录
     */
    @SuppressWarnings("rawtypes") // 抑制警告【原始类型】
    public static void unZipFiles(File zipFile, String descDir) throws Exception {
        // 创存放文件的对象
        File pathFile = new File(descDir);
        // 判断文件是否存在
        if (!pathFile.exists()) {
            // 创建目录[不找到压缩文件里的内容，所以需要创建多级目录]
            pathFile.mkdirs();
        }
        // 创建压缩包条目
        ZipFile zip = new ZipFile(zipFile); // 此类用于从zip文件读取条目。
        // entries() 打开一个ZIP文件，读取指定的File对象。
        for (Enumeration entries = zip.entries(); entries.hasMoreElements(); ) {
            ZipEntry entry = (ZipEntry) entries.nextElement(); // 获取条目
            String zipEntryMame = entry.getName(); // 获取条目名
            InputStream in = zip.getInputStream(entry); // 获取文件的输入流
            String outPath = (descDir + zipEntryMame).replaceAll("\\*", "//"); // 替换全部
            // 判断路径是否存在
            File file = new File(outPath.substring(0, outPath.lastIndexOf("/")));
            // 判断文件，不存在就创建
            if (!file.exists()) {
                file.mkdirs(); // 多级目录
            }
            // 判断文件路径是否为文件
            if (new File(outPath).isDirectory()) {
                continue;
            }
            // 输出文件的路径
            System.out.println(outPath);
            // 创建字节输出流
            FileOutputStream out = new FileOutputStream(outPath);
            // 创建字节数组
            byte[] byf1 = new byte[1024];
            int len;
            while ((len = in.read(byf1)) != -1) {
                out.write(byf1);
            }
            // 关闭流
            in.close();
            out.close();
        }
        System.out.println("文件解压成功");
    }

    /**
     * 读取解压后的文件
     *
     * @param file 文件的位置
     */
    public static void readeZip(File file) throws Exception {
        // 创建高级的字节缓存流读取文件
        try (BufferedReader bdr = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            // 创建字节数组
            StringBuffer sb = new StringBuffer();
            // 读取文件一行信息
            sb.append(bdr.readLine());
            String sb2 = new String(sb);
            System.out.println("解压后的文件内容字符串读取行：" + sb2);

            // -----线程问题导致的冲突
            String s = decryptByCBC(KEY, sb2, VAI, file);

            System.out.println("解密后的内容为：" + s); // AES CBC
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 检查key是否合法
     *
     * @param key {@link String}秘钥信息
     * @throws Exception 异常信息
     */
    private static void checkKey(String key) throws Exception {
        if (key == null || key.length() != KEY_LENGTH_16 && key.length() != KEY_LENGTH_32) {
            throw new Exception("加密秘钥不正确");
        }
    }

    /**
     * 检查偏移矢量是否合法
     *
     * @param iv {@link String} 偏移矢量
     * @throws Exception 异常信息
     */
    private static void checkIV(String iv) throws Exception {
        if (iv == null || iv.length() != IV_LENGTH_16) {
            throw new Exception("偏移矢量不正确，必须为16位");
        }
    }

    /**
     * AES ECB模式解密
     *
     * @param key     加密的秘钥
     * @param encrypt 加密后的内容
     * @param iv      偏移矢量
     * @return 解密后的内容
     * @throws Exception 异常信息
     */
    public static String decryptByCBC(String key, String encrypt, String iv, File file) throws Exception {
        checkKey(key);
        checkIV(iv);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), AES);
        //AES/ECB/PKCS5Padding 格式为 "算法/模式/补码方式"
        Cipher cipher = Cipher.getInstance(AES_CBC_NO_PADDING);
        //偏移矢量
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes());
        //设置为解密模式，解密的key,偏移矢量
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
        //base64解密
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] decode = decoder.decode(encrypt);
        // byte[] decodeBuffer = new BASE64Decoder().decodeBuffer(encrypt);
        //aes解密
        byte[] bytes = cipher.doFinal(decode);
        writFile(file, new String(bytes)); // 将解密模式内容写出文件
        return new String(bytes);
    }

    /**
     * 将解密的信息重新写出一个文件
     *
     * @param file    保存路径
     * @param comtent 写入内容
     */
    public static void writFile(File file, String comtent) throws Exception {
        // 判断文件是否为空
        if (file == null) {
            return;
        }
        String path = file.getAbsolutePath().substring(0, 54) + "解密后的" + file.getName();
        File file1 = new File(path);
        List<File> fileL = new ArrayList<>();
        fileL.add(file1);
        FileOutputStream fos = new FileOutputStream(file1);
        // 写出文件
        fos.write(comtent.getBytes());
        // 关闭流
        fos.close();
        System.out.println("解密文件重新写出，路径为：" + path);
        // 调用文件的压缩方法
        zip(fileL, new File("F:/2022C4java基础认证2/远程下载解压解密压缩任务/code/重新压缩/重新压缩文件" + System.currentTimeMillis() + ".zip"));
    }

    /**
     压缩的思路：
     1、先找到需要压缩的文件目录
     2、将文件压缩到指定的位置
     */


    /**
     * 压缩文件方法
     *
     * @param fileList 需要压缩的文件列表
     * @param destFile 目标文件
     * @throws IOException 异常信息
     */
    public static void zip(List<File> fileList, File destFile) throws IOException {
        if (fileList == null || fileList.isEmpty()) {
            return;
        }
        try {
            ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(destFile)));
            //设置压缩等级，level (0-9)
            zipOutputStream.setLevel(5);
            //遍历文件列表
            for (File file : fileList) {
                //zip文件条目对象
                ZipEntry zipEntry = new ZipEntry(file.getName());
                //将文件条目对象放入zip流中
                zipOutputStream.putNextEntry(zipEntry);
                //将文件信息写入zip流中
                FileInputStream fileInputStream = new FileInputStream(file);
                byte[] bytes = new byte[1024];
                int len = 0;
                while ((len = fileInputStream.read(bytes)) != -1) {
                    zipOutputStream.write(bytes, 0, len);
                }
                zipOutputStream.closeEntry();
                fileInputStream.close();
            }
            zipOutputStream.close();
            System.out.println("解密后的文件压缩成功");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
