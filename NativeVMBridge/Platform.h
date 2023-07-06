#ifndef NFFJNI
#define NFFJNI

#ifdef _WIN32
    
    #include <Windows.h>
    #include <tchar.h>
    #include "external/windows/jni.h"
    #define API_EXPORT extern "C" __declspec(dllexport)

    #define Handler HINSTANCE

#elif __APPLE__
    #include <stdio.h>
    #include <dlfcn.h>
    #include "external/osx/jni.h"
    #define API_EXPORT extern "C"
    #define _T(param) param

    #define Handler void*

    #define sprintf_s(buf,size, format, ...) snprintf((buf), size,format, __VA_ARGS__)
    
    Handler LoadLibrary(const char* path) {
        return dlopen(path,RTLD_LAZY);
    }

    void* GetProcAddress(Handler lib,const char* funcName) {
        return dlsym(lib, funcName);
    }

    void FreeLibrary(Handler lib) {
        dlclose(lib);
    }


#endif 

#endif