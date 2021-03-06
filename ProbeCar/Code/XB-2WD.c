/**********************XB-2WD 智能车*******************************
*  平台：XB-2WD + Keil4 + STC89C52    
*  日期：2018-5-30
*  晶振:11.0592MHZ
******************************************************************/

/******************************************************************
**                        头文件
******************************************************************/
#include<AT89X52.H>	  

/******************************************************************
**                       接线定义
******************************************************************/
#define Left_moto_go      {P1_2=1,P1_3=0;}    //左电机正转
#define Left_moto_back    {P1_2=0,P1_3=1;} 	 //左电机反转
#define Left_moto_Stop    {P1_0=0;}                  //左电机停转                     
#define Right_moto_go     {P1_4=1,P1_5=0;}	 //右电机正转
#define Right_moto_back   {P1_4=0,P1_5=1;}	 //右电机反转
#define Right_moto_Stop   {P1_1=0;}	                 //右电机停转 

#define Left_moto_pwm	  P1_0	 //PWM信号端
#define Right_moto_pwm	  P1_1	  //PWM信号端
#define Left_1_led        P3_4	 // 左传感器       
#define Right_1_led       P3_5	 //右传感器 

sbit BUZZ = P3^6;                                    //蜂鸣器引脚

/******************************************************************
**                       PWM调速相关变量
******************************************************************/
unsigned char pwm_val_left  =0;                     //变量定义
unsigned char push_val_left =0;                      //左电机占空比N/20
unsigned char pwm_val_right =0;
unsigned char push_val_right=0;                      //右电机占空比N/20
bit Right_moto_stop=1;
bit Left_moto_stop =1;
unsigned  int  time=0;

/******************************************************************
**                       延时函数
******************************************************************/	
void delay(unsigned int k){    
     unsigned int x,y;
	 for(x=0;x<k;x++) 
	   for(y=0;y<2000;y++);
}

/******************************************************************
**                       小车前进
******************************************************************/
void  front_run(void){
	 push_val_left=5;
	 push_val_right=5;
	 Left_moto_go;   
	 Right_moto_go;  
}

/******************************************************************
**                       小车左转
******************************************************************/
void  left_run(void){
	 push_val_left=5;
	 push_val_right=5;
	 Left_moto_Stop;   
	 Right_moto_go;  
}

/******************************************************************
**                       小车右转
******************************************************************/
void  right_run(void){
	 push_val_left=5;
	 push_val_right=5;
	 Right_moto_Stop;   
	 Left_moto_go;  
}

/******************************************************************
**                       小车停走
******************************************************************/
void  stop(void){
	 push_val_left=5;
	 push_val_right=5;
	 Left_moto_Stop
	 Right_moto_Stop;    
}

/******************************************************************
**                       左电机调速
******************************************************************/
void pwm_out_left_moto(void){  
	if(Left_moto_stop){
	    if(pwm_val_left<=push_val_left)
			Left_moto_pwm=1;
		else
			Left_moto_pwm=0;

		if(pwm_val_left>=20)
		    pwm_val_left=0;
	}
	else
		Left_moto_pwm=0;
}

/******************************************************************
**                       右电机调速
******************************************************************/
void pwm_out_right_moto(void){ 
	if(Right_moto_stop){ 
    	if(pwm_val_right<=push_val_right)
	    	Right_moto_pwm=1; 
		else
			Right_moto_pwm=0;
		
		if(pwm_val_right>=20)
			pwm_val_right=0;
    }
    else
		Right_moto_pwm=0;
}

/******************************************************************
**                       定时器0初始化
******************************************************************/
void timer0_Init(void){
	TMOD=0X01;
	TH0= 0XFc;		  //1ms定时
	TL0= 0X18;
	TR0= 1;
	ET0= 1;
	EA = 1;			   //开总中断	
}
           
/******************************************************************
**                   定时器0中断服务子程序
******************************************************************/
void timer0() interrupt 1 using 2 {
     TH0=0XFc;	  
	 TL0=0X18;
	 time++;
	 pwm_val_left++;
	 pwm_val_right++;
	 pwm_out_left_moto();
	 pwm_out_right_moto();
}

/******************************************************************
**                       主函数
******************************************************************/
void main(void){
	unsigned char i;
	stop();	            //小车停转

B:	for(i=0;i<50;i++){   //判断按键S4是否按下
		delay(1);	     //1ms内判断50次，如果其中有一次被判断到K4没按下，便重新检测
		if(P2_7!=0)      //当K4按下时，启动小车前进 按下P2_7=0，不按P2_7=0
		goto B;          //跳转到标号B，重新检测  
	}
	BUZZ=0;	             //50次检测S5确认是按下之后，蜂鸣器发出“滴”声响，然后启动小车。
	delay(50);
	BUZZ=1;              //响50ms后关闭蜂鸣器

	//中断实现PWM控速
    timer0_Init();
	
	//沿左白右黑的分界线走
	while(1){
		//1表示黑，0表示白。红外线探测
		if(Left_1_led==0&&Right_1_led==1)	//左白右黑
		   front_run();   //调用前进函数
		else{
			if(Left_1_led==1&&Right_1_led==1){	//左黒右黑
				//小车左转
				left_run();
			}
			if(Right_1_led==0&&Left_1_led==0){	//左白右白
				//小车右转
				right_run();
		    }
		}
	}
}
/******************************************************************
**                        结束
******************************************************************/