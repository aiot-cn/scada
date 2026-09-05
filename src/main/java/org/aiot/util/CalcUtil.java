package org.aiot.util;

import org.aiot.model.enums.PatternEnum;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

import java.util.ArrayList;
import java.util.List;

public class CalcUtil {
	/**	ModBus 通信协议的 CRC ( 冗余循环校验码含2个字节, 即 16 位二进制数。
	CRC 码由发送设备计算, 放置于所发送信息帧的尾部。
	接收信息设备再重新计算所接收信息 (除 CRC 之外的部分）的 CRC,
	比较计算得到的 CRC 是否与接收到CRC相符, 如果两者不相符, 则认为数据出错。

	1) 预置 1 个 16 位的寄存器为十六进制FFFF(即全为 1) , 称此寄存器为 CRC寄存器。
	2) 把第一个 8 位二进制数据 (通信信息帧的第一个字节) 与 16 位的 CRC寄存器的低 8 位相异或, 把结果放于 CRC寄存器。
	3) 把 CRC 寄存器的内容右移一位( 朝低位)用 0 填补最高位, 并检查右移后的移出位。
	4) 如果移出位为 0, 重复第 3 步 ( 再次右移一位); 如果移出位为 1, CRC 寄存器与多项式A001 ( 1010 0000 0000 0001) 进行异或。
	5) 重复步骤 3 和步骤 4, 直到右移 8 次,这样整个8位数据全部进行了处理。
	6) 重复步骤 2 到步骤 5, 进行通信信息帧下一个字节的处理。
	7) 将该通信信息帧所有字节按上述步骤计算完成后,得到的16位CRC寄存器的高、低字节进行交换。
	8) 最后得到的 CRC寄存器内容即为 CRC码。*/
    public static int getCRC16(byte[] bytes) {

    	int CRC = 0x0000ffff;
        int POLYNOMIAL = 0x0000a001;

        int i, j;
        for (i = 0; i < bytes.length; i++) {
            CRC ^= ((int) bytes[i] & 0x000000ff);
            for (j = 0; j < 8; j++) {
                if ((CRC & 0x00000001) != 0) {
                    CRC >>= 1;
                    CRC ^= POLYNOMIAL;
                } else {
                    CRC >>= 1;
                }
            }
        }
         //高低位转换
		return ((CRC & 0x0000FF00) >> 8) | ( (CRC & 0x000000FF ) << 8);
    }

	public  static boolean isModbusCRC16(byte[] bytes){
		if(bytes == null || bytes.length < 5)
			return false;
		byte[] b1 = new byte[bytes.length-2];
		System.arraycopy(bytes, 0, b1, 0, b1.length);
		int crc = getCRC16(b1);
		int crc2 = CalcUtil.byteToInt(new byte[]{0,0,bytes[bytes.length-2],bytes[bytes.length-1]});
		return crc == crc2;
	}

    public static String crcModbus(String hex) {
    	byte[] bytes = hexToByte(hex);
    	int s = getCRC16(bytes);
    	return Strings.alignRight(Integer.toHexString(s), 4, '0').toUpperCase();
    }

	/**
	 * 获取校验位(前面的数据位相加，去低位)
	 * @param s
	 * @return
	 */
	public static String crcYnen(String s) {
		s = s.replaceAll("[ ]", "");
		List<String> list = new ArrayList<String>();
		for(int i=0;i<s.length();i=i+2){
			String temp = s.substring(i,Math.min(i+2, s.length()));
			list.add(temp);
		}
		int sum = 0;
		for(int i=0; i<list.size(); i++){
			String temp = list.get(i);
			sum += Integer.parseInt(temp, 16);
		}
		String hexString = Strings.num2hex(sum).toUpperCase();
		return hexString.substring(hexString.length()-2, hexString.length());
	}

	/**
	 * 和校验取低位
	 * @param hex
	 * @return
	 */
	public static String checkSumLow(String hex){
		//"02 0A 03 29 32 00 00 00 00 E8 03 - AA"
		byte[] a = hexToByte(hex);
		int c = 0;
		for(byte b : a){
			c += b;
		}
		byte crc = (byte) (~c & 0xFF);
		return String.format("%02x",crc).toUpperCase();
	}

