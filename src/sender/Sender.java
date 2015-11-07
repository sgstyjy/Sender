package sender;

import java.io.DataOutputStream;
import java.io.File;
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
		//@para args[4]  the destination IP address
		 Constant.FILE_IN = args[0]; 
		 File file_in = new File(Constant.FILE_IN);
		 int total_block = Integer.parseInt(args[1]);
		 Constant.START_BLOCK=  Integer.parseInt(args[2]);
		 Constant.END_BLOCK =  Integer.parseInt(args[3]);
		 byte[] ip = getIpByte(args[4]);
		 
		 //calculate the parameters for transferring in 60KB
		 int send_times = 0;
		 long last_bytes = 0;
		 int send_block = Constant.END_BLOCK - Constant.START_BLOCK;
		 //if the part is the last part, specially tackle it, because the last block may be not a full block.
		 if(Constant.END_BLOCK == total_block)
		 {
			 long total_bytes = file_in.length();
			 send_times = send_block/15;
			 last_bytes = (total_bytes - Constant.START_BLOCK*4096-send_times*61440);
		 }
		 else
		 {
			 send_times = send_block/15;
			 last_bytes = (send_block%15)*4096;
		 }
	      System.out.println("The outputs are from the Client: ");
	      System.out.println("The send image file is:  " + Constant.FILE_IN);
	      System.out.println("The start block number is:  " + Constant.START_BLOCK);
	      System.out.println("The end block number is:  " + Constant.END_BLOCK);
	      System.out.println("The send times are:  " + send_times);
	      System.out.println("The last bytes are:  " + last_bytes);
         
	        //read the start time
	       Long starttime = System.currentTimeMillis();
	      	
	       //construtct the socket
	       InetAddress addr = InetAddress.getByAddress(ip);
	       Socket socket = new Socket(addr, Constant.PORT);
		   //socket.setSoTimeout(6000); 
	       //System.out.println("The timeout  is:  " + socket.getSoTimeout());
		   RandomAccessFile cin = new RandomAccessFile(Constant.FILE_IN,"r");
		   cin.seek(Constant.START_BLOCK*Constant.TRANSFER_BUFFER);       //set the start read pointer
		   //System.out.println("The start pointer  is:  " + cin.getFilePointer());
		   DataOutputStream cout = new DataOutputStream(socket.getOutputStream());
		   
		   //send the total block, start block, end block, and file name
		   cout.writeInt(send_times);
		   cout.writeLong(last_bytes);
		   cout.writeInt(Constant.START_BLOCK);
		   cout.writeInt(send_block);
		   cout.writeUTF(Constant.FILE_IN);
		   cout.flush();
		   
		   //send data blocks
		   int i = 0 ;
		   byte[] sb = new byte [15* Constant.TRANSFER_BUFFER];
		   
		   int send_length = 0;
	        send_length =  cin.read(sb);
	        //System.out.println("The send length is :  " + send_length);
		   while (send_length!=-1 && i<send_times)
			{
			    	cout.write(sb, 0, send_length);
			    	cout.flush();
			    	i++;
			    	send_length =  cin.read(sb);
			}	
		   //write the last part of data in the buffer
		   cout.write(sb, 0, (int) last_bytes);
		   cout.flush();
		   System.out.println("The actual send times is: " + i);
		   //send end message
		   String end = "end";
		   sb=end.getBytes();
		   cout.write(sb, 0, sb.length);
		   cout.flush();
		   //calculate the send duration
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
       String [] ipStr = ipAddress.split("\\.");
       byte [] ipByte = new byte[ipStr.length];
       for(int i = 0 ; i < ipStr.length; i++) {
           ipByte[i] = (byte)(Integer.parseInt(ipStr[i])&0xFF);
       } 
       return ipByte;
   }

}
