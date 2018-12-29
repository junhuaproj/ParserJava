// AppJNI.cpp : 定义 DLL 应用程序的导出函数。
//

#include "stdafx.h"
#include <jni.h>
#include "com_wang_jnisample_App.h"
#include "AppInfo.h"
#include "JAppInfoBridge.h"
#include "JAppBridge.h"
AppInfo g_appInfo = {1,"hello"};
JNIEXPORT void JNICALL Java_com_wang_jnisample_App_jniSetAppInfo
(JNIEnv *e, jobject app, jobject info) {
	JAppInfoBridge bridge(e, e->GetObjectClass(info));
	if (!bridge.Init())
	{
		printf("bridge error\n");
		return;
	}
	g_appInfo.id = bridge.GetId(info);
	bridge.GetName(info, &g_appInfo);
	printf("%d,%s\n",g_appInfo.id,g_appInfo.name);
}

/*
* Class:     com_wang_jnisample_App
* Method:    jniGetAppInfo
* Signature: ()Lcom/wang/jnisample/AppInfo;
*/
JNIEXPORT jobject JNICALL Java_com_wang_jnisample_App_jniGetAppInfo
(JNIEnv *e, jobject app) {
	JAppBridge appBridge(e, e->GetObjectClass(app));
	if (!appBridge.Init())return NULL;
	jclass appInfoCls=(jclass)appBridge.GetInfoClass();

	JAppInfoBridge infoBridge(e, appInfoCls);
	if (!infoBridge.Init())
	{
		e->DeleteLocalRef(appInfoCls);
		return NULL;
	}
	jobject appinfo= infoBridge.getObject(&g_appInfo);
	e->DeleteLocalRef(appInfoCls);
	return appinfo;
}
