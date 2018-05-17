
#include <stdlib.h>
#include "AES.h"


#define GROUP_SERVER_ADDR "224.0.0.50"
#define GROUP_DISCOVER_PORT 4321
#define GROUP_RECV_PORT 9898
#define UDP_SEND_PORT 9898
#define AES_BLOCK_SIZE 16

static unsigned char w[11][4][4];

//You must modify this "m_key". This key is the same as gateway key which is setted in MI-Home App by user.
static unsigned char m_key[16] = {'1','2','3','4','5','6','7','8','9','0','1','2','3','4','5','6'};
//You must not modify this "m_iv" 
static unsigned char m_iv[16] = {0x17, 0x99, 0x6d, 0x09, 0x3d, 0x28, 0xdd, 0xb3, 0xba, 0x69, 0x5a, 0x2e, 0x6f, 0x58, 0x56, 0x2e};
static unsigned char heart_token[17] = {0};
static unsigned char key_of_write[33] = {0};


static unsigned char Sbox[] = { /*  0    1    2    3    4    5    6    7    8    9    a    b    c    d    e    f */
	0x63, 0x7c, 0x77, 0x7b, 0xf2, 0x6b, 0x6f, 0xc5, 0x30, 0x01, 0x67, 0x2b,
			0xfe, 0xd7, 0xab, 0x76, /*0*/
			0xca, 0x82, 0xc9, 0x7d, 0xfa, 0x59, 0x47, 0xf0, 0xad, 0xd4, 0xa2,
			0xaf, 0x9c, 0xa4, 0x72, 0xc0, /*1*/
			0xb7, 0xfd, 0x93, 0x26, 0x36, 0x3f, 0xf7, 0xcc, 0x34, 0xa5, 0xe5,
			0xf1, 0x71, 0xd8, 0x31, 0x15, /*2*/
			0x04, 0xc7, 0x23, 0xc3, 0x18, 0x96, 0x05, 0x9a, 0x07, 0x12, 0x80,
			0xe2, 0xeb, 0x27, 0xb2, 0x75, /*3*/
			0x09, 0x83, 0x2c, 0x1a, 0x1b, 0x6e, 0x5a, 0xa0, 0x52, 0x3b, 0xd6,
			0xb3, 0x29, 0xe3, 0x2f, 0x84, /*4*/
			0x53, 0xd1, 0x00, 0xed, 0x20, 0xfc, 0xb1, 0x5b, 0x6a, 0xcb, 0xbe,
			0x39, 0x4a, 0x4c, 0x58, 0xcf, /*5*/
			0xd0, 0xef, 0xaa, 0xfb, 0x43, 0x4d, 0x33, 0x85, 0x45, 0xf9, 0x02,
			0x7f, 0x50, 0x3c, 0x9f, 0xa8, /*6*/
			0x51, 0xa3, 0x40, 0x8f, 0x92, 0x9d, 0x38, 0xf5, 0xbc, 0xb6, 0xda,
			0x21, 0x10, 0xff, 0xf3, 0xd2, /*7*/
			0xcd, 0x0c, 0x13, 0xec, 0x5f, 0x97, 0x44, 0x17, 0xc4, 0xa7, 0x7e,
			0x3d, 0x64, 0x5d, 0x19, 0x73, /*8*/
			0x60, 0x81, 0x4f, 0xdc, 0x22, 0x2a, 0x90, 0x88, 0x46, 0xee, 0xb8,
			0x14, 0xde, 0x5e, 0x0b, 0xdb, /*9*/
			0xe0, 0x32, 0x3a, 0x0a, 0x49, 0x06, 0x24, 0x5c, 0xc2, 0xd3, 0xac,
			0x62, 0x91, 0x95, 0xe4, 0x79, /*a*/
			0xe7, 0xc8, 0x37, 0x6d, 0x8d, 0xd5, 0x4e, 0xa9, 0x6c, 0x56, 0xf4,
			0xea, 0x65, 0x7a, 0xae, 0x08, /*b*/
			0xba, 0x78, 0x25, 0x2e, 0x1c, 0xa6, 0xb4, 0xc6, 0xe8, 0xdd, 0x74,
			0x1f, 0x4b, 0xbd, 0x8b, 0x8a, /*c*/
			0x70, 0x3e, 0xb5, 0x66, 0x48, 0x03, 0xf6, 0x0e, 0x61, 0x35, 0x57,
			0xb9, 0x86, 0xc1, 0x1d, 0x9e, /*d*/
			0xe1, 0xf8, 0x98, 0x11, 0x69, 0xd9, 0x8e, 0x94, 0x9b, 0x1e, 0x87,
			0xe9, 0xce, 0x55, 0x28, 0xdf, /*e*/
			0x8c, 0xa1, 0x89, 0x0d, 0xbf, 0xe6, 0x42, 0x68, 0x41, 0x99, 0x2d,
			0x0f, 0xb0, 0x54, 0xbb, 0x16 /*f*/
	};

