/**
 * ScriptEngine对象扩展
 * @Copyright Copyright (c) 2018
 * @author dutaojin
 */

//十六进制转十进制 有符号
function hexToInt(hex) {
	var dec = parseInt(hex,16); //parseInt结果为number,64位的
	var a = new ArrayBuffer(4);
	var v = new Int32Array(a);
	v[0] = dec;
	return v[0];
}

//十进制字符串转Float（上传）
function decToFloat(txt){
	var dec = parseFloat(txt);
	var a = new ArrayBuffer(4);
	var v = new Float32Array(a);
	v[0] = dec;
	return org.aiot.util.CalcUtil.toFloat(v[0]);
}

//十六进制转短整型 有符号（上传）
function hexToShort(hex) {
	return txtToShort(hex,16);
}

//十进制转短整型 有符号
function decToShort(txt) {
	return txtToShort(txt,10);
}

function txtToShort(txt,radis) {
	var dec = parseInt(txt,radis);
	var a = new ArrayBuffer(2);
	var v = new Int16Array(a);
	v[0] = dec;
	return org.aiot.util.CalcUtil.toShort(v[0]);
}

//十六进制转短整型 低位在前
function hexToShort2(hex) {
	return hexToShort(hex.slice(2)+hex.slice(0,2));
}

//ASCII码转十六进制
function asciiToHex(str) {
	if (str === "") {
		return "";
	} else {
		var hexCharCode = [];
		hexCharCode.push("0x");
		for (var i = 0; i < str.length; i++) {
			hexCharCode.push((str.charCodeAt(i)).toString(16));
		}
		return hexCharCode.join("");
	}
}

//十六进制转ASCII码
function hexToAscii(str) {
	var trimedStr = str.trim();
	var rawStr = trimedStr.substr(0, 2).toLowerCase() === "0x" ? trimedStr.substr(2) : trimedStr;
	var len = rawStr.length;
	if (len % 2 !== 0) {
		//alert("存在非法字符!");
		return "";
	}
	var curCharCode;
	var resultStr = [];
	for (var i = 0; i < len; i = i + 2) {
		curCharCode = parseInt(rawStr.substr(i, 2), 16);
		resultStr.push(String.fromCharCode(curCharCode));
	}
	return resultStr.join("");
}


//16进制转二进制，二进制字符串不满足N位的以0补充
function hexToBitPad(hex, n) {
	var bit = parseInt(hex, 16).toString(2);
	while (n > bit.length) {
		bit = '0' + bit;
	}
	return bit;
}

//获取十六进制转二进制的第N位
function hexBit(hex,n){
	var bit = parseInt(hex, 16).toString(2);
	if(n >= bit.length){
		return "0";
	}else{
		var statr = bit.length - n -1;
		return bit.slice(statr,statr+1);
	}
}

//十六进制转有符号浮点数
function hexToFloat32(hex){
	var dec = parseInt(hex,16);
	var b = new ArrayBuffer(4);
	//这里的十六进制不会是负数，所以是无符号的
	var v1 = new Uint32Array(b);
	v1[0] = dec;
	var v2 = new Float32Array(b);
	return v2[0];
}

//获取直线角度 参考点P0
function lineAngle(p1, p2, p0) {
	var y = p1[1]-p2[1];
	var x = p1[0]-p2[0];
	if(p0){
		var d1 = (p1[0] - p0[0])*(p1[0] - p0[0]) + (p1[1] - p0[1])*(p1[1] - p0[1]);
		var d2 = (p2[0] - p0[0])*(p2[0] - p0[0]) + (p2[1] - p0[1])*(p2[1] - p0[1]);
		if(d1 > d2){
			y = -y;
			x = -x;
		}
	}
	var angleRadians = Math.atan2(y, x);
	return (angleRadians * (180 / Math.PI) + 360) % 360;
}

//等距量表换算
function intervalScale(interval,scale){
	for(var i=0;i<interval.length-1;i++){
		var v1 = interval[i];
		var v2 = interval[i+1]
		if(scale >= v1[1] && scale < v2[1]){
			return  v1[0] + (scale - v1[1]) * (v2[0] - v1[0])/(v2[1] - v1[1]);
		}
	}
}

