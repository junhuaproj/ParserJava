#pragma once
#include <jni.h>
#include "AppInfo.h"
class JBridgeAppInfo
{
public:
	JBridgeAppInfo(JNIEnv* e,const jclass cls);
	virtual ~JBridgeAppInfo();
	virtual BOOL Init();
	jobject StructToJObject(const PAppInfo pInfo);
	BOOL JObjectToStruct(jobject obj, PAppInfo pInfo);
protected:
	JNIEnv* _e;
	const jclass _cls;
};

