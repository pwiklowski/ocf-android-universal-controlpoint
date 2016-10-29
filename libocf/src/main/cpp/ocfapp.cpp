#include <jni.h>
#include <arpa/inet.h>
#include <net/if.h>
#include <sys/socket.h>
#include <stdlib.h>
#include "COAPPacket.h"
#include "COAPServer.h"
#include <poll.h>
#include <android/log.h>
#include "OICClient.h"
#include "OICDeviceResource.h"
#include "ocfapp.h"

#include <android/log.h>

OICClient* oic_server=0;
int m_socketFd;
pthread_t m_thread;


List<OICDevice*> m_devices;
#define APPNAME "OcfControlPointNative"


#define log(...) __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, __VA_ARGS__);

JNIEnv* m_env;

JavaVM* m_jvm;
jobject m_obj;
jclass m_class;


jclass m_OcfDeviceClass;
jclass m_OcfDeviceVariableClass;
jclass m_OcfDeviceVariableCallbackClass;

uint64_t get_current_ms(){
    struct timeval te;
    gettimeofday(&te, NULL); // get current time
    long long milliseconds = te.tv_sec*1000LL + te.tv_usec/1000; // caculate milliseconds
    return milliseconds;
}

void get(OICDeviceResource* res, jobject callbackObject);
OICDevice* getDevice(String di);
OICDeviceResource* getDeviceResource(OICDevice* dev, String href);

extern "C" {
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *jvm, void *reserved)
{
    m_jvm = jvm;

    int status = m_jvm->GetEnv((void **)&m_env, JNI_VERSION_1_6);

    return JNI_VERSION_1_6;
}
jobject createDevice(OICDevice* dev){
    jmethodID constructor = m_env->GetMethodID(m_OcfDeviceClass, "<init>", "(Ljava/lang/String;Ljava/lang/String;)V");
    jobject obj = m_env->NewObject(m_OcfDeviceClass, constructor, m_env->NewStringUTF(dev->getName().c_str()), m_env->NewStringUTF(dev->getId().c_str()));

    jmethodID addVariableMethod = m_env->GetMethodID(m_OcfDeviceClass, "appendVariable", "(Locfcontrolpoint/wiklosoft/libocf/OcfDeviceVariable;)V");

    for(size_t i=0; i<dev->getResources()->size(); i++)
    {
        jmethodID constructor = m_env->GetMethodID(m_OcfDeviceVariableClass, "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V");
        jobject v = m_env->NewObject(m_OcfDeviceVariableClass, constructor,
                                     m_env->NewStringUTF(dev->getResources()->at(i)->getInterface().c_str()),
                                     m_env->NewStringUTF(dev->getResources()->at(i)->getHref().c_str()),
                                     m_env->NewStringUTF(dev->getResources()->at(i)->getResourceType().c_str()));

        m_env->CallVoidMethod(obj, addVariableMethod, v);
    }


    return obj;
}


void deviceFound(OICDevice* dev){
    log("deviceFound");
    m_jvm->AttachCurrentThread(&m_env, NULL);
    jobject d2 = createDevice(dev);

    jmethodID gJMethodID = m_env->GetMethodID(m_class, "deviceFound", "(Locfcontrolpoint/wiklosoft/libocf/OcfDevice;)V");
    m_env->CallVoidMethod(m_obj, gJMethodID, d2);
    m_jvm->DetachCurrentThread();
}
void Java_ocfcontrolpoint_wiklosoft_libocf_OcfControlPoint_searchDevices( JNIEnv* env, jobject thiz)
{
    findDevices();
}


