#include "mbed.h"
#include "wifi.h"
#include <inttypes.h>
#include <string>
#include "Servo.h"
#include "GPS.h"

/*------------------------------------------------------------------------------
Hyperterminal settings: 115200 bauds, 8-bit data, no parity

This example 
  - connects to a wifi network (SSID & PWD to set in mbed_app.json)
  - Connects to a TCP server (set the address in RemoteIP)
  - Sends "Hello" to the server when data is received

This example uses SPI3 ( PE_0 PC_10 PC_12 PC_11), wifi_wakeup pin (PB_13), 
wifi_dataready pin (PE_1), wifi reset pin (PE_8)
------------------------------------------------------------------------------*/

/* Private defines -----------------------------------------------------------*/
#define WIFI_WRITE_TIMEOUT 3000
#define WIFI_READ_TIMEOUT  3000
#define CONNECTION_TRIAL_MAX  10

/* Private typedef------------------------------------------------------------*/
/* Private macro -------------------------------------------------------------*/
/* Private variables ---------------------------------------------------------*/
Serial pc(SERIAL_TX, SERIAL_RX);
uint8_t RemoteIP[] = {MBED_CONF_APP_SERVER_IP_1,MBED_CONF_APP_SERVER_IP_2,MBED_CONF_APP_SERVER_IP_3, MBED_CONF_APP_SERVER_IP_4};
uint8_t RxData [] = "xxxxxxx";
char* modulename;
uint8_t TxData2[] = "gps#xxxxxxxxxxxxxxxx";
uint8_t TxData[] = "xxxxxxxxxxxxxxxxxxxxxxx";
uint16_t RxLen;
uint8_t  MAC_Addr[6]; 
uint8_t  IP_Addr[4]; 
int servo_x=0;
int servo_y=0;
int servo_xmem=0;
int servo_ymem=0;
bool isFirst=true;
//Servo myservo(PB_8);
//Servo myservo2(PB_9);
GPS gpsAda(PC_1,PC_0,9600);

PinName pin_SERVOX = PB_8;
PwmOut ServoX(pin_SERVOX);

