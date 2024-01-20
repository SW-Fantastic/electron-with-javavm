#include "Platform.h"
#include <iostream>
#include <fstream>

using namespace std;

typedef jint(JNICALL* JNICREATEPROC)(JavaVM**, void**, void*);

JNIEnv* env;
JavaVM* jvm;
Handler jvmDLL;

char* int_to_string(int val) {

	int charCount = 0;
	int i = val;
	while (i > 0) {
		i = i / 10;
		charCount++;
	}

	char* buf = new char[charCount + 1];
	sprintf_s(buf, charCount + 1, "%d", val);
	return buf;
}


API_EXPORT int initialize(int udpReceiver, const char* vmPath, const char* classPath, const char* vmOptions, const char* mainClass)
{

	if (jvm != NULL) {
		return true;
	}

	SetDllDirectory(vmPath);
	// ����JVM�Ķ�̬��
	jvmDLL = LoadLibrary(_T("D:/SpecialProjects/ElectronWithJava/Electron/backend/bin/server/jvm.dll"));
	if (jvmDLL == NULL) {
		cout << "[VM Initializer] Error on loading java vm native library" << endl;
		return 0;
	}

	//��ʼ��jvm�����ַ
	JNICREATEPROC jvmProcAddress = (JNICREATEPROC)GetProcAddress(jvmDLL, "JNI_CreateJavaVM");
	if (jvmProcAddress == NULL) {
		FreeLibrary(jvmDLL);
		cout << "[VM Initializer] Error on loading, can not find create vm method." << endl;
		return 1;
	}

	//java���������ʱ���յĲ�����ÿ����������һ��


	int nOptionCount = 0;
	char* options[50] = { NULL };
	char* currentOpt = NULL;

	if (vmOptions != NULL) {

		int lenOptions = strlen(vmOptions);
		char* vmOptionsStr = new char[lenOptions + 1];
		memcpy(vmOptionsStr, vmOptions, lenOptions);
		vmOptionsStr[lenOptions] = '\0';
		// printf("\n %s \n",vmOptionsStr);

		currentOpt = strtok(vmOptionsStr, "\n");
		while (currentOpt) {
			//printf("\n opt: %s",currentOpt);
			options[nOptionCount++] = currentOpt;
			currentOpt = strtok(NULL, "\n");
		}
	}

	//printf("\ncount : %d", nOptionCount);

	JavaVMOption* vmOption = new JavaVMOption[nOptionCount + 1];
	for (int idx = 0; idx < nOptionCount; idx++) {
		JavaVMOption opt;
		opt.optionString = options[idx];
		vmOption[idx + 1] = opt;
		//printf("\n opt is %s", opt.optionString);
	}

	//����classpath
	string opt = (string("-Djava.class.path=") + string(classPath));
	JavaVMOption classPathOpt;
	classPathOpt.optionString = (char*)opt.c_str();
	vmOption[0] = classPathOpt;

	cout << "Classpath is " << opt << endl;

	JavaVMInitArgs vmInitArgs;
	vmInitArgs.version = JNI_VERSION_1_8;
	vmInitArgs.options = vmOption;
	vmInitArgs.nOptions = nOptionCount + 1;
	//�����޷�ʶ��jvm�����
	vmInitArgs.ignoreUnrecognized = JNI_TRUE;


	/*
	* ������쳣��JVM�������ɣ�����Ҫ�������
	*
	* SEGV�����쳣0xC0000005������JVM����ʱ�������ɵģ�����֤ĳЩCPU / OS���ܡ�
	* ĳЩ����ϵͳ�����������������һ�����󣬼� AVX �Ĵ������źŴ�����޷��ָ���
	* ��ˣ�JVM��Ҫ����Ƿ����������,��ˣ���ͨ��д�����ַ�����쳣��Ȼ��������
	*/
	jint jvmProc = (jvmProcAddress)(&jvm, (void**)&env, &vmInitArgs);
	if (jvmProc < 0 || jvm == NULL || env == NULL) {
		FreeLibrary(jvmDLL);
		jvmDLL = NULL;
		cout << "[VM Initializer] Error on creating java environment , failed to init Java VM" << endl;
		return jvmProc;
	}
	// ���Һͼ���MainClass
	jclass mainClazz = env->FindClass(mainClass);
	if (mainClazz == NULL) {
		cout << "[VM Initializer] Error on starting java backend, Class not found." << endl;
		return 3;
	}

	// ����Main����
	jmethodID init = env->GetStaticMethodID(mainClazz, "main", "([Ljava/lang/String;)V");
	if (init == NULL) {
		cout << "[VM Initializer] Error on starting java backend, main method not found." << endl;
		return 4;
	}

	// �ҽ�ʹ��UDP��ʵ�ֺ�˵�ǰ�˵���Ϣͨ�ţ������Ҫһ��UDP�˿ڣ�
	// ����˿ڱ�Nodejs������
	char* recvStr = int_to_string(udpReceiver);
	jobjectArray params = env->NewObjectArray(1, env->FindClass("java/lang/String"), NULL);
	jstring udpParam = env->NewStringUTF(recvStr);
	cout << "[VM Initializer] UDP at port " << udpReceiver << endl;
	env->SetObjectArrayElement(params, 0, udpParam);
	cout << "[VM Initializer] Calling main method..." << endl;
	// ����Main������SpringBoot
	env->CallStaticVoidMethod(mainClazz, init, params);
	cout << "[VM Initializer] Service is ready." << endl;
	delete recvStr;
	return true;
}


void main() {

	char vmOptions[] = "--Xmx1024m\n-javaagent:D:\\SpecialProjects\\ElectronWithJava\\Electron\\agentDemo-1.0-SNAPSHOT.jar";
	initialize(0, "D:\\SpecialProjects\\ElectronWithJava\\Electron\\backend\\bin","D:/SpecialProjects/ElectronWithJava/NativeVMBridge/Build/Release/table-0.0.1-SNAPSHOT.jar", vmOptions, "");

}