void Java_ocfcontrolpoint_wiklosoft_libocf_OcfControlPoint_init( JNIEnv* env, jobject thiz)
{
    m_env = env;
    m_obj = env->NewGlobalRef(thiz);
    jclass clazz = env->GetObjectClass(thiz);
    m_class = (jclass)env->NewGlobalRef(clazz);


    jclass ocfDeviceClass = env->FindClass("ocfcontrolpoint/wiklosoft/libocf/OcfDevice");
    m_OcfDeviceClass = (jclass)env->NewGlobalRef(ocfDeviceClass);

    jclass ocfDeviceVariableClass = env->FindClass("ocfcontrolpoint/wiklosoft/libocf/OcfDeviceVariable");
    m_OcfDeviceVariableClass = (jclass)env->NewGlobalRef(ocfDeviceVariableClass);

    jclass ocfDeviceVariableCallbackClass = env->FindClass("ocfcontrolpoint/wiklosoft/libocf/OcfDeviceVariableCallback");
    m_OcfDeviceVariableCallbackClass = (jclass)env->NewGlobalRef(ocfDeviceVariableCallbackClass);

    oic_server = new OICClient([&](COAPPacket* packet){
        send_packet(packet);
    });
    oic_server->start("","");

    pthread_create(&m_thread, NULL, run, 0);

    //findDevices();
}
void Java_ocfcontrolpoint_wiklosoft_libocf_OcfDevice_get( JNIEnv* env, jobject thiz, jstring hrefTmp, jobject callbackObject)
{
    m_jvm->AttachCurrentThread(&m_env, NULL);
    log("get");
    String di;
    String href;

    const char* strChars = env->GetStringUTFChars(hrefTmp, (jboolean *)0);
    href.append(strChars);
    env->ReleaseStringUTFChars(hrefTmp, strChars);


    jfieldID diFieldId = env->GetFieldID(m_OcfDeviceClass, "mDi", "Ljava/lang/String;" );
    if (diFieldId == 0){
        log("diFieldID is 0 ");
        return;
    }
    jstring  diFieldValue = (jstring)env->GetObjectField(thiz, diFieldId);
    if (diFieldValue == 0){
        log("diFieldValue is 0 ");
        return;
    }

    const char* diChars = env->GetStringUTFChars(diFieldValue, (jboolean *)0);
    di.append(diChars);
    env->ReleaseStringUTFChars(diFieldValue, diChars);

    __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "get %s %s",di.c_str(), href.c_str());

    OICDevice* dev = getDevice(di);
    if (dev != 0){
        OICDeviceResource* res = getDeviceResource(dev, href);
        if (res != 0){

            __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "get %d", callbackObject);
            jobject globalCallback = m_env->NewGlobalRef(callbackObject);
            __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "get %d", callbackObject);

            res->get([=] (COAPPacket* response){
                log("get response");
                m_jvm->AttachCurrentThread(&m_env, NULL);
                jmethodID callbackID = m_env->GetMethodID(m_OcfDeviceVariableCallbackClass, "update", "(Ljava/lang/String;)V");
                cbor cborResponse;
                cbor::parse(&cborResponse, response->getPayload());
                String cborString = cbor::toJsonString(&cborResponse);
                m_env->CallVoidMethod(globalCallback, callbackID, m_env->NewStringUTF(cborString.c_str()));
                m_env->DeleteGlobalRef(globalCallback);

                m_jvm->DetachCurrentThread();
            });
        }
    }
}


void Java_ocfcontrolpoint_wiklosoft_libocf_OcfDevice_unobserve( JNIEnv* env, jobject thiz, jstring hrefTmp, jobject callbackObject)
{
    m_jvm->AttachCurrentThread(&m_env, NULL);
    String di;
    String href;

    const char* strChars = env->GetStringUTFChars(hrefTmp, (jboolean *)0);
    href.append(strChars);
    env->ReleaseStringUTFChars(hrefTmp, strChars);


    jfieldID diFieldId = env->GetFieldID(m_OcfDeviceClass, "mDi", "Ljava/lang/String;" );
    if (diFieldId == 0){
        log("diFieldID is 0 ");
        return;
    }
    jstring  diFieldValue = (jstring)env->GetObjectField(thiz, diFieldId);
    if (diFieldValue == 0){
        log("diFieldValue is 0 ");
        return;
    }

    const char* diChars = env->GetStringUTFChars(diFieldValue, (jboolean *)0);
    di.append(diChars);
    env->ReleaseStringUTFChars(diFieldValue, diChars);

    OICDevice* dev = getDevice(di);
    if (dev != 0){
        OICDeviceResource* res = getDeviceResource(dev, href);
        if (res != 0){
            //m_env->DeleteGlobalRef(globalCallback); //TODO clean it
            res->unobserve([=] (COAPPacket* response){
                log("unobserve response");
            });
        }
    }
}

