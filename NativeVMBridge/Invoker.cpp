#include "Platform.h"
#include <iostream>
#include <fstream>

using namespace std;

typedef jint(JNICALL* JNICREATEPROC)(JavaVM**, void**, void*);

JNIEnv* env;
JavaVM* jvm;
Handler jvmDLL;

ofstream ofsLogger;

char* int_to_string(int val) {

	int charCount = 0;
	int i = val;
	while (i > 0) {
		i = i / 10;
		charCount++;
	}

	char* buf = new char[charCount + 1];
	sprintf_s(buf, charCount + 1,"%d", val);
	return buf;
}

API_EXPORT int initialize(int udpReceiver, const char* vmLocation, const char* classPath,const char* vmOptions, const char* mainClass)
{

	if (jvm != NULL) {
		return true;
	}

	ofsLogger.open("napi.log",ios::out);

// 加载JVM的动态库
#ifdef _WIN32
	ofsLogger << "vmPath: " << vmLocation << endl;
	SetDllDirectory(vmLocation);
	jvmDLL = LoadLibrary(_T("server/jvm.dll"));
#endif
#ifdef __APPLE__
	jvmDLL = LoadLibrary(_T("backend/bin/server/jvm.dylib"));
#endif
	if (jvmDLL == NULL) {
		ofsLogger << "[VM Initializer] Error on loading java vm native library" << endl;
		ofsLogger.close();
		return -1;
	}

	//初始化jvm物理地址
	JNICREATEPROC jvmProcAddress = (JNICREATEPROC)GetProcAddress(jvmDLL, "JNI_CreateJavaVM");
	if (jvmProcAddress == NULL) {
		FreeLibrary(jvmDLL);
		ofsLogger << "[VM Initializer] Error on loading, can not find create vm method." << endl;
		ofsLogger.close();
		return -2;
	}

	//java虚拟机启动时接收的参数，每个参数单独一项

	int nOptionCount = 0;
	char* options[50] = { NULL };
	char* currentOpt = NULL;

	if (vmOptions != NULL) {

		int lenOptions = strlen(vmOptions);
		char* vmOptionsStr = new char[lenOptions + 1];
		memcpy(vmOptionsStr, vmOptions, lenOptions);
		vmOptionsStr[lenOptions] = '\0';

		currentOpt = strtok(vmOptionsStr, "\n");
		while (currentOpt) {
			ofsLogger << "opt:" << currentOpt << endl;
			options[nOptionCount++] = currentOpt;
			currentOpt = strtok(NULL, "\n");
		}
	}

	JavaVMOption* vmOption = new JavaVMOption[nOptionCount + 1];
	for (int idx = 0; idx < nOptionCount; idx++) {
		JavaVMOption opt;
		opt.optionString = options[idx];
		vmOption[idx + 1] = opt;
	}

	//设置classpath
	string opt = (string("-Djava.class.path=") + string(classPath));
	JavaVMOption classPathOpt;
	classPathOpt.optionString = (char*)opt.c_str();
	vmOption[0] = classPathOpt;

	ofsLogger << "Classpath is " << opt << endl;

	JavaVMInitArgs vmInitArgs;
	vmInitArgs.version = JNI_VERSION_1_8;
	vmInitArgs.options = vmOption;
	vmInitArgs.nOptions = nOptionCount + 1;
	//忽略无法识别jvm的情况
	vmInitArgs.ignoreUnrecognized = JNI_TRUE;
	
	/*
	* 这里的异常是JVM主动生成，不需要理会它。
	* 
	* SEGV（或异常0xC0000005）是在JVM启动时有意生成的，以验证某些CPU / OS功能。
	* 某些操作系统或虚拟机管理程序存在一个错误，即 AVX 寄存器在信号处理后无法恢复。
 	* 因此，JVM需要检查是否是这种情况,因此，它通过写入零地址生成异常，然后处理它。
	*/
	jint jvmProc = (jvmProcAddress)(&jvm, (void**)&env, &vmInitArgs);
	if (jvmProc < 0 || jvm == NULL || env == NULL) {
		FreeLibrary(jvmDLL);
		jvmDLL = NULL;
		ofsLogger << "[VM Initializer] Error on creating java environment , failed to init Java VM" << endl;
		ofsLogger.close();
		return -3;
	}
    // 查找和加载MainClass
	jclass mainClazz = env->FindClass(mainClass);
	if (mainClazz == NULL) {
		ofsLogger << "[VM Initializer] Error on starting java backend, Class not found." << endl;
		ofsLogger.close();
		return -4;
	}
    
	// 查找Main方法
	jmethodID init = env->GetStaticMethodID(mainClazz, "main", "([Ljava/lang/String;)V");
	if (init == NULL) {
		ofsLogger << "[VM Initializer] Error on starting java backend, main method not found." << endl;
		ofsLogger.close();
		return -5;
	}

    // 我将使用UDP来实现后端到前端的消息通信，因此需要一个UDP端口，
    // 这个端口被Nodejs监听。
	char * recvStr = int_to_string(udpReceiver);
	jobjectArray params = env->NewObjectArray(1, env->FindClass("java/lang/String"),NULL);
	jstring udpParam = env->NewStringUTF(recvStr);
	cout << "[VM Initializer] UDP at port " << udpReceiver << endl;
	env->SetObjectArrayElement(params, 0, udpParam);
	cout << "[VM Initializer] Calling main method..." << endl;
        // 调用Main，启动SpringBoot
	env->CallStaticVoidMethod(mainClazz, init, params);
	cout << "[VM Initializer] Service is ready." << endl;
	delete recvStr;
	ofsLogger.close();
	return 0;
}

API_EXPORT void destroy() {

	if (jvm != NULL) {
		jvm->DestroyJavaVM();
		jvm = NULL;
		env = NULL;
	}
	if (jvmDLL != NULL) {
		FreeLibrary(jvmDLL);
		jvmDLL = NULL;
	}
}