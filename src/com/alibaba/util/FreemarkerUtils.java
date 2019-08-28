package com.alibaba.util;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.PropertyConfigurator;

import com.winning.Application;
import com.winning.polaris.admin.service.impl.UpgradeServiceImpl;
import com.winning.polaris.comm.util.LogUtil;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;

public class FreemarkerUtils {
    private static LogUtil logger = LogUtil.getInstance(UpgradeServiceImpl.class);
    private static String defaultCharacter = "UTF-8";
    private static Configuration cfg;
    private  FreemarkerUtils() {
    }
    static {
        cfg = new Configuration(Configuration.getVersion());
        cfg.setDefaultEncoding(defaultCharacter);
        cfg.setTagSyntax(Configuration.AUTO_DETECT_TAG_SYNTAX);
    }
    /**
     * 对模板进行渲染
     * @param data 数据Map
     * @param tplStr 模板
     * @return
     */
    public static  String generateString(
            Map<String, Object> data,  String tplStr) {
        String result = null;
        String name="myStrTpl";
        try { 
            StringTemplateLoader stringTemplateLoader= new StringTemplateLoader();
            stringTemplateLoader.putTemplate(name, tplStr);
            cfg.setTemplateLoader(stringTemplateLoader); 
            Template template = cfg.getTemplate(name,defaultCharacter);
            StringWriter out = new StringWriter(); 
            template.process(data, out);  
            out.flush();  
            result= out.toString();
            out.close(); 
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return result;
    }
    /**
     * 将模板渲染以后保存到文件
     * @param templateFileDir 模板目录
     * @param fileName 模板文件名称
     * @param targetFilePath 渲染后文件名称
     * @param dataMap 数据
     * @return
     */
    public static boolean renderingTemplateAndGenerateFile(String templateFileDir,
            String fileName,String targetFilePath,Map<String, Object> dataMap){
            boolean flag=true;
            try {
                // 设置文件所在目录的路径  
                cfg.setDirectoryForTemplateLoading(new File(templateFileDir));//模板路径  
                // 获取模版  
                Template template = cfg.getTemplate(fileName);
                // 设置输出文件名,和保存路径  
                File outFile = new File(targetFilePath);
                // 将模板和数据模型合并生成文件 重点设置编码集  
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile), "UTF-8"));  
                // 生成文件  
                template.process(dataMap, out);  
                // 关闭流  
                out.flush();  
                out.close();
            } catch (Exception e) {
                logger.error("生产模板文件失败！",e);
                flag=false;
            }
        return flag;
    }
    
    
    public static void main(String[] args) {
        PropertyConfigurator.configure(Application.class.getClassLoader().getResourceAsStream("config" + File.separator + "log4j.properties"));

        Map<String,Object> dataMap=new HashMap<String, Object>();
        dataMap.put("APP_HOME", "c:/test/appHome");
        //F:\freemark
        boolean renderingTemplateAndGenerateFile = renderingTemplateAndGenerateFile("F:\\freemark\\", "temp.txt",
                "F:\\freemark\\temp.bat",dataMap);
        
        System.out.println(renderingTemplateAndGenerateFile);
    }

}
