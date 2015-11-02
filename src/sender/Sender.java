package sender;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.Socket;

import jxl.read.biff.BiffException;

public class Sender {

	public static void main(String[] args) throws BiffException, IOException {
        //@para args[0]  the send image file name;
		//@para args[1]  the start block number;
		//@para args[2]  the end block number;
		//@para args[3]  the destination IP address
		//@para args[4]  the client ID;
		 Constant.FILE_IN = args[0];
		 Constant.START_BLOCK=  Integer.parseInt(args[1]);
		 Constant.END_BLOCK =  Integer.parseInt(args[2]);
		 int total_block = Constant.END_BLOCK - Constant.START_BLOCK;
		 
		byte[] ip = getIpByte(args[3]);
		 //System.out.println(ip);
		InetAddress addr = InetAddress.getByAddress(ip);
		
		int cid = Integer.parseInt(args[4]);
		 // InetAddress addr = InetAddress.getByName("localhost");
		 //InetAddress addr = InetAddress.getByAddress("141.5.103.57");
	      System.out.println("The outputs are from the Client: ");
	      System.out.println("The send image file is:  " + Constant.FILE_IN);
	      System.out.println("The start block number is:  " + Constant.START_BLOCK);
	      System.out.println("The end block number is:  " + Constant.END_BLOCK);
         
	           //read the start time
	      Long starttime = System.currentTimeMillis();
	      	      
	       Socket socket = new Socket(addr, Constant.PORT);
		   socket.setSoTimeout(6000);
		   RandomAccessFile cin = new RandomAccessFile(Constant.FILE_IN,"r");
		   cin.seek(Constant.START_BLOCK);
		   DataOutputStream cout = new DataOutputStream(socket.getOutputStream());
		   //send the client ID, start block, end block, and file name
		   cout.writeInt(cid);
		   cout.writeInt(Constant.START_BLOCK);
		   cout.writeInt(total_block);
		   cout.writeUTF(Constant.FILE_IN);
		   cout.flush();
		   //send data blocks
		   int i = 0 ;
		   byte[] bb =  new byte[Constant.TRANSFER_BUFFER];
		   int send_length = 0;
		   while (i<total_block)
			{
				 send_length =  cin.read(bb);
				 cout.write(bb, 0, send_length);
	    		 cout.flush();
				  i++;
			}	
		   Long endtime  = System.currentTimeMillis();
		   Long duration = endtime - starttime;
		   System.out.println("The send time is :  " + duration);
		   socket.close();
		   cin.close();
		   cout.close();
		   
		   return;
	}
	
	//translate the String IP to byte[] IP
   private static byte [] getIpByte(String ipAddress) {
       String [] ipStr = ipAddress.split("\\.");//以"."拆分字符串
       byte [] ipByte = new byte[ipStr.length];
       for(int i = 0 ; i < ipStr.length; i++) {
           // 进行byte转换后不是会有溢出吗？为什么溢出后还是可以正确运行？
           ipByte[i] = (byte)(Integer.parseInt(ipStr[i])&0xFF);//调整整数大小,byte的数值范围为-128~127
       } 
       return ipByte;
   }

}
