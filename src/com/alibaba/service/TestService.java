package com.alibaba.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class TestService {

	public static void main(String[] args) {

		List<String> list = new ArrayList<>();

		for (int i = 0; i < 100000; i++) {
			list.add("" + i);
		}

		List<Object> resultList = null;
		try {
			resultList = multHandle(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("执行完："+resultList.size());
	}

	static class MyTask implements Callable<Object> {

		private Object data;

		public MyTask(Object data) {
			this.data = data;
		}

		@Override
		public Object call() throws Exception {
			System.out.println(data.toString());

			return data;
		}

	}

	private static List<Object> multHandle(List<String> list) throws Exception {
		ExecutorService exec = Executors.newFixedThreadPool(20);
		List<FutureTask<Object>> futureList = new ArrayList<>();
		MyTask task = null;
		FutureTask<Object> ft = null;
		for (String str : list) {
			task = new MyTask(str);
			ft = new FutureTask<>(task);
			exec.execute(ft);
			futureList.add(ft);
		}

		Object obj = null;

		List<Object> resultList = new ArrayList<>();
		for (FutureTask<Object> futureTask : futureList) {
			obj = futureTask.get();
			if (obj != null) {
				resultList.add(obj);
			}

		}
		return resultList;

	}

}
