package com.hotpot.ioc.utils;

import com.hotpot.exception.HotSpringException;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

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
        String protocol = url.getProtocol();
        if ("file".equals(protocol)) {
            return fileModelScan(url);
        } else if ("jar".equals(protocol)) {
            return jarModelScan(url);
        }
        throw new HotSpringException("不支持的类型");
    }

    /**
     * 扫描文件夹中的class文件
     */
    private static Map<String, Class> fileModelScan(URL url) {
        File rootFile = new File(url.getPath());
        List<String> classList = new ArrayList<>();
        doScanClassFile(classList, rootFile);

        Map<String, Class> result = new HashMap<>((int) (classList.size() / 0.75) + 1);
        classList.forEach(className -> {
            try {
                result.put(className, Class.forName(className));
            } catch (ClassNotFoundException e) {
                throw new HotSpringException(e);
            }
        });
        return result;
    }

    /**
     * 扫描jar包中的class文件
     */
    private static Map<String, Class> jarModelScan(URL url) {
        JarFile jarFile;
        try {
            jarFile = ((JarURLConnection) url.openConnection()).getJarFile();
        } catch (IOException e) {
            throw new HotSpringException(e);
        }
        Map<String, Class> result = new HashMap<>(16);
        Enumeration<JarEntry> entry = jarFile.entries();
        JarEntry jarEntry;
        while (entry.hasMoreElements()) {
            jarEntry = entry.nextElement();
            // name的格式形如 net/sf/cglib/util/SorterTemplate.class
            String name = jarEntry.getName();
            if (!name.endsWith(".class")) {
                continue;
            }
            name = name.replace("/", ".").replace(".class", "");
            try {
                result.put(name, Class.forName(name));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private static void doScanClassFile(List<String> classList, File file) {
        File[] files = file.listFiles(f -> f.isFile() && f.getName().endsWith(".class") || f.isDirectory());
        for (File f : files != null ? files : new File[0]) {
            if (f.isDirectory()) {
                doScanClassFile(classList, f);
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
