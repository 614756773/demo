package com.hotpot.ioc.utils;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qinzhu
 * @since 2020/1/8
 */
public class ClassScanner {
    /**
     * (packageName, (className, class))
     */
    private static Map<String, Map<String, Class>> cache = new HashMap<>();

    /**
     * 根据包名扫描所有class文件
     */
    public synchronized static Map<String, Class> listClass(String packageName) {
        return cache.computeIfAbsent(packageName, ClassScanner::scanClass);
    }

    private static Map<String, Class> scanClass(String packageName) {
        URL url = ClassLoader.getSystemResource(packageName.replace(".", "/"));
        File rootFile = new File(url.getPath());
        List<String> classList = new ArrayList<>();
        doScanClass(classList, rootFile);

        Map<String, Class> result = new HashMap<>((int)(classList.size() / 0.75) + 1);
        classList.forEach(className -> {
            try {
                result.put(className, Class.forName(className));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });

        return result;
    }

    private static void doScanClass(List<String> classList, File file) {
        File[] files = file.listFiles(f -> f.isFile() && f.getName().endsWith(".class") || f.isDirectory());
        for (File f : files != null ? files : new File[0]) {
            if (f.isDirectory()) {
                doScanClass(classList, f);
            } else {
                classList.add(convertToClassName(f.getPath()));
            }
        }
    }

    /**
     * 将形如"C:\Users\HotPot\Desktop\my\ioc\target\classes\com\hotpot\ioc\test\StudentServiceImpl.class"的字符转换为
     * "com.hotpot.test.StudentServiceImpl"
     */
    private static String convertToClassName(String path) {
        int begin = path.indexOf("classes\\") + "classes\\".length();
        int end = path.indexOf(".class");
        return path.substring(begin, end).replace("\\", ".");
    }
}
