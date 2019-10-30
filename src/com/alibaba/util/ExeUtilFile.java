package com.alibaba.util;

import java.io.File;
import java.util.HashMap;
import java.util.PriorityQueue;


public class ExeUtilFile {
	public static void jieya(String filePath) {  
		File f = new File(filePath);  
		FileUtil.hfmCode2File(f);  
	}
	
	public static FileConfig jieyaFc(String filePath) {  
		File f = new File(filePath);  
		return FileUtil.hfmCode2FileFc(f);  
	}
	
	public static FileConfig yasuo(File f) {  
        HashMap<Byte, Integer> map = FileUtil.countByte(f);  
        Tree tree = new Tree();
        // 构建优先队列  
        PriorityQueue<Node> queue = tree.map2Queue(map);  
        // 构建树  
        Node root = tree.queue2Tree(queue);  
        // 获得字节的哈夫曼编码map  
        // tree.ergodicTree(root);  
        HashMap<Byte, String> hfmMap = tree.tree2HfmMap(root);  
        // Set<Byte> set = hfmMap.keySet();  
        // for (Byte b : set) {  
        // System.out.printf("字节为：%d，哈夫曼编码为：%s\n", b, hfmMap.get(b));  
        // }  
        FileConfig fc = FileUtil.file2HfmCode(f, hfmMap);  
        return fc;  
    }  
}
