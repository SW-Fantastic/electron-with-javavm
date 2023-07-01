cmake_minimum_required(VERSION 3.12)
project(BackendAPI)

set (CMAKE_CONFIGURATION_TYPES "Release")
   
add_library(BackendAPI SHARED Invoker.cpp)