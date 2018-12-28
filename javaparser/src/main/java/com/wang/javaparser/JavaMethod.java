package com.wang.javaparser;

import java.util.List;

public class JavaMethod extends JavaMember{
	String retType;
	List<JavaMember> paramList;
	public String getRetType() {
		return retType;
	}
	public void setRetType(String retType) {
		this.retType = retType;
	}
	public List<JavaMember> getParamList() {
		return paramList;
	}
	public void setParamList(List<JavaMember> paramList) {
		this.paramList = paramList;
	}
	
}
