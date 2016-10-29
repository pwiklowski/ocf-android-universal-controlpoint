#ifndef OCFAPP_H
#define OCFAPP_H


void findDevices();
void* run(void* param);
void send_packet_addr(sockaddr_in destination, COAPPacket* packet);
void send_packet(COAPPacket* packet);
int readPacket(uint8_t* buf, uint16_t maxSize, String* address);

#endif