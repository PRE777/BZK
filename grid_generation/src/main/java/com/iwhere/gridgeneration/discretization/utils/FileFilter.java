package com.iwhere.gridgeneration.discretization.utils;

import java.io.File;
import java.io.FilenameFilter;

public class FileFilter implements FilenameFilter {
	
	private String filterCondition = null;
	
	public FileFilter(String filterCondition){
		this.filterCondition = filterCondition;
	}

	@Override
	// name是File中的所有文件或者路径的名称，一级的，而dir是File对象的绝对路径。
	public boolean accept(File dir, String name) {
		int index = name.indexOf(filterCondition);
		return index == -1? false : true;
	}
}