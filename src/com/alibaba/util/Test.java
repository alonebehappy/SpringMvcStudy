package com.alibaba.util;

import java.io.File;

public class Test {
	public static void main(String[] args) {
		File f = new File("F:\\logs.txt");
		long startTime = System.currentTimeMillis();
		FileConfig fc = ExeUtilFile.yasuo(f);
		System.out.println(fc.getFilePath());
		File nFile = new File(fc.getFilePath());
		ExeUtilFile.jieya(fc.getFilePath());
		long endTime = System.currentTimeMillis();
		System.out.println("压缩和解压共花费时间为：" + (endTime - startTime) + "ms");
		Float rate = (float) (nFile.length()) / (f.length());

		System.out.println("压缩比为：" + rate);
	}
}
