cmake_minimum_required(VERSION 3.4.1)

set(CMAKE_VERBOSE_MAKEFILE on)


#TODO: get rid those long paths

#libcoap
aux_source_directory(../../../../../libcoap/ SRC_LIST)
INCLUDE_DIRECTORIES(../../../../../std/)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11 -Werror=sign-compare")
add_library(coap SHARED ${SRC_LIST})


#liboic
aux_source_directory(../../../../../liboic/ SRC_LIST)
INCLUDE_DIRECTORIES(../../../../../libcoap/)
INCLUDE_DIRECTORIES(../../../../../std/)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11 -Werror=sign-compare")
add_library(oic SHARED ${SRC_LIST})



INCLUDE_DIRECTORIES(../../../../../liboic/)
INCLUDE_DIRECTORIES(../../../../../libcoap/)
INCLUDE_DIRECTORIES(../../../../../std/)

add_library(ocfapp SHARED ocfapp.cpp )

find_library(log-lib log)

target_link_libraries(ocfapp coap)
target_link_libraries(ocfapp oic)
target_link_libraries(ocfapp ${log-lib})
