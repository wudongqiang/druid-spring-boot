package com.github.trang.druid.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.github.trang.druid.datasource.DruidMultiDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 多数据源配置，只在 #{@code spring.profiles.active=dynamic} 时生效
 *
 * @author trang
 */
@Configuration
@Profile({"dynamic", "dynamic-dev-yaml", "dynamic-dev-props"})
public class SpringDataSourceConfig {

    private static final Logger log = LoggerFactory.getLogger(SpringDataSourceConfig.class);

    /**
     * 第一个数据源，注意数据源类型为 #{@link com.github.trang.druid.datasource.DruidMultiDataSource}
     *
     * `spring.datasource.druid.one` 前缀的配置会注入到该 Bean，同时会继承 `spring.datasource.druid`
     * 前缀的配置，若名称相同则会被 `spring.datasource.druid.one` 覆盖
     */
    @Bean(initMethod = "init", destroyMethod = "close")
    @ConfigurationProperties("spring.datasource.druid.one")
    public DruidDataSource firstDataSource() {
        log.debug("druid first-data-source init...");
        return new DruidMultiDataSource();
    }

    /**
     * 第二个数据源，若还有其它数据源可以继续增加
     */
    @Bean(initMethod = "init", destroyMethod = "close")
    @ConfigurationProperties("spring.datasource.druid.two")
    public DruidDataSource secondDataSource() {
        log.debug("druid second-data-source init...");
        return new DruidMultiDataSource();
    }

    @Bean
    @Primary
    public DynamicDataSource dataSource(DruidDataSource firstDataSource, DruidDataSource secondDataSource) {
        Map<String, DataSource> targetDataSources = new HashMap<>();
        targetDataSources.put(firstDataSource.getName(), firstDataSource);
        targetDataSources.put(secondDataSource.getName(), secondDataSource);
        return new DynamicDataSource(firstDataSource, targetDataSources);
    }

}