PinName pin_SERVOY = PB_9;
PwmOut ServoY(pin_SERVOY);
void Servo_angleX(int16_t angle);
void Servo_angleY(int16_t angle);
int main()
{ //Servo_angleY(0);
/*while(1){
    Servo_angleY(0);
    Servo_angleX(0);
   /* for(int k=0;k<=180;k++) {
                                    Servo_angleY(k);
                                    wait(0.1);
                                }
                                 for(int k=0;k<=180;k++) {
                                    Servo_angleX(k);
                                    wait(0.1);
                                }
                                
                                }*/
ServoX.period_ms(20);
int i;
int j;   
//uint8_t TxData[] = "gps#";
//myservo.calibrate(0.0010, 90.0);
//myservo2.calibrate(0.0010, 90.0);   
string str;
int32_t Socket = -1;
uint16_t Datalen;
uint16_t Trials = CONNECTION_TRIAL_MAX;
pc.baud(9600);
    printf("\n");
    printf("************************************************************\n");
    printf("***   STM32 IoT Discovery kit for STM32L475 MCU          ***\n");
    printf("***      WIFI Module in TCP Client mode demonstration    ***\n\n");
    printf("*** TCP Client Instructions :\n");
    printf("*** 1- Make sure your Phone is connected to the same network that\n");
    printf("***    you configured using the Configuration Access Point.\n");
    printf("*** 2- Create a server by using the android application TCP Server\n");
    printf("***    with port(8002).\n");
    printf("*** 3- Get the Network Name or IP Address of your phone from the step 2.\n\n"); 
    printf("************************************************************\n");

    /*Initialize  WIFI module */
    if(WIFI_Init() ==  WIFI_STATUS_OK) {
        printf("> WIFI Module Initialized.\n");  
        if(WIFI_GetMAC_Address(MAC_Addr) == WIFI_STATUS_OK) {
            printf("> es-wifi module MAC Address : %X:%X:%X:%X:%X:%X\n",     
                   MAC_Addr[0],
                   MAC_Addr[1],
                   MAC_Addr[2],
                   MAC_Addr[3],
                   MAC_Addr[4],
                   MAC_Addr[5]);   
        } else {
            printf("> ERROR : CANNOT get MAC address\n");
        }
    
        if( WIFI_Connect(MBED_CONF_APP_WIFI_SSID, MBED_CONF_APP_WIFI_PASSWORD, WIFI_ECN_WPA2_PSK) == WIFI_STATUS_OK) {
            printf("> es-wifi module connected \n");
            if(WIFI_GetIP_Address(IP_Addr) == WIFI_STATUS_OK) {
                printf("> es-wifi module got IP Address : %d.%d.%d.%d\n",     
                       IP_Addr[0],
                       IP_Addr[1],
                       IP_Addr[2],
                       IP_Addr[3]); 
        
                printf("> Trying to connect to Server: %d.%d.%d.%d:8002 ...\n",     
                       RemoteIP[0],
                       RemoteIP[1],
                       RemoteIP[2],
                       RemoteIP[3]);
        
                while (Trials--){ 
                    if( WIFI_OpenClientConnection(0, WIFI_TCP_PROTOCOL, "TCP_CLIENT", RemoteIP, 8002, 0) == WIFI_STATUS_OK){
                        printf("> TCP Connection opened successfully.\n"); 
                        Socket = 0;
                    }
                }
                if(!Trials) {
                    printf("> ERROR : Cannot open Connection\n");
                }
            } else {
                printf("> ERROR : es-wifi module CANNOT get IP address\n");
            }
        } else {
            printf("> ERROR : es-wifi module NOT connected\n");
        }
    } else {
        printf("> ERROR : WIFI Module cannot be initialized.\n"); 
    }
  
    while(1){        
        /*if(gpsAda.sample()){
          pc.printf("%f\t%c\t%f\t%c\t%f\t%f\t%f\n\r",gpsAda.longitude, gpsAda.ns,gpsAda.latitude,gpsAda.ew, gpsAda.alt, gpsAda.geoid, gpsAda.time);
          pc.printf("%d:%d:%d",gpsAda.hour,gpsAda.minute,gpsAda.seconed);
        }*/
        
        if(Socket != -1) {
                if(WIFI_ReceiveData(Socket, RxData, sizeof(RxData), &Datalen, 5000) == WIFI_STATUS_OK){
                        printf("Data Recieved  %X \n",RxData);
                        printf("Data Recieved  %d \n",Datalen);
                        str = (char*)RxData;
                    if(Datalen > 0) {      
                       printf("STR : %s \n",str);
                        // printf("x: %d \n",atoi(str.substr(0,4).c_str()));
                        // printf("y: %d \n",atoi(str.substr(5,9).c_str()));
                          
                            servo_x = atoi(str.substr(0,3).c_str());
                            printf("Data Reciever x: %d \n",servo_x);
                                
                            servo_y = atoi(str.substr(4,7).c_str());
                            if(servo_y<0)
                                servo_y = servo_y * -1;
                                
                            printf("Data Reciever y: %d \n",servo_y);
                              
                             if(isFirst){ 
                            if(servo_x<=180) {
                                for(int k=0;k<=180-servo_x;k++) {
                                    Servo_angleX(k);
                                    wait(0.07);
                                }
                                 for(int k=0;k<=180-servo_y;k++) {
                                    Servo_angleY(k);
                                    wait(0.07);
                                }
                                servo_ymem=180-servo_y;
                                servo_xmem=180-servo_x;
                                //Servo_angle(servo_y);
                            }
                            else {
                                //Servo_angle(servo_x - 180);
                                //Servo_angle(180 - servo_y);
                                for(int k=0;k<=360-servo_x;k++) {
                                    Servo_angleX(k);
                                    wait(0.07);
                                }
                                for(int k=0;k<=servo_y;k++) {
                                    Servo_angleY(k);
                                    wait(0.07);
                                }  
                                servo_ymem=servo_y;
                                servo_xmem=360-servo_x;
                            }  
                            isFirst=false;   
                            } else{
                             
                            if(servo_x>180) {      
                              servo_x=360-servo_x; 
                               
                            }
                            else{
                                servo_x=180-servo_x;
                                servo_y=180-servo_y;
                                }
                            if(servo_x>=servo_xmem){
                                 for(int k=servo_xmem;k<=servo_x;k++) {
                                    Servo_angleX(k);
                                    wait(0.07);
                                    }
                                }else{
                                    for(int k=servo_xmem;k>=servo_x;k--){
                                         Servo_angleX(k);
                                    wait(0.07);
                                        }
                                    
                                    
                                    }
                                    servo_xmem=servo_x;
                                    
                                if(servo_y>=servo_ymem){
                                 for(int k=servo_ymem;k<=servo_y;k++) {
                                    Servo_angleY(k);
                                    wait(0.07);
                                    }
                                }else{
                                    for(int k=servo_ymem;k>=servo_y;k--){
                                         Servo_angleY(k);
                                    wait(0.07);
                                        }
                                    }
                                    servo_ymem=servo_y;
                                }      
                   }
                } 
                else {
                        printf("> ERROR : Failed to Receive Data.\n");
                }
                //wait(1.0);
                /*Send GPS DATA*/
                if(gpsAda.sample()){
                  //pc.printf("%f\t%c\t%f\t%c\t%f\t%f\t%f\n\r",gpsAda.longitude, gpsAda.ns,gpsAda.latitude,gpsAda.ew, gpsAda.alt, gpsAda.geoid, gpsAda.time);
                  //pc.printf("%d:%d:%d",gpsAda.hour,gpsAda.minute,gpsAda.seconed);
                    char output[50];                    
                    snprintf(output, 50, "%f", gpsAda.longitude/100);
                    //printf("output : %s",output);
                    i=0;
                    j=0;
                    while(i < 7) {
                        TxData[j] = output[i];
                        j++;
                        i++;
                    }
                    TxData[j] = '#';
                    j++;
                    i=0;
                    snprintf(output, 50, "%f", gpsAda.latitude/100);
                    while(i < 7) {
                        TxData[j] = output[i];    
                        j++;
                        i++;
                    }
                    
                    TxData[j] = '#';
                    j++;
                    i=0;
                    snprintf(output, 50, "%f", gpsAda.alt);
                    while(i <7) {
                        if( output[i]=='.')
                            break;
                        TxData[j] = output[i];
                        j++;
                        i++;
                    }
                    if(WIFI_SendData(Socket, TxData, sizeof(TxData), &Datalen, WIFI_WRITE_TIMEOUT) != WIFI_STATUS_OK) {
                        printf("> ERROR : Failed to send Data.\n");   
                    }
                   wait(0.5);
                 }
                 else {
                 printf("cannot get gps"); 
                 }
                  
                 
            
                               /*
                   else {
                       printf("> ERROR : Failed to Receive Data.\n");  
                    }*/ 

        }
    }
}


void Servo_angleX(int16_t angle)
{
    int16_t Angle = 600 + (angle * 10);
    ServoX.pulsewidth_us(Angle);
}

void Servo_angleY(int16_t angle)
{
    int16_t Angle = 600 + (angle * 10);
    ServoY.pulsewidth_us(Angle);
}