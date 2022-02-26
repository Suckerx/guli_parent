package stademo;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
import org.junit.Test;

import java.util.Collections;


public class CodeGenerator {
    // 演示例子，执行 main 方法控制台输入模块表名回车自动生成对应项目目录中
    @Test
    public void test(){
        FastAutoGenerator.create("jdbc:mysql://localhost:3306/guli_edu?useUnicode=true&characterEncoding=utf8",
                "root", "123456")
                .globalConfig(builder -> {
                    builder.author("sucker") // 设置作者
                            .enableSwagger() // 开启 swagger 模式
                            .dateType(DateType.ONLY_DATE)
                            .fileOverride()
                            .outputDir("D:\\JavaCode\\gulimall\\guli_parent1\\service\\service_statistics"+"\\src\\main\\java"); // 指定输出目录
                })
                .packageConfig(builder -> {
                    builder.parent("com.sucker") // 设置父包名
                            .moduleName("staservice") // 设置父包模块名
                            .controller("controller")
                            .entity("entity")
                            .service("service")
                            .mapper("mapper")
                            .xml("mapper")
                            .pathInfo(Collections.singletonMap(OutputFile.mapperXml,
                                    "D:\\JavaCode\\gulimall\\guli_parent1\\service\\service_statistics\\src\\main\\resources\\mapper")); // 设置mapperXml生成路径
                })
                .strategyConfig(builder -> {
                    builder.addInclude("statistics_daily") // 设置需要生成的表名
                            .addTablePrefix("t_", "c_") // 设置过滤表前缀
                            .serviceBuilder()
                            .formatServiceFileName("%sService")
                            .entityBuilder()
                            .enableLombok()
                            .logicDeleteColumnName("is_deleted")
                            .enableTableFieldAnnotation()
                            .controllerBuilder()
                            .formatFileName("%sController")
                            .enableRestStyle()
                            .mapperBuilder()
                            .enableBaseResultMap()
                            .superClass(BaseMapper.class)
                            .formatMapperFileName("%sMapper")
                            .enableMapperAnnotation()
                            .formatXmlFileName("%sMapper");
                })
                .templateEngine(new VelocityTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();

    }

}