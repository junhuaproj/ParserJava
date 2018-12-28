
/**
 * 使用解析好的Java类得到生成C++文件
 * */
package com.wang.javaparser;

import java.util.ArrayList;
import java.util.List;

public class CppClass {
	JavaCls jCls;
	String cppName;//C++类名称
	ArrayList<String> cppHeaderContent;//头文件
	ArrayList<String> cppSourceContent;//源文件
	public CppClass(JavaCls cls,String cppName)
	{
		jCls=cls;
		this.cppName=cppName;
		cppHeaderContent=new ArrayList<String>();
		cppSourceContent=new ArrayList<String>();
		beginHeader();
	}
	/**
	 * 头文件开头部分的类定义
	 * */
	public void beginHeader()
	{
		cppHeaderContent.add("#pragram once");
		cppHeaderContent.add("");
		cppHeaderContent.add("#include <jni.h>");
		cppHeaderContent.add("");
		cppHeaderContent.add("class "+cppName);
		cppHeaderContent.add("{");
		cppHeaderContent.add("public:");
		cppHeaderContent.add("\t"+cppName+"(JNIEnv* e,jclass cls);");
	}
	/**
	 * 源文件开头部分
	 * */
	public void beginSource()
	{
		cppSourceContent.add("#include \"stdafx.h\"");
		cppSourceContent.add("#include \""+cppName+".h\"");
		
	}
	/**
	 * 从类变量名称定义fiedId,加前缀_f,并把首字母大写
	 * */
	public String getFieldId(String identifier)
	{
		String s="_f"+identifier.substring(0, 1).toUpperCase();
		if(identifier.length()>1)
			s+=identifier.substring(1);
		return s;
	}
	/**
	 * 从方法名称得到方法ID定义，加前缀_m,并且原首字母大写
	 * */
	public String getMethodId(String identifier)
	{
		String s="_m"+identifier.substring(0, 1).toUpperCase();
		if(identifier.length()>1)
			s+=identifier.substring(1);
		return s;
	}
	/**
	 * 得到Java基本类型的C签名名称
	 * */
	static String GetJavaBaseTypeSig(String jtype)
	{
		switch(jtype)
		{
		case "boolean":return "Z";
		case "byte":return "B";
		case "char":return "C";
		case "short":return "S";
		case "int":return "I";
		case "long":return "J";
		case "float":return "F";
		case "double":return "D";
		case "void":return "V";
		case "String":return "Ljava/lang/String;";
		case "List":return "Ljava/util/List;";
		}
		return null;
	}
	/**
	 * 得到类变量的C签名名称，可能是Java基本数据类型,可能是数组,可能是导入的类
	 * */
	public String getVariableCSign(String jType)
	{
		//是否数组
		boolean isArray=jType.endsWith("[]");
		if(isArray)
			jType=jType.replace("[]", "");
		//根据类型中是否有"<"判断是否是泛型
		int index=jType.indexOf('<');
		if(index!=-1)//如果是泛型
		{
			//删除"<"中的内容
			jType=jType.substring(0,index);
		}
		String cSign=GetJavaBaseTypeSig(jType);
		if(cSign==null)
		{
			//如果是类，把.换成/
			jType=jCls.getFullType(jType);
			jType=jType.replace('.','/');
			//类的开头加"L",末尾加";"
			jType="L"+jType;
			if(isArray)
				jType="["+jType;
			jType+=";";
			return jType;
		}
		if(isArray)
			cSign="["+cSign;
		return cSign;
	}
	/**
	 * 得到方法的签名，包含返回值，参数两部分组成
	 * */
	public String getMethodCSign(JavaMethod method)
	{
		String s="(";
		List<JavaMember> paramList=method.getParamList();
		if(paramList!=null)
		{
			for(JavaMember m:paramList)
			{
				s+=getVariableCSign(m.getType());
			}
		}
		s+=")";
		//注意，有些方法没有返回值，例如构造函数
		if(method.getRetType()==null)
			s+="V";
		s+=getVariableCSign(method.getRetType());
		return s;
	}
	public void printIdDeclaration()
	{
		List<JavaMember> fieldList=jCls.getFieldList();
		if(fieldList!=null)
		{
			System.out.println("protected://field");
			cppHeaderContent.add("protected://field");
			for(JavaMember j:fieldList)
			{
				String fieldId=getFieldId(j.getName());
				//id 定义
				cppHeaderContent.add("\tjfieldID\t"+fieldId);
				//得到field方法
				String gid=String.format("\t%s=_e->%s(_cls,\"%s\",\"%s\")",fieldId,
						j.isStatic?"GetStaticFieldID":"GetFieldID",
						fieldId,getVariableCSign(j.getType()));
				cppSourceContent.add(gid);
				System.out.println(gid);
			}
		}
		List<JavaMethod> methodList=jCls.getMethodList();
		if(methodList!=null)
		{
			System.out.println("protected://method");
			//同时要检查是否有构造函数，如果没有，就使用默认构造函数
			boolean isConstruct=false;
			for(JavaMethod j:methodList)
			{
				String methodId=getMethodId(j.getName());
				if(j.getName()==jCls.getCls())
					isConstruct=true;
				//id 定义
				cppHeaderContent.add("\tjmethodID\t"+methodId);
				//得到method id
				String gid=String.format("\t%s=_e->%s(_cls,\"%s\",\"%s\")",methodId,j.isStatic?"GetStaticMethodID":"GetMethodID",
						methodId,getMethodCSign(j));
				System.out.println(gid);
			}
			if(!isConstruct) {//如果没有构造函数
				String methodId="_mInit";
				cppHeaderContent.add("\tjmethodID\t"+methodId);
				String gid=String.format("\t%s=_e->GetMethodID(_cls,\"<init>\",\"()V\");", methodId);
				System.out.println(gid);
			}
		}
	}
}
