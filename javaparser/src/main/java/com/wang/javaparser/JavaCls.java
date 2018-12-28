package com.wang.javaparser;

import java.util.ArrayList;
import java.util.List;

public class JavaCls {
	String pkg;
	//不考虑一个Java文件中包含多个Java类的情况
	String cls;
	List<String> imports;
	List<JavaMember> fieldList=new ArrayList<JavaMember>();
	List<JavaMethod> methodList=new ArrayList<JavaMethod>();
	//类型是否是这个Java类
	public boolean isFullThisCls(String cls)
	{
		String fullName=pkg;
		if(fullName==null)
			fullName=this.cls;
		else
			fullName+="."+this.cls;
		return fullName==cls;
	}
	//得到完整的类型
	public String getFullType(String jType)
	{
		if(imports==null)return jType;
		for(String i:imports)
		{
			if(i.endsWith(jType))
				return i;
		}
		return jType;
	}
	public List<String> getImports() {
		return imports;
	}
	public void setImports(List<String> imports) {
		this.imports = imports;
	}
	public String getPkg() {
		return pkg;
	}
	public void setPkg(String pkg) {
		this.pkg = pkg;
	}
	public String getCls() {
		return cls;
	}
	public void setCls(String cls) {
		this.cls = cls;
	}
	public List<JavaMember> getFieldList() {
		return fieldList;
	}
	public void setFieldList(List<JavaMember> fieldList) {
		this.fieldList = fieldList;
	}
	public List<JavaMethod> getMethodList() {
		return methodList;
	}
	public void setMethodList(List<JavaMethod> methodList) {
		this.methodList = methodList;
	}
	
}
