#include "stdafx.h"
#include "JAppInfoBridge.h"
JAppInfoBridge::JAppInfoBridge(JNIEnv* e,jclass cls):_e(e),_cls(cls)
{
}
JAppInfoBridge::~JAppInfoBridge()
{
}
BOOL JAppInfoBridge::Init(){
	_fId=_e->GetFieldID(_cls,"id","I");
	if(_fId==NULL)return FALSE;
	_fName=_e->GetFieldID(_cls,"name","Ljava/lang/String;");
	if(_fName==NULL)return FALSE;
	_mGetId=_e->GetMethodID(_cls,"getId","()I");
	if(_mGetId==NULL)return FALSE;
	_mSetId=_e->GetMethodID(_cls,"setId","(I)V");
	if(_mSetId==NULL)return FALSE;
	_mGetName=_e->GetMethodID(_cls,"getName","()Ljava/lang/String;");
	if(_mGetName==NULL)return FALSE;
	_mSetName=_e->GetMethodID(_cls,"setName","(Ljava/lang/String;)V");
	if(_mSetName==NULL)return FALSE;
	_mInit=_e->GetMethodID(_cls,"<init>","()V");
	if(_mInit==NULL)return FALSE;
	return TRUE;
}
