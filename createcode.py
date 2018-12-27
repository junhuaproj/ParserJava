# -*- coding: utf-8 -*-
"""
Created on Thu Dec 27 10:33:56 2018

@author: junhua
"""

from JavaDeclaration import JavaDeclaration

'''
如果是Java基础类型
'''
def getJavaBaseTypeSig(jType):
    if(jType=='boolean'):
        return 'Z'
    elif jType=='byte':
        return 'B'
    elif(jType=='char'):
        return 'C'
    elif(jType=='short'):
        return 'S'
    elif(jType=='int'):
        return 'I'
    elif(jType=='long'):
        return 'J'
    elif(jType=='float'):
        return 'F'
    elif(jType=='double'):
        return 'D'
    return None

class Code:
    def __init__(self,javaFile):
        self.javaFile=javaFile
        
    def parseJava(self):
        javaCls=JavaDeclaration(self.javaFile)
        self.javaCls=javaCls.getAllJavaInfo()
        return self.javaCls!=None
    
    def getJavaTypeSig(self,jType):
        r=getJavaBaseTypeSig(jType)
        if r==None:
            #java内置类在rt.jar中定义，是指不需要import就可以使用的类
            #这里应当返回完整的类定义，暂时简化
            return jType
        return r
    def getMethodSig(self,retType,params):
        ret='('
        if params!=None:
            for p in params:
                if p['type']=='void':
                    continue
                r=self.getJavaTypeSig(p['type'])
                ret=ret+r
        ret=ret+')'
        if retType=='void':
            ret=ret+'V'
        else:
            r=self.getJavaTypeSig(retType)
            ret=ret+r;
        return ret
    #得到头文件中的类成员变量定义
    def getCHeaderDeclaration(self):
        methods=self.javaCls['methods']
        for method in methods:
            params=method.get('params')
            #if method.has_key('params'):
            #    params=method['params']
            #GetStaticMethodID
            #GetMethodID
            print(self.getMethodSig(method['return'],params))