package com.alibaba.util;

import java.util.HashMap;

public class FileConfig {  
  
    // 文件路径  
    private String filePath;  
    // 文件字节的哈夫曼编码映射  
    private HashMap<Byte, String> hfmCodeMap; 
    
    private boolean success;
  
    /** 
     * 构造方法 
     *  
     * @param filePath 
     *            文件路径 
     * @param hfmCodeMap 
     *            文件字节的哈夫曼编码映射 
     */  
    public FileConfig(String filePath, HashMap<Byte, String> hfmCodeMap) {  
        super();  
        this.filePath = filePath;  
        this.hfmCodeMap = hfmCodeMap;  
    }
    
    public FileConfig(String filePath, HashMap<Byte, String> hfmCodeMap,boolean success) {  
        super();  
        this.filePath = filePath;  
        this.hfmCodeMap = hfmCodeMap;
        this.success = success;
    } 
  
    public String getFilePath() {  
        return filePath;  
    }  
  
    public void setFilePath(String filePath) {  
        this.filePath = filePath;  
    }  
  
    public HashMap<Byte, String> getHfmCodeMap() {  
        return hfmCodeMap;  
    }  
  
    public void setHfmCodeMap(HashMap<Byte, String> hfmCodeMap) {  
        this.hfmCodeMap = hfmCodeMap;  
    }  
  
    @Override  
    public String toString() {  
        return "FileConfig [filePath=" + filePath + ", hfmCodeMap=" + hfmCodeMap + "]";  
    }

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}  
      
}
