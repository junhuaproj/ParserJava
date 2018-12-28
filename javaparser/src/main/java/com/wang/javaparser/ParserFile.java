package com.wang.javaparser;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

import com.wang.JavaLexer;
import com.wang.JavaParser;
import com.wang.JavaParser.ClassBodyContext;
import com.wang.JavaParser.ClassBodyDeclarationContext;
import com.wang.JavaParser.ClassDeclarationContext;
import com.wang.JavaParser.FieldDeclarationContext;
import com.wang.JavaParser.FormalParameterContext;
import com.wang.JavaParser.FormalParameterListContext;
import com.wang.JavaParser.FormalParametersContext;
import com.wang.JavaParser.ImportDeclarationContext;
import com.wang.JavaParser.MemberDeclarationContext;
import com.wang.JavaParser.MethodDeclarationContext;
import com.wang.JavaParser.ModifierContext;
import com.wang.JavaParser.PackageDeclarationContext;
import com.wang.JavaParser.TypeDeclarationContext;
import com.wang.JavaParser.VariableDeclaratorContext;
import com.wang.JavaParser.VariableDeclaratorIdContext;
import com.wang.JavaParser.VariableDeclaratorsContext;

public class ParserFile {
	
	public static JavaCls ParseJava(String path)
	{
		ParserFile p=new ParserFile();
		if(p.parse(path))
			return p.cls;
		return null;
	}
	JavaCls cls;
	protected boolean parse(String path)
	{
		JavaParser.CompilationUnitContext unit=getParseTree(path);
		if(unit==null)return false;
		cls=new JavaCls();
		
		PackageDeclarationContext pkg=unit.packageDeclaration();
    	parsePackage(pkg);
    	List<ImportDeclarationContext>  ipts=unit.importDeclaration();
    	parseImpts(ipts);
    	List<TypeDeclarationContext> types=unit.typeDeclaration();
    	parseTypes(types);
		return true;
	}
	//得到包
    public void parsePackage(PackageDeclarationContext pkg)
    {
    	//System.out.println("pkg:"+);
    	if(pkg!=null)
    		cls.setPkg(pkg.qualifiedName().getText());
    }
    //得到Import部分
    public void parseImpts(List<ImportDeclarationContext>  ipts)
    {
    	if(ipts==null)return ;
    	List<String> l=new ArrayList<String>();
    	for(ImportDeclarationContext i: ipts)
    	{
    		l.add(i.qualifiedName().getText());
    		//System.out.println("import:"+i.qualifiedName().getText());
    	}
    	cls.setImports(l);
    }
    public void parseTypes(List<TypeDeclarationContext> types)
    {
    	for(TypeDeclarationContext t: types)
    	{
    		ClassDeclarationContext cls=t.classDeclaration();
    		if(cls!=null)
    			parseClass(cls);
    		//t.interfaceDeclaration();interface
    		//cls.getText();
    	}
    }
    //解析类
    public void parseClass(ClassDeclarationContext cls)
    {
    	//类名称
    	this.cls.setCls(cls.IDENTIFIER().getText());
    	//System.out.println("cls name:"+cls.IDENTIFIER().getText());
    	ClassBodyContext body=cls.classBody();
    	List<ClassBodyDeclarationContext> clsBody= body.classBodyDeclaration();
    	for(ClassBodyDeclarationContext c:clsBody) {
    		//printModifiers(c.modifier());
    		MemberDeclarationContext member=c.memberDeclaration();
    		//printModifiers(c.modifier());
    		boolean isStatic=isStatic(c.modifier());
    		if(member!=null) {
	    		FieldDeclarationContext field=member.fieldDeclaration();
	    		MethodDeclarationContext method=member.methodDeclaration();
	    		if(field!=null)
	    			parseField(isStatic,field);
	    		if(method!=null)
	    			parseMethod(isStatic,method);
    		}
    	}
    }
    //得到Java方法
    public void parseMethod(boolean isStatic,MethodDeclarationContext method)
    {
    	//System.out.println(method.typeTypeOrVoid().getText());
    	//System.out.println(method.IDENTIFIER().getText());
    	JavaMethod jmethod=new JavaMethod();
    	//构造函数没有返回值
    	if(method.typeTypeOrVoid()!=null)
    		jmethod.setRetType(method.typeTypeOrVoid().getText());
    	jmethod.setName(method.IDENTIFIER().getText());
    	jmethod.setStatic(isStatic);
    	FormalParametersContext param= method.formalParameters();
    	FormalParameterListContext paramList=param.formalParameterList();
    	//如果没有参数就直接返回
    	if(paramList==null)return ;
    	List<JavaMember> jparamList=new ArrayList<JavaMember>();
    	List<FormalParameterContext> paramContext=paramList.formalParameter();
    	for(FormalParameterContext p:paramContext)
    	{
    		JavaMember m=new JavaMember();
    		m.setName(p.variableDeclaratorId().IDENTIFIER().getText());
    		m.setType(p.typeType().getText());
    		jparamList.add(m);
    		//System.out.println("type:"+p.typeType().getText()
    		//		+" name:"+p.variableDeclaratorId().IDENTIFIER().getText());
    	}
    	jmethod.setParamList(jparamList);
    	cls.getMethodList().add(jmethod);
    }
    //解析字段
    public void parseField(boolean isStatic,FieldDeclarationContext field)
    {
    	//System.out.println(field.typeType().getText());
    	String type=field.typeType().getText();

    	VariableDeclaratorsContext variable= field.variableDeclarators();
    	List<VariableDeclaratorContext> variables=variable.variableDeclarator();
    	for(VariableDeclaratorContext v:variables) {
    		JavaMember m=new JavaMember();
    		VariableDeclaratorIdContext  variableId=v.variableDeclaratorId();
    		m.setType(type);
    		m.setStatic(isStatic);
    		m.setName(variableId.IDENTIFIER().getText());
    		cls.getFieldList().add(m);
    		//System.out.println("identifier:"+v.variableDeclaratorId().IDENTIFIER().getText());
    	}
    }
    //是否静态字段或方法
    public boolean isStatic(List<ModifierContext> modifiers)
    {
    	if(modifiers==null)return false;
    	for(ModifierContext mc:modifiers) {
    		if(mc.getText().equals("static"))
    			return true;
    	}
    	return false;
    }
    //处理static,public,protected,private
    public void printModifiers(List<ModifierContext> modifiers)
    {
    	for(ModifierContext mc:modifiers) {
    		System.out.println(mc.getText());
    	}
    }
    public JavaParser.CompilationUnitContext getParseTree(String path)
    {
		try {
			ANTLRInputStream input;
			input = new ANTLRInputStream(new FileInputStream(path));
			JavaLexer lexer=new JavaLexer(input);
	    	CommonTokenStream tokens=new CommonTokenStream(lexer);
	    	JavaParser parse=new JavaParser(tokens);
	    	return parse.compilationUnit();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return null;    	
    	//return parse.compilationUnit();
    }
}
