package com.wang.javaparser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;

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
import com.wang.JavaParser.VariableDeclaratorsContext;
/**
 * java -Xmx500M -cp antlr-4.7.2-complete.jar org.antlr.v4.Tool -Dlanguage=Java -package com.wang JavaLexer.g4
 * java -Xmx500M -cp antlr-4.7.2-complete.jar org.antlr.v4.Tool -Dlanguage=Java -package com.wang JavaParser.g4
 * */
/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	String path="C:\\proj\\files\\javaparser\\App.java";
    	App a=new App();
    	try {
    		a.main(path);
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
        System.out.println( "Hello World!" );
    }
    public void main(String path) throws FileNotFoundException, IOException
    {
    	//https://github.com/antlr/antlr4/blob/4.6/doc/listeners.md
    	//JavaCls j=ParserFile.ParseJava(path);
    	//System.out.println(j);
    	Parser p=new Parser();
    	p.parseFolder("C:\\proj\\files\\javaparser\\javaparser\\src\\main\\java", true);
    	ArrayList<String> clsNames=new ArrayList<String>();
    	clsNames.add("JavaCls");
    	clsNames.add("CppClass");
    	p.createCFile(clsNames);
    	//ParseTree tree=getParseTree(path);
    	//AST ast=new AST(tree);
    	//System.out.println(ast);
    }
    
    //public MyJavaListener extends Parse
    public class AST{
    	final private Object payload;
    	final private List<AST> children;
    	public AST(ParseTree tree)
    	{
    		this(null,tree,new ArrayList<AST>());
    	}
    	private AST(AST ast, ParseTree tree) {
            this(ast, tree, new ArrayList<AST>());
        }

        private AST(AST parent, ParseTree tree, List<AST> children) {

            this.payload = getPayload(tree);
            this.children = children;

            if (parent == null) {
                walk(tree, this);
            }
            else {
                parent.children.add(this);
            }
        }

        public Object getPayload() {
            return payload;
        }

        public List<AST> getChildren() {
            return new ArrayList<>(children);
        }

        private Object getPayload(ParseTree tree) {
            if (tree.getChildCount() == 0) {
                return tree.getPayload();
            }
            else {
                String ruleName = tree.getClass().getSimpleName().replace("Context", "");
                return Character.toLowerCase(ruleName.charAt(0)) + ruleName.substring(1);
            }
        }

        private void walk(ParseTree tree, AST ast) {

            if (tree.getChildCount() == 0) {
                new AST(ast, tree);
            }
            else if (tree.getChildCount() == 1) {
                walk(tree.getChild(0), ast);
            }
            else if (tree.getChildCount() > 1) {

                for (int i = 0; i < tree.getChildCount(); i++) {

                    AST temp = new AST(ast, tree.getChild(i));

                    if (!(temp.payload instanceof Token)) {
                        walk(tree.getChild(i), temp);
                    }
                }
            }
        }

        @Override
        public String toString() {

            StringBuilder builder = new StringBuilder();

            AST ast = this;
            List<AST> firstStack = new ArrayList<>();
            firstStack.add(ast);

            List<List<AST>> childListStack = new ArrayList<>();
            childListStack.add(firstStack);

            while (!childListStack.isEmpty()) {

                List<AST> childStack = childListStack.get(childListStack.size() - 1);

                if (childStack.isEmpty()) {
                    childListStack.remove(childListStack.size() - 1);
                }
                else {
                    ast = childStack.remove(0);
                    String caption;

                    if (ast.payload instanceof Token) {
                        Token token = (Token) ast.payload;
                        caption = String.format("TOKEN[type: %s, text: %s]",
                                token.getType(), token.getText().replace("\n", "\\n"));
                    }
                    else {
                        caption = String.valueOf(ast.payload);
                    }

                    String indent = "";

                    for (int i = 0; i < childListStack.size() - 1; i++) {
                        indent += (childListStack.get(i).size() > 0) ? "|  " : "   ";
                    }

                    builder.append(indent)
                            .append(childStack.isEmpty() ? "'- " : "|- ")
                            .append(caption)
                            .append("\n");

                    if (ast.children.size() > 0) {
                        List<AST> children = new ArrayList<>();
                        for (int i = 0; i < ast.children.size(); i++) {
                            children.add(ast.children.get(i));
                        }
                        childListStack.add(children);
                    }
                }
            }

            return builder.toString();
        }
    }
}