//角度刻度 旋转使得角度范围不跨0
function angleScale(interval,scale,rotate){
	var t = [];
	for(var i=0;i<interval.length;i++){
		t.push([interval[i][0],(interval[i][1] + rotate + 360) % 360]);
	}
	var last = t[t.length - 1];
	var scale2 = (scale + rotate + 360) % 360;
	if(scale2 < t[0][1])
		return t[0][0];
	if(scale2 > last[1])
		return last[0];
	return  intervalScale(t,scale2);
}

function script_IEC104(val,analysis,data,bd,command){
	if(val.length < 20)
		return "";
	var type = val.slice(12,14);
	var t2 = val.slice(14,16);//可变结构限定词
	var t3 = val.slice(16,18);
	var m1 = {"01":"单点遥信","03":"双点遥信","09":"整型遥测","0D":"浮点型遥测","1E":"SOE事件记录","24":"M-ME-TF-1=测量值,带时标CP56TimE2A的短浮点数","2D":"单点遥控","2E":"双点遥控","32":"浮点遥调","25":"电度","64":"总召","67":"对时",};
	var m2 = {"01":"周期、循环","02":"背景扫描","03":"突发、自发上传","04":"初始化","05":"请求或被请求","06":"激活","07":"激活确认","08":"停止激活","09":"停止激活确认","0A":"激活结束","14":"响应总招","2F":"未知信息体地址"};
	var msg = (m1[type] || type) + ":" + (m2[t3] || t3) + (hexBit(t2,7) == 1 ? "(连续)" : "(非连续)") + " 数量:"+(parseInt(t2,16) & 0x7F) +" ";

	if(type == "01"){
		var t = val.slice(24);
		for(var i=0;i<t.length/8;i++){
			var a = t.substring(i*8,(i+1)*8);
			var address = a.slice(4,6) + a.slice(2,4) + a.slice(0,2);
			var v = a.slice(7,8);
			address = parseInt(address,16)+"";
			bd.put(address,v).setType(1);
			msg += address + ":" + v + " ";
		}
	}else if(type == "0D"){
		var t = val.slice(24);
		for(var i=0;i<t.length/16;i++){
			var a = t.substring(i*16,(i+1)*16);
			var address = a.slice(4,6) + a.slice(2,4) + a.slice(0,2);
			var b = a.slice(12,14) + a.slice(10,12) + a.slice(8,10) + a.slice(6,8);
			address = parseInt(address,16)+"";
			var v = hexToFloat32(b)+"";
			bd.put(address,v).setType(0);
			msg += address + ":" + v + " ";
		}
	}else if(type == "2D"){
		var a = val.slice(24);
		var address = a.slice(4,6) + a.slice(2,4) + a.slice(0,2);
		var v = a.slice(7,8);
		address = parseInt(address,16)+"";
		bd.put(address,v).setType(2);
		msg += address + ":" + v + " ";
	}else if(type == "32"){
		var a = val.slice(24);
		var address = a.slice(4,6) + a.slice(2,4) + a.slice(0,2);
		var b = a.slice(12,14) + a.slice(10,12) + a.slice(8,10) + a.slice(6,8);
		address = parseInt(address,16)+"";
		var v = hexToFloat32(b)+"";
		bd.put(address,v).setType(3);
		msg += address + ":" + v + " ";
	}
	return msg;
}

