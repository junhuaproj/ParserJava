#pragma once

#include <jni.h>
#include "AppInfo.h"
class JAppInfoBridge
{
public:
	JAppInfoBridge(JNIEnv* e,jclass cls);
	virtual ~JAppInfoBridge();
	virtual BOOL Init();
protected:
	JNIEnv* _e;
	jclass _cls;
protected: //field
	jfieldID	_fId;
	jfieldID	_fName;
protected: //method
	jmethodID	_mGetId;
	jmethodID	_mSetId;
	jmethodID	_mGetName;
	jmethodID	_mSetName;
	jmethodID	_mInit;
public:
	jobject getObject(const PAppInfo pApp)
	{
		jobject o = _e->NewObject(_cls, _mInit);
		SetId(o, pApp->id);
		SetName(o, pApp->name);
		return o;
	}
	inline int GetId(jobject obj){
		return _e->CallIntMethod(obj,_mGetId);
	};
	inline void SetId(jobject obj,int id){
		_e->CallVoidMethod(obj,_mSetId,id);
	};
	inline const char* GetName(jobject obj,PAppInfo pApp){
		jstring str = (jstring)_e->CallObjectMethod(obj, _mGetName);
		const char* szName=_e->GetStringUTFChars(str, NULL);
		strcpy_s(pApp->name, szName);
		_e->ReleaseStringUTFChars(str, szName);
		return pApp->name;
	};
	inline void SetName(jobject obj,const char* name){
		jstring str = _e->NewStringUTF(name);
		_e->CallVoidMethod(obj,_mSetName,str);
		_e->DeleteLocalRef(str);
	};
};
