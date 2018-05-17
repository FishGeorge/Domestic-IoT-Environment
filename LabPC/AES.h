/*
 * FileName : AES.h
 * 
 */
#ifndef __AES_CBC_H__
#define __AES_CBC_H__


void SetKey(unsigned char *key);
unsigned char* Cipher(unsigned char* input, unsigned char* output);
unsigned char* InvCipher(unsigned char* input, unsigned char* output);
void KeyExpansion(unsigned char* key, unsigned char w[][4][4]);
unsigned char FFmul(unsigned char a, unsigned char b);

void SubBytes(unsigned char state[][4]);
void ShiftRows(unsigned char state[][4]);
void MixColumns(unsigned char state[][4]);
void AddRoundKey(unsigned char state[][4], unsigned char k[][4]);

void InvSubBytes(unsigned char state[][4]);
void InvShiftRows(unsigned char state[][4]);
void InvMixColumns(unsigned char state[][4]);
int  Encrypt(unsigned char *input, int length, unsigned char *output);
int  Decrypt(unsigned char *input, int length, unsigned char *output);	



#endif 
