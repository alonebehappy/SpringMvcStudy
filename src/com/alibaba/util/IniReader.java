package com.alibaba.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Properties;

@SuppressWarnings("rawtypes")
public class IniReader {
	protected HashMap sections = new HashMap();
	private transient String currentSecion;
	private transient Properties current;

	/**
	 * 构造函数
	 * 
	 * @param filename
	 * @throws IOException
	 */
	public IniReader(String filename) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		read(reader);
		reader.close();
	}

	/**
	 * 构造函数
	 * 
	 * @param sourceReader
	 * @throws IOException
	 */
	public IniReader(Reader sourceReader) throws IOException {
		BufferedReader reader = new BufferedReader(sourceReader);
		read(reader);
		reader.close();
	}

	/**
	 * 读取文件
	 * 
	 * @param reader
	 * @throws IOException
	 */
	protected void read(BufferedReader reader) throws IOException {
		String line;
		while ((line = reader.readLine()) != null) {
			parseLine(line);
		}
	}

	/**
	 * 去除ini文件中的注释，以";"或"#"开头，顺便去除UTF-8等文件的BOM头
	 * 
	 * @param source
	 * @return
	 */
	private String removeIniComments(String source) {
		String result = source;

		if (result.contains(";")) {
			result = result.substring(0, result.indexOf(";"));
		}

		if (result.contains("#")) {
			result = result.substring(0, result.indexOf("#"));
		}

		return result.trim();
	}

	/**
	 * 解析配置文件行
	 * 
	 * @param line
	 */
	@SuppressWarnings("unchecked")
	protected void parseLine(String line) {
		line = removeIniComments(line.trim());
		if (line.matches("\\[.*\\]")) {
			currentSecion = line.replaceFirst("\\[(.*)\\]", "$1");
			current = new Properties();
			sections.put(currentSecion, current);
		} else if (line.matches(".*=.*")) {
			if (current != null) {
				int i = line.indexOf('=');
				String name = line.substring(0, i);
				String value = line.substring(i + 1);
				current.setProperty(name, value);
			}
		}
	}

	/**
	 * 获取值
	 * 
	 * @param section
	 * @param name
	 * @return
	 */
	public String getValue(String section, String name) {
		Properties p = (Properties) sections.get(section);
		if (p == null) {
			return null;
		}
		String value = p.getProperty(name);
		return value;
	}

	/**
	 * 是否包含key
	 * 
	 * @param section
	 * @param name
	 * @return
	 */
	public boolean containsKey(String section, String key) {
		Properties p = (Properties) sections.get(section);
		return p.contains(key);
	}
  
  public static void main(String[] args) throws IOException {

		String str = "[ODBC Data Sources]\r\nSQL Server Legacy Wire Protocol=DataDirect 7.0 SQL Server Legacy Wire Protocol";
		ByteArrayInputStream bis = null;
		bis = new ByteArrayInputStream(str.getBytes());
		InputStreamReader inputStreamReader = new InputStreamReader(bis);
		IniReader reader = new IniReader(inputStreamReader);
		String value = reader.getValue("ODBC Data Sources", "SQL Server Legacy Wire Protocol");
		System.out.println(value);

	}
}
