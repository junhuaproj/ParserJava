#include "stdafx.h"
#include "JBridgeAppInfo.h"


JBridgeAppInfo::JBridgeAppInfo(JNIEnv* e, const jclass cls):
	_e(e),_cls(cls)
{
}


JBridgeAppInfo::~JBridgeAppInfo()
{
}

BOOL JBridgeAppInfo::Init()
{
	/*_e->GetFieldID()
	_e->GetStaticMethodID
	_e->GetMethodID(_cls,"",)*/
	return true;
}
jobject JBridgeAppInfo::StructToJObject(const PAppInfo pInfo)
{
	return NULL;
}
BOOL JBridgeAppInfo::JObjectToStruct(jobject obj, PAppInfo pInfo)
{
	return FALSE;
}