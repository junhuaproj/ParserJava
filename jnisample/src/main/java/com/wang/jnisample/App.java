package com.wang.jnisample;

import java.util.Random;

import com.google.gson.Gson;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        //System.out.println( "Hello World!" );
    	Gson g=new Gson();
    	//Random rand=new Random();
    	App a=new App();
    	AppInfo info=a.getAppInfo();
    	//info.setId(rand.nextInt());
    	//info.setName("name:"+rand.nextInt());
    	System.out.println("from:"+g.toJson(info));
    	//a.jniSetAppInfo(info);
    	info.setId(info.getId()+1);
    	info.setName("hello world");
    	a.setAppInfo(info);
    	//System.out.println("to:"+g.toJson(info));
    }
    //AppInfo appInfo;
	public AppInfo getAppInfo() {
		return jniGetAppInfo();
	}
	public void setAppInfo(AppInfo appInfo) {
		//this.appInfo = appInfo;
		jniSetAppInfo(appInfo);
	}
    static Class<AppInfo> getInfoClass()
    {
    	return AppInfo.class;
    }
    protected native void jniSetAppInfo(AppInfo appInfo);
    protected native AppInfo jniGetAppInfo(); 
    
    static {
    	System.loadLibrary("AppJNI");
    }
}
