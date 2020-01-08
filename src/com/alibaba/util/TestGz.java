package com.alibaba.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.lang.StringUtils;

public class TestGz {

	public static void main(String[] args) throws Exception {

		String archiveFileName = "G://plbuylast_slow_2020-01-06-14_36_05_2020-01-06-14_38_15.log";
		File tarGzFile = new File(archiveFileName + ".tar.gz");
		FileInputStream tarGzFileIs = null;
		CompressorInputStream cis = null;
		TarArchiveInputStream tis = null;
		BufferedReader br = null;
		tarGzFileIs = new FileInputStream(tarGzFile);
		cis = new GzipCompressorInputStream(tarGzFileIs, true);
		tis = new TarArchiveInputStream(cis);
		TarArchiveEntry entry = tis.getNextTarEntry();
		StringBuffer ret = new StringBuffer();

		while (entry != null) {
			String _snapshotID = "json";
			if (entry.getName().endsWith(_snapshotID)) {
				InputStreamReader inr = new InputStreamReader(tis, "UTF8");// 考虑到编码格式
				br = new BufferedReader(inr);// 缓冲流
				String str = null;
				while ((str = br.readLine()) != null) {
					if (StringUtils.isNotBlank(str)) {
						ret.append(str);
					}
				}
				br.close();

				break;
			}
			entry = tis.getNextTarEntry();
		}

		System.out.println(ret.toString());
	}

}