static unsigned char InvSbox[256] = { /*  0    1    2    3    4    5    6    7    8    9    a    b    c    d    e    f  */
	0x52, 0x09, 0x6a, 0xd5, 0x30, 0x36, 0xa5, 0x38, 0xbf, 0x40, 0xa3, 0x9e,
			0x81, 0xf3, 0xd7, 0xfb, /*0*/
			0x7c, 0xe3, 0x39, 0x82, 0x9b, 0x2f, 0xff, 0x87, 0x34, 0x8e, 0x43,
			0x44, 0xc4, 0xde, 0xe9, 0xcb, /*1*/
			0x54, 0x7b, 0x94, 0x32, 0xa6, 0xc2, 0x23, 0x3d, 0xee, 0x4c, 0x95,
			0x0b, 0x42, 0xfa, 0xc3, 0x4e, /*2*/
			0x08, 0x2e, 0xa1, 0x66, 0x28, 0xd9, 0x24, 0xb2, 0x76, 0x5b, 0xa2,
			0x49, 0x6d, 0x8b, 0xd1, 0x25, /*3*/
			0x72, 0xf8, 0xf6, 0x64, 0x86, 0x68, 0x98, 0x16, 0xd4, 0xa4, 0x5c,
			0xcc, 0x5d, 0x65, 0xb6, 0x92, /*4*/
			0x6c, 0x70, 0x48, 0x50, 0xfd, 0xed, 0xb9, 0xda, 0x5e, 0x15, 0x46,
			0x57, 0xa7, 0x8d, 0x9d, 0x84, /*5*/
			0x90, 0xd8, 0xab, 0x00, 0x8c, 0xbc, 0xd3, 0x0a, 0xf7, 0xe4, 0x58,
			0x05, 0xb8, 0xb3, 0x45, 0x06, /*6*/
			0xd0, 0x2c, 0x1e, 0x8f, 0xca, 0x3f, 0x0f, 0x02, 0xc1, 0xaf, 0xbd,
			0x03, 0x01, 0x13, 0x8a, 0x6b, /*7*/
			0x3a, 0x91, 0x11, 0x41, 0x4f, 0x67, 0xdc, 0xea, 0x97, 0xf2, 0xcf,
			0xce, 0xf0, 0xb4, 0xe6, 0x73, /*8*/
			0x96, 0xac, 0x74, 0x22, 0xe7, 0xad, 0x35, 0x85, 0xe2, 0xf9, 0x37,
			0xe8, 0x1c, 0x75, 0xdf, 0x6e, /*9*/
			0x47, 0xf1, 0x1a, 0x71, 0x1d, 0x29, 0xc5, 0x89, 0x6f, 0xb7, 0x62,
			0x0e, 0xaa, 0x18, 0xbe, 0x1b, /*a*/
			0xfc, 0x56, 0x3e, 0x4b, 0xc6, 0xd2, 0x79, 0x20, 0x9a, 0xdb, 0xc0,
			0xfe, 0x78, 0xcd, 0x5a, 0xf4, /*b*/
			0x1f, 0xdd, 0xa8, 0x33, 0x88, 0x07, 0xc7, 0x31, 0xb1, 0x12, 0x10,
			0x59, 0x27, 0x80, 0xec, 0x5f, /*c*/
			0x60, 0x51, 0x7f, 0xa9, 0x19, 0xb5, 0x4a, 0x0d, 0x2d, 0xe5, 0x7a,
			0x9f, 0x93, 0xc9, 0x9c, 0xef, /*d*/
			0xa0, 0xe0, 0x3b, 0x4d, 0xae, 0x2a, 0xf5, 0xb0, 0xc8, 0xeb, 0xbb,
			0x3c, 0x83, 0x53, 0x99, 0x61, /*e*/
			0x17, 0x2b, 0x04, 0x7e, 0xba, 0x77, 0xd6, 0x26, 0xe1, 0x69, 0x14,
			0x63, 0x55, 0x21, 0x0c, 0x7d /*f*/
	};

