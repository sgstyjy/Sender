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
		//@para args[1]  the total blocks;
		//@para args[2]  the start block number;
		//@para args[3]  the end block number;
		//@para args[4]  the destination IP address and port, for example: 127.0.0.1:24
		//@para args[5]  the client ID;
		 Constant.FILE_IN = args[0];
		 int total_block = Integer.parseInt(args[1]);
		 Constant.START_BLOCK=  Integer.parseInt(args[2]);
		 Constant.END_BLOCK =  Integer.parseInt(args[3]);
		 String part0 = args[4].split("\\:")[0];
		 String part1 = args[4].split("\\:")[1];
		 byte[] ip = getIpByte(part0);
		 int port = Integer.parseInt(part1);
		 //int cid = Integer.parseInt(args[5]);
		 
		 int send_block = Constant.END_BLOCK - Constant.START_BLOCK;
		InetAddress addr = InetAddress.getByAddress(ip);
		
	      System.out.println("The outputs are from the Client: ");
	      System.out.println("The send image file is:  " + Constant.FILE_IN);
	      System.out.println("The start block number is:  " + Constant.START_BLOCK);
	      System.out.println("The end block number is:  " + Constant.END_BLOCK);
         
	           //read the start time
	      Long starttime = System.currentTimeMillis();
	      	      
	       Socket socket = new Socket(addr, port);
		   socket.setSoTimeout(6000);
		   RandomAccessFile cin = new RandomAccessFile(Constant.FILE_IN,"r");
		   cin.seek(Constant.START_BLOCK*Constant.TRANSFER_BUFFER);
		   System.out.println("The start pointer  is:  " + cin.getFilePointer());
		   
		   DataOutputStream cout = new DataOutputStream(socket.getOutputStream());
		   //send the client ID, start block, end block, and file name
		  // cout.writeInt(cid);
		   cout.writeInt(total_block);
		   cout.writeInt(Constant.START_BLOCK);
		   cout.writeInt(send_block);
		   cout.writeUTF(Constant.FILE_IN);
		   cout.flush();
		   //send data blocks
		   int i = 0 ;
		   byte[] bb =  new byte[Constant.TRANSFER_BUFFER];
		   int send_length = 0;
		   send_length =  cin.read(bb);
		   while (i<send_block && send_length!=-1)
			{
			     cout.write(bb, 0, send_length);
	    		 cout.flush();
	    		  i++;
	    		 send_length =  cin.read(bb);
			}	
		   Long endtime  = System.currentTimeMillis();
		   Long duration = endtime - starttime;
		   System.out.println("The send time is :  " + duration);
		   System.out.println("The actual send blocks are :  " + i);
		   socket.close();
		   cin.close();
		   cout.close();
		   return;
	}
	
	//translate the String IP to byte[] IP
   private static byte [] getIpByte(String ipAddress) {
       String [] ipStr = ipAddress.split("\\.");
       byte [] ipByte = new byte[ipStr.length];
       for(int i = 0 ; i < ipStr.length; i++) {
           ipByte[i] = (byte)(Integer.parseInt(ipStr[i])&0xFF);
       } 
       return ipByte;
   }

}
