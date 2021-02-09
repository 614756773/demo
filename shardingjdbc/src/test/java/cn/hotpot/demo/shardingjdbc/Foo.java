package cn.hotpot.demo.shardingjdbc;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.pool.HikariPool;
import lombok.SneakyThrows;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySourcesPropertyResolver;
import org.springframework.core.env.SystemEnvironmentPropertySource;
import org.springframework.util.StopWatch;

import java.util.Map;
import java.util.Properties;

/**
 * @author qinzhu
 * @since 2021/2/8
 */
public class Foo {
    @SneakyThrows
    public static void main(String[] args) {
    }

    private static void propertyResolver() {
        SystemEnvironmentPropertySource propertySource = new SystemEnvironmentPropertySource("asd", (Map) System.getenv());
        MutablePropertySources sources = new MutablePropertySources();
        sources.addFirst(propertySource);
        PropertySourcesPropertyResolver resolver = new PropertySourcesPropertyResolver(sources);
        System.out.println(resolver.getProperty("Path"));
    }

    private static void stopWatch() throws InterruptedException {
        StopWatch stopWatch = new StopWatch("123");
        stopWatch.start("1");
        Thread.sleep(10000);
        stopWatch.stop();

        stopWatch.start("2");
        Thread.sleep(2000);
        stopWatch.stop();

        stopWatch.start("3");
        Thread.sleep(1000);
        stopWatch.stop();
        System.out.println(stopWatch.prettyPrint());
    }

    private static void a() throws ClassNotFoundException {
        ClassLoader classLoader = Class.forName("com.mysql.cj.jdbc.Driver").getClassLoader();
        Thread.currentThread().setContextClassLoader(classLoader);
        HikariConfig config = new HikariConfig();
        Properties properties = new Properties();
        properties.put("minIdle", 2);
        config.setJdbcUrl("jdbc:mysql://localhost:3306/test?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8&useSSL=false");
        config.setUsername("root");
        config.setPassword("123456");
        config.setMinimumIdle(1);
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setDataSourceProperties(properties);
        HikariPool pool = new HikariPool(config);
        System.out.println(config);
    }
}