unsigned char* Cipher(unsigned char* input, unsigned char *output) {
	unsigned char state[4][4];
	int i, r, c;

	for (r = 0; r < 4; r++) {
		for (c = 0; c < 4; c++) {
			state[r][c] = input[c * 4 + r];
		}
	}

	AddRoundKey(state, w[0]);

	for (i = 1; i <= 10; i++) {
		SubBytes(state);
		ShiftRows(state);
		if (i != 10)
			MixColumns(state);
		AddRoundKey(state, w[i]);
	}

	for (r = 0; r < 4; r++) {
		for (c = 0; c < 4; c++) {
			output[c * 4 + r] = state[r][c];
		}
	}

	return output;
}

unsigned char* InvCipher(unsigned char* input, unsigned char *output) {
	unsigned char state[4][4];
	int i, r, c;

	for (r = 0; r < 4; r++) {
		for (c = 0; c < 4; c++) {
			state[r][c] = input[c * 4 + r];
		}
	}

	AddRoundKey(state, w[10]);
	for (i = 9; i >= 0; i--) {
		InvShiftRows(state);
		InvSubBytes(state);
		AddRoundKey(state, w[i]);
		if (i) {
			InvMixColumns(state);
		}
	}

	for (r = 0; r < 4; r++) {
		for (c = 0; c < 4; c++) {
			output[c * 4 + r] = state[r][c];
		}
	}
	return output;
}

void KeyExpansion(unsigned char* key, unsigned char w[][4][4]) {
	int i, j, r, c;
	unsigned char rc[] = { 0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80, 0x1b,
			0x36 };
	for (r = 0; r < 4; r++) {
		for (c = 0; c < 4; c++) {
			w[0][r][c] = key[r + c * 4];
		}
	}
	for (i = 1; i <= 10; i++) {
		for (j = 0; j < 4; j++) {
			unsigned char t[4];
			for (r = 0; r < 4; r++) {
				t[r] = j ? w[i][r][j - 1] : w[i - 1][r][3];
			}
			if (j == 0) {
				unsigned char temp = t[0];
				for (r = 0; r < 3; r++) {
					t[r] = Sbox[t[(r + 1) % 4]];
				}
				t[3] = Sbox[temp];
				t[0] ^= rc[i - 1];
			}
			for (r = 0; r < 4; r++) {
				w[i][r][j] = w[i - 1][r][j] ^ t[r];
			}
		}
	}
}

unsigned char FFmul(unsigned char a, unsigned char b) {
	unsigned char bw[4];
	unsigned char res = 0;
	int i;
	bw[0] = b;
	for (i = 1; i < 4; i++) {
		bw[i] = bw[i - 1] << 1;
		if (bw[i - 1] & 0x80) {
			bw[i] ^= 0x1b;
		}
	}
	for (i = 0; i < 4; i++) {
		if ((a >> i) & 0x01) {
			res ^= bw[i];
		}
	}
	return res;
}

void SubBytes(unsigned char state[][4]) {
	int r, c;
	for (r = 0; r < 4; r++) {
		for (c = 0; c < 4; c++) {
			state[r][c] = Sbox[state[r][c]];
		}
	}
}

void ShiftRows(unsigned char state[][4]) {
	unsigned char t[4];
	int r, c;
	for (r = 1; r < 4; r++) {
		for (c = 0; c < 4; c++) {
			t[c] = state[r][(c + r) % 4];
		}
		for (c = 0; c < 4; c++) {
			state[r][c] = t[c];
		}
	}
}

void MixColumns(unsigned char state[][4]) {
	unsigned char t[4];
	int r, c;
	for (c = 0; c < 4; c++) {
		for (r = 0; r < 4; r++) {
			t[r] = state[r][c];
		}
		for (r = 0; r < 4; r++) {
			state[r][c] = FFmul(0x02, t[r]) ^ FFmul(0x03, t[(r + 1) % 4])
					^ FFmul(0x01, t[(r + 2) % 4]) ^ FFmul(0x01, t[(r + 3) % 4]);
		}
	}
}

void AddRoundKey(unsigned char state[][4], unsigned char k[][4]) {
	int r, c;
	for (c = 0; c < 4; c++) {
		for (r = 0; r < 4; r++) {
			state[r][c] ^= k[r][c];
		}
	}
}

void InvSubBytes(unsigned char state[][4]) {
	int r, c;
	for (r = 0; r < 4; r++) {
		for (c = 0; c < 4; c++) {
			state[r][c] = InvSbox[state[r][c]];
		}
	}
}

void InvShiftRows(unsigned char state[][4]) {
	unsigned char t[4];
	int r, c;
	for (r = 1; r < 4; r++) {
		for (c = 0; c < 4; c++) {
			t[c] = state[r][(c - r + 4) % 4];
		}
		for (c = 0; c < 4; c++) {
			state[r][c] = t[c];
		}
	}
}

