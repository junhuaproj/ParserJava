package com.wang.javaparser;

import java.io.File;
//import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

public class Parser {
	//JavaFilter filter=new JavaFilter();
	List<JavaCls> _clss=new ArrayList<JavaCls>();
	public boolean parseFolder(String folder,boolean isChildren)
	{
		File f=new File(folder);
		if(!f.exists())return false;
		if(f.isFile()&&folder.toLowerCase().endsWith(".java"))
		{
			JavaCls c=ParserFile.ParseJava(folder);
			if(c!=null)
				_clss.add(c);
			System.out.println("is file:"+f.getPath());
		}
		else if(f.isDirectory()&&isChildren)
		{
			String[] files=f.list();
			for(String s:files)
			{
				parseFolder(f.getAbsolutePath()+"\\"+s,isChildren);
				//System.out.println(f.getAbsolutePath()+"\\"+s);
			}
		}
		return true;
	}
	public void createCFile(ArrayList<String> clss)
	{
		for(JavaCls c:_clss)
		{
			if(clss.contains(c.getCls()))
			{
				CppClass cpp=new CppClass(c,"J"+c.getCls()+"Bridge");
				cpp.printIdDeclaration();
			}
		}
	}
	
	/*class JavaFilter implements FilenameFilter
	{

		@Override
		public boolean accept(File dir, String name) {
			return name.toLowerCase().endsWith(".java");
		}
		
	}*/
}
