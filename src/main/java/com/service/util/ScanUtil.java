package main.java.com.service.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: ScanUtil
 * @Description: 扫描工具类
 * @author: liuchen
 * @date: 2024/7/16 0:26
 */

public class ScanUtil {

    public static List<String> scanClassFromDir(String dirPath){
        List<String> classNames = new ArrayList<>();
        //根据传入文件夹路径创建File对象
        File dir = new File(dirPath);
        //检查是否为文件夹
        if (dir.isDirectory()){
            //遍历文件夹内的文件
            for (File f : dir.listFiles()){
                //获取文件名,并删除后缀
                String path = f.getPath();
                path = path.substring(path.indexOf("src\\")+4, path.lastIndexOf("."));
                //添加到结果中
                classNames.add(path.replace('\\','.'));
            }
        }
        return classNames;
    }

    public static void main(String[] args) {
        List<String> strings = scanClassFromDir("./src/main/java/com/service/control/impl");
        for (String string : strings) {
            System.out.println(string);
        }
    }

}