void InvMixColumns(unsigned char state[][4]) {
	unsigned char t[4];
	int r, c;
	for (c = 0; c < 4; c++) {
		for (r = 0; r < 4; r++) {
			t[r] = state[r][c];
		}
		for (r = 0; r < 4; r++) {
			state[r][c] = FFmul(0x0e, t[r]) ^ FFmul(0x0b, t[(r + 1) % 4])
					^ FFmul(0x0d, t[(r + 2) % 4]) ^ FFmul(0x09, t[(r + 3) % 4]);
		}
	}
}

int Encrypt(unsigned char *_in, int _length,
		unsigned char *_out) {
	bool first_round = true;
	int rounds = 0;
	int start = 0;
	int end = 0;
	int i,j,k;
	int co_index = 0;
	unsigned char input[16] = { 0 };
	unsigned char output[16] = { 0 };
	unsigned char ciphertext[16] = { 0 };	
	unsigned char plaintext[16] = { 0 };

	//You need modify the buffer size of "cipherout" !!
	unsigned char cipherout[300] = { 0 };	

	rounds = _length / 16 + 1;

	for (j = 0; j < rounds; ++j) {
		start = j * 16;
		end = j * 16 + 16;

		if (j < rounds - 1) {
			memcpy(plaintext, _in + start, end - start);
		} else {
			int padding = 16 - _length % 16;
			memset(plaintext, padding, 16);
			memcpy(plaintext, _in + start, _length - start);
		}

		//use MODE_CBC
		for (i = 0; i < 16; ++i) {
			if (first_round == true) {
				input[i] = plaintext[i] ^ m_iv[i];
			} else {
				input[i] = plaintext[i] ^ ciphertext[i];
			}
		}
		first_round = false;
		Cipher(input, ciphertext);

		for (k = 0; k < end - start; ++k) {
			cipherout[co_index++] = ciphertext[k];
		}
	
	}
	memcpy(_out, cipherout, co_index);
	return co_index;
}

int Decrypt(unsigned char *_in, int _length,
		unsigned char *_out) {

	bool first_round = true;
	int rounds = 0;
	int po_index = 0;
	int i,j,k;
	int unpadding;
	int start = 0;
	int end = 0;	
	unsigned char ciphertext[16] = { 0 };
	unsigned char input[16] = { 0 };
	unsigned char output[16] = { 0 };
	unsigned char plaintext[16] = { 0 };
	
	//You need modify the buffer size of "plainout" !!
	unsigned char plainout[300] = { 0 };	
	
	if (_length % 16 == 0) {
		rounds = _length / 16;
	} else {
		wmprintf("input data must be multiples of 16 bytes\n");
		return -1;
	}

	for (j = 0; j < rounds; j++) {
		start = j * 16;
		end = start + 16;
		if (end > _length) {
			end = _length;
		}

		memcpy(ciphertext, _in + start, end - start);

		//MODE_CBC
		InvCipher(ciphertext, output);

		for (i = 0; i < 16; ++i) {
			if (first_round == true) {
				plaintext[i] = m_iv[i] ^ output[i];
			} else {
				plaintext[i] = input[i] ^ output[i];
			}
		}
		first_round = false;
		for (k = 0; k < end - start; ++k) {
			plainout[po_index++] = plaintext[k];
		}
		memcpy(input, ciphertext, 16);

	}
	memcpy(_out, plainout, po_index);

	//PKCS5UnPadding
	unpadding = (int) *(_out + _length - 1);
	if (0 < unpadding && unpadding <= AES_BLOCK_SIZE) {
		int i = 0;
		while (i < unpadding) {
			*(_out + _length - unpadding + i) = '\0';
			i++;
		}
		//end PKCS5UnPadding
		return _length - unpadding;
	}
	return po_index;
}




static struct sockaddr_in multi_addr;
struct sockaddr_in gateway_addr;
static int discover_sockfd = -1;

