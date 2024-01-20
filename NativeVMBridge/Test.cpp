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
	// 加载JVM的动态库
	jvmDLL = LoadLibrary(_T("D:/SpecialProjects/ElectronWithJava/Electron/backend/bin/server/jvm.dll"));
	if (jvmDLL == NULL) {
		cout << "[VM Initializer] Error on loading java vm native library" << endl;
		return 0;
	}

	//初始化jvm物理地址
	JNICREATEPROC jvmProcAddress = (JNICREATEPROC)GetProcAddress(jvmDLL, "JNI_CreateJavaVM");
	if (jvmProcAddress == NULL) {
		FreeLibrary(jvmDLL);
		cout << "[VM Initializer] Error on loading, can not find create vm method." << endl;
		return 1;
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

	//设置classpath
	string opt = (string("-Djava.class.path=") + string(classPath));
	JavaVMOption classPathOpt;
	classPathOpt.optionString = (char*)opt.c_str();
	vmOption[0] = classPathOpt;

	cout << "Classpath is " << opt << endl;

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
		cout << "[VM Initializer] Error on creating java environment , failed to init Java VM" << endl;
		return jvmProc;
	}
	// 查找和加载MainClass
	jclass mainClazz = env->FindClass(mainClass);
	if (mainClazz == NULL) {
		cout << "[VM Initializer] Error on starting java backend, Class not found." << endl;
		return 3;
	}

	// 查找Main方法
	jmethodID init = env->GetStaticMethodID(mainClazz, "main", "([Ljava/lang/String;)V");
	if (init == NULL) {
		cout << "[VM Initializer] Error on starting java backend, main method not found." << endl;
		return 4;
	}

	// 我将使用UDP来实现后端到前端的消息通信，因此需要一个UDP端口，
	// 这个端口被Nodejs监听。
	char* recvStr = int_to_string(udpReceiver);
	jobjectArray params = env->NewObjectArray(1, env->FindClass("java/lang/String"), NULL);
	jstring udpParam = env->NewStringUTF(recvStr);
	cout << "[VM Initializer] UDP at port " << udpReceiver << endl;
	env->SetObjectArrayElement(params, 0, udpParam);
	cout << "[VM Initializer] Calling main method..." << endl;
	// 调用Main，启动SpringBoot
	env->CallStaticVoidMethod(mainClazz, init, params);
	cout << "[VM Initializer] Service is ready." << endl;
	delete recvStr;
	return true;
}


void main() {

	char vmOptions[] = "--Xmx1024m\n-javaagent:D:\\SpecialProjects\\ElectronWithJava\\Electron\\agentDemo-1.0-SNAPSHOT.jar";
	initialize(0, "D:\\SpecialProjects\\ElectronWithJava\\Electron\\backend\\bin","D:/SpecialProjects/ElectronWithJava/NativeVMBridge/Build/Release/table-0.0.1-SNAPSHOT.jar", vmOptions, "");

}
