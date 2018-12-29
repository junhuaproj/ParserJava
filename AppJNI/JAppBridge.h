#pragma once

#include <jni.h>

class JAppBridge
{
public:
	JAppBridge(JNIEnv* e,jclass cls);
	virtual ~JAppBridge();
	virtual BOOL Init();
protected:
	JNIEnv* _e;
	jclass _cls;
protected: //field
	jfieldID	_fAppInfo;
protected: //method
	jmethodID	_mMain;
	jmethodID	_mGetAppInfo;
	jmethodID	_mSetAppInfo;
	jmethodID	_mGetInfoClass;
	jmethodID	_mJniSetAppInfo;
	jmethodID	_mJniGetAppInfo;
	jmethodID	_mInit;
public:
	inline void Main(jobject args){
		_e->CallStaticVoidMethod(_cls,_mMain,args);
	};
	inline jobject GetAppInfo(jobject obj){
		return _e->CallObjectMethod(obj,_mGetAppInfo);
	};
	inline void SetAppInfo(jobject obj,jobject appInfo){
		_e->CallVoidMethod(obj,_mSetAppInfo,appInfo);
	};
	inline jobject GetInfoClass(){
		return _e->CallStaticObjectMethod(_cls,_mGetInfoClass);
	};
	inline void JniSetAppInfo(jobject obj,jobject appInfo){
		_e->CallVoidMethod(obj,_mJniSetAppInfo,appInfo);
	};
	inline jobject JniGetAppInfo(jobject obj){
		return _e->CallObjectMethod(obj,_mJniGetAppInfo);
	};
};