void Java_ocfcontrolpoint_wiklosoft_libocf_OcfDevice_observe( JNIEnv* env, jobject thiz, jstring hrefTmp, jobject callbackObject)
{
    m_jvm->AttachCurrentThread(&m_env, NULL);
    String di;
    String href;

    const char* strChars = env->GetStringUTFChars(hrefTmp, (jboolean *)0);
    href.append(strChars);
    env->ReleaseStringUTFChars(hrefTmp, strChars);


    jfieldID diFieldId = env->GetFieldID(m_OcfDeviceClass, "mDi", "Ljava/lang/String;" );
    if (diFieldId == 0){
        log("diFieldID is 0 ");
        return;
    }
    jstring  diFieldValue = (jstring)env->GetObjectField(thiz, diFieldId);
    if (diFieldValue == 0){
        log("diFieldValue is 0 ");
        return;
    }

    const char* diChars = env->GetStringUTFChars(diFieldValue, (jboolean *)0);
    di.append(diChars);
    env->ReleaseStringUTFChars(diFieldValue, diChars);


    OICDevice* dev = getDevice(di);
    if (dev != 0){
        OICDeviceResource* res = getDeviceResource(dev, href);
        if (res != 0){
            jobject globalCallback = m_env->NewGlobalRef(callbackObject); //TODO: delete it later afetr unoberve is called

            res->observe([=] (COAPPacket* response){
                log("observe response");
                m_jvm->AttachCurrentThread(&m_env, NULL);
                jmethodID callbackID = m_env->GetMethodID(m_OcfDeviceVariableCallbackClass, "update", "(Ljava/lang/String;)V");
                cbor cborResponse;
                cbor::parse(&cborResponse, response->getPayload());
                String cborString = cbor::toJsonString(&cborResponse);
                m_env->CallVoidMethod(globalCallback, callbackID, m_env->NewStringUTF(cborString.c_str()));
                m_jvm->DetachCurrentThread();
            });
        }
    }
}
void Java_ocfcontrolpoint_wiklosoft_libocf_OcfDevice_post( JNIEnv* env, jobject thiz, jstring hrefTmp, jstring jsonString, jobject callbackObject)
{
    m_jvm->AttachCurrentThread(&m_env, NULL);
    log("post");
    String di;
    String href;
    String value;

    const char* strChars = env->GetStringUTFChars(hrefTmp, (jboolean *)0);
    href.append(strChars);
    env->ReleaseStringUTFChars(hrefTmp, strChars);


    const char*  jsonChars = env->GetStringUTFChars(jsonString, (jboolean *)0);
    value.append(jsonChars);
    env->ReleaseStringUTFChars(hrefTmp, jsonChars);


    jfieldID diFieldId = env->GetFieldID(m_OcfDeviceClass, "mDi", "Ljava/lang/String;" );
    if (diFieldId == 0){
        log("diFieldID is 0 ");
        return;
    }
    jstring  diFieldValue = (jstring)env->GetObjectField(thiz, diFieldId);
    if (diFieldValue == 0){
        log("diFieldValue is 0 ");
        return;
    }

    const char* diChars = env->GetStringUTFChars(diFieldValue, (jboolean *)0);
    di.append(diChars);
    env->ReleaseStringUTFChars(diFieldValue, diChars);

    __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "post %s %s %s",di.c_str(), href.c_str(), value.c_str());

    cbor v;
    v.parse_value(value.c_str());

    OICDevice* dev = getDevice(di);
    if (dev != 0){
        OICDeviceResource* res = getDeviceResource(dev, href);
        if (res != 0){
            jobject globalCallback = m_env->NewGlobalRef(callbackObject);

            res->post(v, [=] (COAPPacket* response){
                log("post response");
                m_jvm->AttachCurrentThread(&m_env, NULL);
                jmethodID callbackID = m_env->GetMethodID(m_OcfDeviceVariableCallbackClass, "update", "(Ljava/lang/String;)V");
                m_env->CallVoidMethod(globalCallback, callbackID, 0);
                m_env->DeleteGlobalRef(globalCallback);
                m_jvm->DetachCurrentThread();
            });
        }
    }
}
}

OICDevice* getDevice(String di){

    for (uint8_t i=0; i<m_devices.size(); i++){
        OICDevice* d = m_devices.at(i);
        if (d->getId() == di){
            return d;
        }
    }
    return 0;
}

OICDeviceResource* getDeviceResource(OICDevice* dev, String href){

    for (uint8_t i=0; i<dev->getResources()->size(); i++){
        OICDeviceResource* v = dev->getResources()->at(i);
        if (v->getHref() == href){
            return v;
        }
    }
    return 0;
}

bool isDeviceOnList(String id){
    return getDevice(id) != 0;
}

