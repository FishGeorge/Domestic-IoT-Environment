# **IOT_SmartLab**
## ***Introduction***
---
Establish an IOT environment in the lab!
<br>运用多传感器搭建局域物联网，打造一个智能实验室(:P)，目的为积累生活数据以用于隐私相关研究。

## ***Release Notes***
---
<font size=4>局域网传感器记录系统</font>
---
更新时间|版本|内容
:--|:--|:--
2018.4.24|v0.0.1|来到实验室210
2018.5.18|v0.1|Lumi通信协议demo
2018.8.2|v0.2|#首次得到了网关应答<br>#确认“米家空调伴侣”(lumi_partner_v2)当前版本不适用“绿米局域网通信协议”
2018.8.7|v0.3|#8.4 硬件环境搭建完成<br>#结合lumi通信协议重构了通信demo<br>#重写了项目简介
2018.8.8|v0.4.0|#添加了AES-CBC-128加密功能<br>#AES功能对lumi通信协议进行了适配（但未进行测试）
2018.8.9|v0.4.2|#对AES加密功能进行了少量添改，提供HeartBeat消息的Get接口<br>#通过了AES加密功能（Write操作）部分的测试，测试demo见Test_CommuProto.java_main()
2018.8.16|v0.5.0|#适配了Lumi通信协议全部功能<br>#proto输出尚未接口化

<font size=4>信号探测车</font>
---
更新时间|版本|内容
:--|:--|:--
2018.5.30|v0.0.1|寻分界线demo

## ***To Do***
---
#尽快上线&实地部署局域网传感器记录系统<br>
#清晰明了人见人爱的doc<br>
#一个简洁的数据库GUI<br>
#一个足够美观的传感器数据实时GUI<br>