function resModbus(val,analysis,data,bd,command){
	var outByte = command.getBs();
	var inByte =  command.getRX();
	var errCode = {1:"非法功能",2:"非法地址",3:"非法数据值",4:"从站设备故障",5:"确认",6:"从属设备忙",8:"存储奇偶性差错",10:"不可用网关路径",11:"网关目标设备响应失败"};
	var fCode = inByte[1];
	if(fCode < 0)
		fCode += 256;

	if(fCode != outByte[1]){
		if(fCode - outByte[1] == 0x80)
			return "错误:"+errCode[inByte[2]+""];
		return "错误码:"+inByte[2];
	}

	var outBuffer = new ArrayBuffer(outByte.length + outByte.length%2);
	var inBuffer = new ArrayBuffer(inByte.length + inByte.length%2);

	var outInt8 = new Int8Array(outBuffer);
	var inInt8 = new Int8Array(inBuffer);
	for(var i=0;i<outByte.length - outByte.length % 2;i++){
		outInt8[i] = outByte[i % 2 == 0 ? i+1 : i-1];
	}

	var ba3 = new Uint16Array(outBuffer);
	var start = ba3[1],count=ba3[2];
	var msg = "起始位:"+start;

	//1 读线圈（输出Y） 2 读输入物理离散量（输入X）
	if(fCode == 1 || fCode == 2){
		msg += " 数量:"+count;
		var t = fCode == 1 ? "Y" : "X"
		var v = "";
		var UI8 = new Uint8Array(inBuffer);
		for(var i=3;i<inByte.length;i++){
			inInt8[i] = inByte[i];
			v += (UI8[i].toString(2).split("").reverse().join("")+"0000000").slice(0,8);
		}
		for(var i=0;i<count;i++){
			var code = t+(start+i);
			msg += " "+code+":"+v[i];
			bd.put(code,v[i]);
		}

	}else if(fCode == 3){//读多个寄存器(D)
		msg += " 数量:"+count;
		for(var i=0;i<inByte.length-3;i++){
			inInt8[i] = inByte[i+3 + (i%2==0?1:-1)];
		}
		var UI16 = new Uint16Array(inBuffer);
		for(var i=0;i<count;i++){
			var code = "D"+(start+i);
			msg += " "+code+":"+UI16[i];
			bd.put(code,UI16[i]);
		}

	}else if(fCode == 4){//读输入寄存器

	}else if(fCode == 5){//写单个线圈
		msg += " 数量:1";
		var code = "Y"+start;
		var val = count == 0 ? 0 : 1;
		msg += " "+code+":"+val;
		bd.put(code,val);
	}else if(fCode == 6){//写单个寄存器
		msg += " D"+start+":"+count;
	}else if(fCode == 0x0F){//写多个线圈
		msg += " 数量:"+count;
		var v = "";
		var UI8 = new Uint8Array(outBuffer);
		for(var i=7;i<outByte.length;i++){
			outInt8[i] = outByte[i];
			v += (UI8[i].toString(2).split("").reverse().join("")+"0000000").slice(0,8);
		}
		for(var i=0;i<count;i++){
			var code = "Y"+(start+i);
			msg += " "+code+":"+v[i];
			bd.put(code,v[i]);
		}
	}else if(fCode == 0x10){//写多个寄存器
		msg += " 数量:"+count;
		for(var i=0;i<outByte.length-7;i++){
			inInt8[i] = outByte[i+7 + (i%2==0?1:-1)];
		}
		var UI16 = new Uint16Array(inBuffer);
		for(var i=0;i<count;i++){
			var code = "D"+(start+i);
			msg += " "+code+":"+UI16[i];
			bd.put(code,UI16[i]);
		}

	}
	return msg;
}

function now(){
	return (new Date).format("yyyy-MM-dd hh:mm:ss");
}

Date.prototype.format = function(format) {
	if(isNaN(this.getTime()))
		return "";
	var o = {
		"M+" : this.getMonth() + 1,
		"d+" : this.getDate(),
		"h+" : this.getHours(),
		"m+" : this.getMinutes(),
		"s+" : this.getSeconds(),
		"q+" : Math.floor((this.getMonth() + 3) / 3),
		"S" : this.getMilliseconds()
	};
	if (/(y+)/.test(format)) {
		format = format.replace(RegExp.$1, (this.getFullYear() + "")
			.substr(4 - RegExp.$1.length));
	}
	for ( var k in o) {
		if (new RegExp("(" + k + ")").test(format)) {
			format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k]
				: ("00" + o[k]).substr(("" + o[k]).length));
		}
	}
	return format;
};
//print(__FILE__, __LINE__, __DIR__);
print("[JavaScript ] analysis.js 加载成功");