void findDevices()
{
    __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "findDevices");
    oic_server->searchDevices([&](COAPPacket* packet){
        cbor message;
        cbor::parse(&message, packet->getPayload());

        __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "found");

        for (uint16_t i=0; i<message.toArray()->size(); i++){
            cbor device = message.toArray()->at(i);

            String name = device.getMapValue("n").toString();
            String di= device.getMapValue("di").toString();

            __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "found device %s %s", name.c_str(), di.c_str());

            if (isDeviceOnList(di)) continue;

            cbor links = device.getMapValue("links");
            OICDevice* dev = new OICDevice(di, name, packet->getAddress(), oic_server);

            for (uint16_t j=0; j< links.toArray()->size(); j++){
                cbor link = links.toArray()->at(j);


                String href = link.getMapValue("href").toString();
                String rt = link.getMapValue("rt").toString();
                String iff = link.getMapValue("if").toString();

                dev->getResources()->push_back(new OICDeviceResource(href, iff, rt, dev, oic_server));
            }
            m_devices.append(dev);
            deviceFound(dev);

        }
    });
}

String convertAddress(sockaddr_in a){
    char addr[30];
    sprintf(addr, "%d.%d.%d.%d %d",
            (uint8_t) (a.sin_addr.s_addr),
            (uint8_t) (a.sin_addr.s_addr >> 8),
            (uint8_t) (a.sin_addr.s_addr >> 16 ),
            (uint8_t) (a.sin_addr.s_addr >> 24),
            htons(a.sin_port));

    return addr;
}
void* run(void* param){
    m_jvm->AttachCurrentThread(&m_env, NULL);

    const int on = 1;
    m_socketFd = socket(AF_INET,SOCK_DGRAM,IPPROTO_UDP);


    struct sockaddr_in serv,client;
    struct ip_mreq mreq;

    serv.sin_family = AF_INET;
    serv.sin_port = 0;
    serv.sin_addr.s_addr = htonl(INADDR_ANY);

    uint8_t buffer[1024];
    socklen_t l = sizeof(client);
    if(setsockopt(m_socketFd, SOL_SOCKET, SO_REUSEADDR, &on, sizeof(on)) < 0)
    {
        return 0;
    }
    struct timeval tv;
    tv.tv_sec = 0;
    tv.tv_usec = 1000000;

    if( bind(m_socketFd, (struct sockaddr*)&serv, sizeof(serv) ) == -1)
    {
        __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "Unable to bind");
        return 0;
    }

    struct pollfd pfd;
    int res;

    pfd.fd = m_socketFd;
    pfd.events = POLLIN;
    String address;

    uint64_t lastTick = get_current_ms();
    size_t rc;
    __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "Start loop");
    while(1){
        rc = readPacket(buffer, sizeof(buffer), &address);
        if (rc >0){
            COAPPacket* p = COAPPacket::parse(buffer, rc, address.c_str());
            if (p != 0){
                oic_server->handleMessage(p);
                delete p;
            }
        }
        oic_server->sendQueuedPackets();
        if ((get_current_ms() - lastTick) > 50){
            lastTick = get_current_ms();
            oic_server->checkPackets();
        }
    }
    m_jvm->DetachCurrentThread();
}
int readPacket(uint8_t* buf, uint16_t maxSize, String* address){
    struct pollfd pfd;
    int res = sizeof(*buf);

    pfd.fd = m_socketFd;
    pfd.events = POLLIN;

    struct sockaddr_in client;
    socklen_t l = sizeof(client);
    size_t rc = poll(&pfd, 1, 20); // 1000 ms timeout
    if (rc >0){
        rc = recvfrom(m_socketFd, buf, maxSize, 0, (struct sockaddr *)&client,&l);

        *address = convertAddress(client);
    }
    return rc;
}



void send_packet_addr(sockaddr_in destination, COAPPacket* packet){

    uint8_t buffer[1024];
    size_t response_len;
    socklen_t l = sizeof(destination);
    packet->build(buffer, &response_len);

    sendto(m_socketFd, buffer, response_len, 0, (struct sockaddr*)&destination, l);
}

void send_packet(COAPPacket* packet){
    String destination = packet->getAddress();
    size_t pos = destination.find(" ");
    String ip = destination.substr(0, pos);
    uint16_t port = atoi(destination.substr(pos).c_str());

    struct sockaddr_in client;

    client.sin_family = AF_INET;
    client.sin_port = htons(port);
    client.sin_addr.s_addr = inet_addr(ip.c_str());

    send_packet_addr(client, packet);
}