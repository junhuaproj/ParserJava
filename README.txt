最近在一个项目中需要开发一个JNI库，需要让Java调用一个Windows DLL。
功能中涉及很多C结构传递参数，并且这些结构里包含了很多字段，到Java代码中用对象。代码相对规范，整齐，借助于Python直接从Java中抽取方法声明，生成部分C代码。这样可以减少手工代码的麻烦，另外还不会减少字段赋值或获取。

antlr4

1.antlr-4.7.2-complete.jar
	从https://www.antlr.org/下载 complete ANTLR jar包
2.java*.g4
	从https://github.com/antlr/grammars-v4下载相应的语法包
	在列表中找到java，然后找到目录下的JavaLexer.g4,JavaParser.g4两个文件都需要
	依次执行
	java -Xmx500M -cp antlr-4.7.2-complete.jar org.antlr.v4.Tool -Dlanguage=Python3 JavaLexer.g4
	java -Xmx500M -cp antlr-4.7.2-complete.jar org.antlr.v4.Tool -Dlanguage=Python3 JavaParser.g4
	得到Python 文件，可以使用
3.测试
	pip install antlr4-python3-runtime
		import sys
		from antlr4 import *

		from JavaLexer import JavaLexer
		from JavaParser import JavaParser


		def getJavaTree(path):
			inputStream= FileStream(path)	#antlr4
			lexer=JavaLexer(inputStream)
			stream=CommonTokenStream(lexer)	#antlr4
			parser=JavaParser(stream)
			return parser.compilationUnit()