	/**
	 * 和校验取低8位
	 * @param data
	 * @return
	 */
	public static String add8(String data) {
		if (data == null || data.equals("")) {
			return "";
		}
		data = data.replaceAll(" ", "");
		int total = 0;
		int len = data.length();
		int num = 0;
		while (num < len) {
			String s = data.substring(num, num + 2);
			total += Integer.parseInt(s, 16);
			num = num + 2;
		}

		int mod = total % 256;
		String hex = Integer.toHexString(mod);
		len = hex.length();
		// 如果不够校验位的长度，补0,这里用的是两位校验
		if (len < 2) {
			hex = "0" + hex;
		}
		return hex.toUpperCase();
	 }

	/**
	 * 异或校验
	 * @param hex
	 * @return
	 */
	public static String Xor(String hex) {
		byte[] a = hexToByte(hex);
		byte temp = a[0];
		for (int i = 1; i < a.length; i++) {
			temp ^= a[i];
		}
		return String.format("%02x",temp).toUpperCase();
	}

	/**
	 * 把16进制字符串转换成字节数组
	 *
	 * @param hex
	 * @return byte[]
	 */
	public static byte[] hexToByte(String hex) {
		if(hex != null){
			hex = hex.replaceAll(" ", "");
		}
		if(hex == null || !PatternEnum.HEX.matches(hex)){
			throw Lang.makeThrow("[%s] does not match the hex format.", hex);
		}

		int len = (hex.length() / 2);
		byte[] result = new byte[len];
		char[] achar = hex.toCharArray();
		for (int i = 0; i < len; i++) {
			result[i] = (byte) Integer.parseInt(achar[i * 2] + "" + achar[i * 2 + 1], 16);
		}
		return result;
	}

	/**
	 * byte数组转换成十六进制字符串
	 *
	 * @param bArray
	 * @return HexString
	 */
	public static String byteToHex(byte[] bArray) {
		if(bArray == null){
			return "";
		}
		StringBuffer sb = new StringBuffer();
		for (byte b : bArray) {
			String hex = Integer.toHexString(0xff & b);
			if (hex.length() == 1) {
				sb.append('0');
			}
			sb.append(hex);
		}
		return sb.toString().toUpperCase();
	}

	/**
	 * int到byte[] 由高位到低位
	 */
	public static byte[] intToByte(int i) {
		byte[] result = new byte[4];
		result[0] = (byte)((i >> 24) & 0xFF);
		result[1] = (byte)((i >> 16) & 0xFF);
		result[2] = (byte)((i >>  8) & 0xFF);
		result[3] = (byte)( i & 0xFF);
		return result;
	}

	public static byte[] floatToByte(float f) {
		int i = Float.floatToIntBits(f);
		return intToByte(i);
	}

	/**
	 * byte[]转int 高位在前
	 */
	public static int byteToInt(byte... bytes){
		int srcLen = Math.min(4,bytes.length);
		byte[] b = new byte[4];
		System.arraycopy(bytes,0,b,4-srcLen,srcLen);
		return  ((b[0] & 0xFF) << 24) |
				((b[1] & 0xFF) << 16) |
				((b[2] & 0xFF) << 8)  |
				( b[3] & 0xFF);
	}

	/**
	 *低位在前
	 */
	public static int byteToInt2(byte[] bytes) {
		int value = 0;
		for(int i = 0; i < 4; i++) {
			int shift = i * 8;
			value += (bytes[i] & 0xff) << shift;
		}
		return value;
	}

	public static byte[] shortToByte(int i) {
		byte[] result = new byte[2];
		result[0] = (byte)((i >> 8) & 0xFF);
		result[1] = (byte)(i & 0xFF);
		return result;
	}

	/**
	 * << 表示左移移，不分正负数，低位补0；
	 * >> 表示右移，如果该数为正，则高位补0，若为负数，则高位补1；
	 * >>>表示无符号右移，也叫逻辑右移，即若该数为正，则高位补0，而若该数为负数，则右移后高位同样补0
	 */
	public static int byteToBit(byte b,int i){
		return (b >> i) & 0x1;
	}

	/**
	 * 设置byte的i位为1
	 */
	public static byte setBit1(byte b,int i){
		return (byte)(b | (0x1 << i));
	}

	public static Short toShort(short v){
		return v;
	}
	public static Float toFloat(float v){
		return v;
	}

	public static byte[] byteJoin(byte[] b1,byte[] b2){
		byte[] c = new byte[b1.length+b2.length];
		System.arraycopy(b1, 0, c, 0, b1.length);
		System.arraycopy(b2, 0, c, b1.length, b2.length);
		return c;
	}

}
