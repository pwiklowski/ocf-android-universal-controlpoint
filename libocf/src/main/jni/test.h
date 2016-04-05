#ifndef TEST_H
#define TEST_H


void findDevices();
void* run(void* param);
void send_packet_addr(sockaddr_in destination, COAPPacket* packet);
void send_packet(COAPPacket* packet);

#endif