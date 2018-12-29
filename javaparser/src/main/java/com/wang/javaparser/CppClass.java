
/**
 * 使用解析好的Java类得到生成C++文件
 * */
package com.wang.javaparser;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CppClass {
	JavaCls jCls;
	String cppName;//C++类名称
	Parser parser;
	ArrayList<String> cppHeaderContent;//头文件
	ArrayList<String> cppSourceContent;//源文件
	public CppClass(JavaCls cls,String cppName,Parser p)
	{
		parser=p;
		jCls=cls;
		this.cppName=cppName;
		cppHeaderContent=new ArrayList<String>();
		cppSourceContent=new ArrayList<String>();
		beginHeader();
		beginSource();
	}
	public boolean save(String dir)
	{
		String fileName=dir+"\\"+cppName+".h";
		if(!writeToFile(cppHeaderContent,fileName))
		{
			return false;
		}
		fileName=dir+"\\"+cppName+".cpp";
		if(!writeToFile(cppSourceContent,fileName))
		{
			return false;
		}
		return true;
	}
	public boolean writeToFile(ArrayList<String> arr,String fileName)
	{
		try {
			FileOutputStream stream=new FileOutputStream(fileName);
			final byte[] enter="\r\n".getBytes();
			for(String s:arr)
			{
				stream.write(s.getBytes());
				stream.write(enter);
			}
			stream.close();
			return true;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	/**
	 * 头文件开头部分的类定义
	 * */
	public void beginHeader()
	{
		cppHeaderContent.add("#pragma once");
		cppHeaderContent.add("");
		cppHeaderContent.add("#include <jni.h>");
		cppHeaderContent.add("");
		cppHeaderContent.add("class "+cppName);
		cppHeaderContent.add("{");
		cppHeaderContent.add("public:");
		cppHeaderContent.add("\t"+cppName+"(JNIEnv* e,jclass cls);");
		cppHeaderContent.add("\tvirtual ~"+cppName+"();");
		cppHeaderContent.add("\tvirtual BOOL Init();");
		cppHeaderContent.add("protected:");
		cppHeaderContent.add("\tJNIEnv* _e;");
		cppHeaderContent.add("\tjclass _cls;");
	}
	public void endHeader()
	{
		cppHeaderContent.add("};");
	}
	/**
	 * 源文件开头部分
	 * */
	public void beginSource()
	{
		cppSourceContent.add("#include \"stdafx.h\"");
		cppSourceContent.add("#include \""+cppName+".h\"");
		//构造函数
		cppSourceContent.add(cppName+"::"+cppName+"(JNIEnv* e,jclass cls):_e(e),_cls(cls)");
		cppSourceContent.add("{");
		cppSourceContent.add("}");
		cppSourceContent.add(cppName+"::~"+cppName+"()");
		cppSourceContent.add("{");
		cppSourceContent.add("}");
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
	public String getMdName(String publicMdName)
	{
		String s=publicMdName.substring(0, 1).toUpperCase();
		if(publicMdName.length()>1)
			s+=publicMdName.substring(1);
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
			if(!jType.contains("."))
			{//如果不是import的，可能是当前代码下的包
				jType=parser.getFullClassName(jType);
			}
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
	/**
	 * 转成CPP类代码
	 * */
	public void printIdDeclaration()
	{
		List<JavaMember> fieldList=jCls.getFieldList();
		cppSourceContent.add("BOOL "+cppName+"::Init(){");
		if(fieldList!=null)
		{
			//System.out.println("protected://field");
			cppHeaderContent.add("protected: //field");
			for(JavaMember j:fieldList)
			{
				String fieldId=getFieldId(j.getName());
				//id 定义
				cppHeaderContent.add("\tjfieldID\t"+fieldId+";");
				//得到field方法
				String gid=String.format("\t%s=_e->%s(_cls,\"%s\",\"%s\");",fieldId,
						j.isStatic?"GetStaticFieldID":"GetFieldID",
								j.getName(),getVariableCSign(j.getType()));
				cppSourceContent.add(gid);
				cppSourceContent.add("\tif("+fieldId+"==NULL)return FALSE;");
				System.out.println(gid);
			}
		}
		List<JavaMethod> methodList=jCls.getMethodList();
		if(methodList!=null)
		{
			//System.out.println("protected: //method");
			cppHeaderContent.add("protected: //method");
			//同时要检查是否有构造函数，如果没有，就使用默认构造函数
			boolean isConstruct=false;
			ArrayList<JavaMethod> publicMd=new ArrayList<JavaMethod>();
			for(JavaMethod j:methodList)
			{
				String methodId=getMethodId(j.getName());
				if(j.getName()==jCls.getCls())
					isConstruct=true;
				if(j.isPublic())
					publicMd.add(j);
				//id 定义
				cppHeaderContent.add("\tjmethodID\t"+methodId+";");
				//得到method id
				String gid=String.format("\t%s=_e->%s(_cls,\"%s\",\"%s\");",methodId,j.isStatic?"GetStaticMethodID":"GetMethodID",
						j.getName(),getMethodCSign(j));
				
				cppSourceContent.add(gid);
				cppSourceContent.add("\tif("+methodId+"==NULL)return FALSE;");
				System.out.println(gid);
			}
			if(!isConstruct) {//如果没有构造函数
				String methodId="_mInit";
				cppHeaderContent.add("\tjmethodID\t"+methodId+";");
				String gid=String.format("\t%s=_e->GetMethodID(_cls,\"<init>\",\"()V\");", methodId);
				//cppSourceContent.add("\t//增加一个默认构造函数");
				cppSourceContent.add(gid);
				cppSourceContent.add("\tif("+methodId+"==NULL)return FALSE;");
				System.out.println(gid);
			}
			//把Public方法增加到头文件中，成为inline的Public C++方法，此部分代码只是生成代码框架，使用时需要修改，例如jstring,需要转换成const char*
			cppHeaderContent.add("public:");
			for(JavaMethod j:methodList)
			{
				String line=String.format("\tinline %s %s(", GetCppType(j.getRetType()),getMdName(j.getName()));
				if(!j.isStatic())
					line+="jobject obj,";
				//把Java参数重新拼成C++参数
				List<JavaMember> paramList=j.getParamList();
				if(paramList!=null) {
					for(JavaMember m:paramList)
					{
						line+=GetCppType(m.getType())+" "+m.getName()+",";
					}
				}
				//多余的“,”删除
				if(line.endsWith(","))
					line=line.substring(0, line.length()-1);
				line+="){";
				cppHeaderContent.add(line);
				line="\t\t";
				if(!j.getRetType().contentEquals("void"))
					line+="return ";
				line+="_e->"+GetCppCallMethod(j.getRetType(),j.isStatic());
				line+="(";
				if(j.isStatic())
					line+="_cls,";
				else
					line+="obj,";
				line+=getMethodId(j.getName())+",";
				//处理参数部分
				if(paramList!=null) {
					for(JavaMember m:j.getParamList())
					{
						line+=m.getName()+",";
					}
				}
				//多余的“,”删除
				line=line.substring(0, line.length()-1);
				line+=");";
				cppHeaderContent.add(line);
				
				cppHeaderContent.add("\t};");
			}
		}
		cppSourceContent.add("\treturn TRUE;");
		cppSourceContent.add("}");
	}
	/**
	 * 从Java类型转成CPP类型
	 * */
	String GetCppType(String jType)
	{
		String methodType="jobject";
		switch(jType)
		{
		case "void":methodType= "void";break;
		case "boolean":methodType="BOOL";break;
		case "Byte":methodType="unsigned short";break;
		case "Char":methodType="short";break;
		case "Short":methodType="Short";break;
		case "int":methodType="int";break;
		case "long":methodType="__int64";break;
		case "float":methodType="float";break;
		case "double":methodType="double";break;
		case "String":methodType="const char*";break;
		}
		return methodType;
	}
	String GetCppCallMethod(String retType,boolean isStatic)
	{
		String methodType=getCppCall(retType);
		if(isStatic)
		{
			return "CallStatic"+methodType+"Method";
		}
		return "Call"+methodType+"Method";
	}
	//根据Java方法返回值选择合适的JNI调用类型，使用时再拼成JNI方法
	String getCppCall(String retType)
	{
		String methodType="Object";
		switch(retType)
		{
		case "void":methodType= "Void";break;
		case "boolean":methodType="Boolean";break;
		case "Byte":methodType="Byte";break;
		case "Char":methodType="Char";break;
		case "Short":methodType="Short";break;
		case "int":methodType="Int";break;
		case "long":methodType="Long";break;
		case "float":methodType="Float";break;
		case "double":methodType="Double";break;
		}
		return methodType;
	}
}
