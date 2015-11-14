package sender;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.Socket;

import jxl.read.biff.BiffException;

public class Sender {

	public static void main(String[] args) throws BiffException, IOException, InterruptedException {
        //@para args[0] the total layer number
		//@para for each layer 
		/*the parameters for each layer are same:
		 * [1]  the file name;
		 * [2]  the total blocks;
		 * [3]  the start block number;
		 * [4]  the end block number;
		*/
		//@para args[last one]  the destination IP address
		
		//receive parameters from input
		 int total_layer = Integer.parseInt(args[0]);
		 String file0 = null, file1 = null, file2= null;
		 int total_block0=0, total_block1=0, total_block2=0;
		 int start_block0=0, start_block1=0, start_block2=0;
		 int end_block0=0, end_block1=0, end_block2=0;
		 
		 //read the start time
	     Constant.START_TIME = System.currentTimeMillis();
	    
		 //send data according to the total layer information
	     //the total layer is 1, it is just for testing the IO speed and network speed
	     //the total layer is 2, send the WE and UD layers's data
	     //the total layer is 3, send the three layers's data
	     
	     //just send the OS layer, this is for testing the transfer speed
	     if(total_layer == 1)
		 {
			  file1 = args[1]; 
			  total_block1 = Integer.parseInt(args[2]);
			  start_block1=  Integer.parseInt(args[3]);
			  end_block1 =  Integer.parseInt(args[4]);
			 
			 Constant.IP = getIpByte(args[5]);
			 
			 Constant.ADDRESS = InetAddress.getByAddress(Constant.IP);
			 
			//send the total layer information
			 Socket m_socket = new Socket(Constant.ADDRESS, Constant.MPORT);
			 DataOutputStream  mout = new DataOutputStream(m_socket.getOutputStream());
		     mout.writeInt(total_layer);
		     mout.flush();
		     m_socket.close();
		     System.out.println(" The output is from the client: " + m_socket.getLocalSocketAddress());
		     
		     //create thread to send data blocks 
		     Socket os_socket = new Socket(Constant.ADDRESS, Constant.OSPORT);
		     Thread os_thread = new SenderThread(os_socket,  file1,  total_block1,  start_block1,  end_block1, 0);
		     
		     os_thread.join();
			 os_socket.close();
		 }
		 //just send the WE and UD layers
		 if(total_layer == 2)
		 {
			  file1 = args[1]; 
			  total_block1 = Integer.parseInt(args[2]);
			  start_block1=  Integer.parseInt(args[3]);
			  end_block1 =  Integer.parseInt(args[4]);
			 
			  file2 = args[5]; 
			  total_block2 = Integer.parseInt(args[6]);
			 start_block2=  Integer.parseInt(args[7]);
			  end_block2 =  Integer.parseInt(args[8]);
			 
			 Constant.IP = getIpByte(args[9]);
			 
			 Constant.ADDRESS = InetAddress.getByAddress(Constant.IP);
			 
			//send the total layer information
			 Socket m_socket = new Socket(Constant.ADDRESS, Constant.MPORT);
			 DataOutputStream  mout = new DataOutputStream(m_socket.getOutputStream());
		     mout.writeInt(total_layer);
		     mout.flush();
		     m_socket.close();
		     System.out.println(" The output is from the client: " + m_socket.getLocalSocketAddress());
		     
		     //create thread to send data blocks   
		     Socket we_socket = new Socket(Constant.ADDRESS, Constant.WEPORT);
		     Thread we_thread = new SenderThread(we_socket,  file1,  total_block1,  start_block1,  end_block1,  1);
	    	 Socket ud_socket = new Socket(Constant.ADDRESS, Constant.UDPORT);
	    	 Thread ud_thread = new SenderThread(ud_socket,  file2,  total_block2,  start_block2,  end_block2,  2);
		     
		     we_thread.join();
			 we_socket.close();
			 ud_thread.join();
			 ud_socket.close();
		 }
		 //send all three layers
		 if(total_layer ==3)
		 {
			  file0 = args[1]; 
			  total_block0 = Integer.parseInt(args[2]);
			  start_block0=  Integer.parseInt(args[3]);
			  end_block0 =  Integer.parseInt(args[4]);
			 
			  file1 = args[5]; 
			  total_block1 = Integer.parseInt(args[6]);
			  start_block1=  Integer.parseInt(args[7]);
			  end_block1 =  Integer.parseInt(args[8]);
			 
			  file2 = args[9]; 
			  total_block2 = Integer.parseInt(args[10]);
			  start_block2=  Integer.parseInt(args[11]);
			  end_block2 =  Integer.parseInt(args[12]);
			 
			 Constant.IP = getIpByte(args[13]);
			 
			 Constant.ADDRESS = InetAddress.getByAddress(Constant.IP);
			 
			//send the total layer information
			 Socket m_socket = new Socket(Constant.ADDRESS, Constant.MPORT);
			 DataOutputStream  mout = new DataOutputStream(m_socket.getOutputStream());
		     mout.writeInt(total_layer);
		     mout.flush();
		     m_socket.close();
		     System.out.println(" The output is from the client: " + m_socket.getLocalSocketAddress());
		     
		     Socket os_socket = new Socket(Constant.ADDRESS, Constant.OSPORT);
		     Thread os_thread = new SenderThread(os_socket,  file0,  total_block0,  start_block0,  end_block0,  0);
	    	 Socket we_socket = new Socket(Constant.ADDRESS, Constant.WEPORT);
	    	 Thread we_thread = new SenderThread(we_socket,  file1,  total_block1,  start_block1,  end_block1,  1);
	    	 Socket ud_socket = new Socket(Constant.ADDRESS, Constant.UDPORT);
	    	 Thread ud_thread = new SenderThread(ud_socket,  file2,  total_block2,  start_block2,  end_block2,  2);
		     
		     os_thread.join();
			 os_socket.close();
			 we_thread.join();
			 we_socket.close();
			 ud_thread.join();
			 ud_socket.close();
		 }
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
