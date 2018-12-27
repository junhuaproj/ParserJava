# -*- coding: utf-8 -*-
"""
Created on Wed Dec 26 20:20:25 2018

@author: junhua
解析Java 类文件
"""

import sys
from antlr4 import *

from JavaLexer import JavaLexer
from JavaParser import JavaParser

def IsMemberStatic(member):
    #方法或函数是否静态，public,protected,private
    #这里只处理static,影响JNI中取得methodid
    modifiers=member.modifier()
    if(modifiers==None):
        return False
    for m in modifiers:
        m1=m.classOrInterfaceModifier()
        if m1!=None and m.classOrInterfaceModifier().getText()=='static':
            return True
    return False

class JavaDeclaration:
    def __init__(self,javapath):
        self.parseJava(javapath)
        
    '''
    得到Unit，准备解释
    @param path  java 文件路径
    '''
    def parseJava(self,javapath):
        inputStream= FileStream(javapath,encoding='utf-8')
        lexer=JavaLexer(inputStream)
        stream=CommonTokenStream(lexer)
        parser=JavaParser(stream)
        self.unit=parser.compilationUnit()
    '''
    得到Java类的所有信息
    '''
    def getAllJavaInfo(self):
        clssDeclar=self.getClsDeclaration()
        clazz={}
        clazz['pkg']=self.getPackage()
        clazz['name']=self.getClsName(clssDeclar)
        clazz['import']=self.getImports()
        body=self.getJavaClassBody()
        clazz['fields']=self.getFields(body)
        clazz['methods']=self.getMethods(body)
        return clazz
    def getAllJavaInfoJson(self):
        import json
        return json.dumps(self.getAllJavaInfo())
    '''
    得到类定义
    '''        
    def getClsDeclaration(self):
        lst=self.unit.typeDeclaration();
        if lst==None:
            return None
        return lst[0].classDeclaration();
    '''
    得到类所在包
    '''
    def getPackage(self):
        package=self.unit.packageDeclaration()
        if package!=None:
            return package.qualifiedName().getText() 
        return None
    '''
    得到import的类
    '''
    def getImports(self):
        im=self.unit.importDeclaration()
        if(im==None):
            return None
        imports=[]
        for item in im:
            #import的包名称
            imports.append(item.qualifiedName().getText())
        return imports;
            
    '''
    得到类名称
    '''
    def getClsName(self,clsDeclaration):
        return clsDeclaration.IDENTIFIER().getText()
    '''
    得到类
    '''
    def getJavaClassBody(self):
        return  self.getClsDeclaration().classBody()
    '''
    得到类成员方法，包括静态
    '''
    def getMethods(self,body):
        body=body.classBodyDeclaration()
        methods=[]
        for item in body:
            method=item.memberDeclaration();
            if method==None:
                continue
            method=method.methodDeclaration()
            if method!=None:
                #打印方法返回值类型，名称
                m={}
                m['static']=IsMemberStatic(item)
                m['identifier']=method.IDENTIFIER().getText()
                m['return']=method.typeTypeOrVoid().getText()
                #print(method.typeTypeOrVoid().getText()+' '+method.IDENTIFIER().getText())
                paramList=method.formalParameters().formalParameterList()
                if paramList!=None:
                    #得到参数列表
                    mp=[]
                    param=paramList.formalParameter()
                    for p in param:
                        #参数类型，参数名称
                        #print(p.typeType().getText()+' '+p.variableDeclaratorId().getText())
                        mp.append({'type':p.typeType().getText(),'name':p.variableDeclaratorId().getText()})
                    m['params']=mp;
                methods.append(m)
        return methods
    '''
    得到类Field
    '''
    def getFields(self,body):
        body=body.classBodyDeclaration()
        fields=[]
        for item in body:
            field=item.memberDeclaration()
            if field==None:
                continue
            field=field.fieldDeclaration()
            if field!=None:
                fields.append({'type':field.typeType().getText(),'name':field.variableDeclarators().getText(),'static':IsMemberStatic(item)})
        if fields!=None:
            return fields
        return None
          