static int mulcast_recv_task(os_thread_arg_t data)
{
	static int one = 1;
	struct ip_mreq mc;
	struct sockaddr_in listen;
	int addr_len;
	int len;
	struct sockaddr_in client_addr;
	uint8_t *whois_str = "{\"cmd\":\"whois\"}";
	uint8_t msg_buf[300] = {0};
	uint8_t mcast_mac[6];

	KeyExpansion(m_key,w);

	/*
		You must make sure you have opened the mulcast function for your device,support IGMP V2
	*/
	
	addr_len = sizeof(struct sockaddr_in);
	discover_sockfd = net_socket(PF_INET, SOCK_DGRAM, 0);
	if (discover_sockfd < 0) {
		return;
	}

	setsockopt(discover_sockfd, SOL_SOCKET, SO_REUSEADDR, (char *)&one, sizeof(one));	
	
	/* because want to receive  report of the gateway ,so need to join multicast group 224.0.0.50 : 9898 */
	listen.sin_family = PF_INET;
	listen.sin_port = htons(GROUP_RECV_PORT);
	listen.sin_addr.s_addr = htonl(INADDR_ANY);
	if (net_bind(discover_sockfd, (struct sockaddr *)&listen, addr_len) < 0) {
		net_close(discover_sockfd);
		discover_sockfd = -1;
		return;
	}	
	mc.imr_multiaddr.s_addr = inet_addr(GROUP_SERVER_ADDR);
	mc.imr_interface.s_addr = htonl(INADDR_ANY);
	if (setsockopt(discover_sockfd, IPPROTO_IP, IP_ADD_MEMBERSHIP, (char *)&mc,
		       sizeof(mc)) < 0) {
		net_close(discover_sockfd);
		discover_sockfd = -1;
		return;
	}	

	//send "whois" to 224.0.0.50 : 4321
	multi_addr.sin_family = PF_INET;
	multi_addr.sin_port = htons(GROUP_DISCOVER_PORT);
	multi_addr.sin_addr.s_addr = inet_addr(GROUP_SERVER_ADDR);		
	sendto(discover_sockfd, whois_str, strlen(whois_str), 0, (struct sockaddr*)&multi_addr, sizeof(multi_addr));

	/*
		receive "iam" from one gateway
		eg. {"cmd":"iam","port":"9898","sid":"f0b429999470","model":"gateway","ip":"192.168.5.101"}
	*/
	recvfrom(discover_sockfd, msg_buf, sizeof(msg_buf), 0, (struct sockaddr *)&gateway_addr, (socklen_t *)&addr_len);	
	wmprintf("\r\nWhois response:%s\r\n",msg_buf);
	
	//here we use 9898, maybe use other port ,which is from "whois" response
	gateway_addr.sin_port = htons(UDP_SEND_PORT);

	while (1) {		
		memset(msg_buf, 0, sizeof(msg_buf));
		len = recvfrom(discover_sockfd, msg_buf, sizeof(msg_buf), 0, (struct sockaddr *)&client_addr, (socklen_t *)&addr_len);
		msg_buf[len] = '\0';

		/*
			Message Received, eg.
		
			{"cmd":"heartbeat","model":"gateway","sid":"f0b429999470","short_id":"0","token":"tyAlZCNaaFHlMR1O","data":"{\"ip\":\"192.168.5.101\"}"}
			{"cmd":"report","model":"switch","sid":"158d0000d43fbd","short_id":34836,"data":"{\"status\":\"click\"}"}
		*/

		
		if (strstr(msg_buf,"\"heartbeat\""))
		{
			char *token_ptr;
			if (token_ptr = strstr(msg_buf,"\"token\""))
			{
				//get token in heartbeat of gateway
				if (token_ptr = strstr(token_ptr + 7,"\""))
				{
					int32_t i;
					uint8_t out_key[16];
					memcpy(heart_token,token_ptr + 1,16);
					wmprintf("\r\n\r\n\r\nheart_token=%s\r\n\r\n\r\n",heart_token);
					
					//generate out_key by heart_token and m_key
					Encrypt(heart_token,16,out_key);
					for (i = 0;i < sizeof(out_key);i++)
					{
						sprintf(key_of_write + i*2,"%02x",out_key[i]);
					}
				}
			}
		}
		wmprintf("\r\n\r\n\r\nRecv Msg=%s\r\n\r\n\r\n",msg_buf);
	
	}
}

static void send_msg_to_gateway(uint8_t *data_str,int32_t data_len)
{
	if (-1 != discover_sockfd)
		sendto(discover_sockfd, data_str, data_len, 0, (struct sockaddr*)&gateway_addr, sizeof(gateway_addr));
}

void ctrl_plug_demo(uint64_t sid,uint8_t state)
{
	char cmd_buf[200] = {0};
	snprintf(cmd_buf,sizeof(cmd_buf),"{\"cmd\":\"write\",\"model\":\"plug\",\"sid\":\"%llx\",\"data\":\"{\\\"channel_0\\\":\\\"%s\\\",\\\"key\\\":\\\"%s\\\"}\"}",
		sid,
		(state ? "on": "off"),
		key_of_write);
	wmprintf("\r\n\r\n\r\nRecv Msg=%s\r\n\r\n\r\n",cmd_buf);
	send_msg_to_gateway(cmd_buf,strlen(cmd_buf));	
}