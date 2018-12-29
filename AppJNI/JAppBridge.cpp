#include "stdafx.h"
#include "JAppBridge.h"
JAppBridge::JAppBridge(JNIEnv* e,jclass cls):_e(e),_cls(cls)
{
}
JAppBridge::~JAppBridge()
{
}
BOOL JAppBridge::Init(){
	//_fAppInfo=_e->GetFieldID(_cls,"_fAppInfo","LAppInfo;");
	//if(_fAppInfo==NULL)return FALSE;
	_mMain=_e->GetStaticMethodID(_cls,"main","([Ljava/lang/String;)V");
	if(_mMain==NULL)return FALSE;
	_mGetAppInfo=_e->GetMethodID(_cls,"getAppInfo","()Lcom/wang/jnisample/AppInfo;");
	if(_mGetAppInfo==NULL)return FALSE;
	_mSetAppInfo=_e->GetMethodID(_cls,"setAppInfo","(Lcom/wang/jnisample/AppInfo;)V");
	if(_mSetAppInfo==NULL)return FALSE;
	_mGetInfoClass=_e->GetStaticMethodID(_cls,"getInfoClass","()Ljava/lang/Class;");
	if(_mGetInfoClass==NULL)return FALSE;
	_mJniSetAppInfo=_e->GetMethodID(_cls,"jniSetAppInfo","(Lcom/wang/jnisample/AppInfo;)V");
	if(_mJniSetAppInfo==NULL)return FALSE;
	_mJniGetAppInfo=_e->GetMethodID(_cls,"jniGetAppInfo","()Lcom/wang/jnisample/AppInfo;");
	if(_mJniGetAppInfo==NULL)return FALSE;
	_mInit=_e->GetMethodID(_cls,"<init>","()V");
	if(_mInit==NULL)return FALSE;
	return TRUE;
}
