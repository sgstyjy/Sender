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
		 * [2]  the layer number; 0 is the OS layer, 1 is the WE layer, 2 is the UD layer
		 * [3]  the total blocks;
		 * [4]  the start block number;
		 * [5]  the end block number;
		*/
		//@para args[last one]  the destination IP address
		
		//receive parameters from input
		 int total_layer = Integer.parseInt(args[0]);
		 String file0 = null, file1 = null, file2= null;
		 int  layer0=0, layer1=0, layer2=0;
		 int total_block0=0, total_block1=0, total_block2=0;
		 int start_block0=0, start_block1=0, start_block2=0;
		 int end_block0=0, end_block1=0, end_block2=0;
		 
		 //just send the WE and UD layers
		 if(total_layer == 2)
		 {
			 file1 = args[1]; 
			  layer1 = Integer.parseInt(args[2]);
			  total_block1 = Integer.parseInt(args[3]);
			 start_block1=  Integer.parseInt(args[4]);
			  end_block1 =  Integer.parseInt(args[5]);
			 
			  file2 = args[6]; 
			  layer2 = Integer.parseInt(args[7]);
			  total_block2 = Integer.parseInt(args[8]);
			 start_block2=  Integer.parseInt(args[9]);
			  end_block2 =  Integer.parseInt(args[10]);
			 
			 Constant.IP = getIpByte(args[11]);
		 }
		 //send all three layers
		 if(total_layer ==3)
		 {
			  file0 = args[1]; 
			  layer0 = Integer.parseInt(args[2]);
			  total_block0 = Integer.parseInt(args[3]);
			 start_block0=  Integer.parseInt(args[4]);
			  end_block0 =  Integer.parseInt(args[5]);
			 
			  file1 = args[6]; 
			  layer1 = Integer.parseInt(args[7]);
			  total_block1 = Integer.parseInt(args[8]);
			 start_block1=  Integer.parseInt(args[9]);
			  end_block1 =  Integer.parseInt(args[10]);
			 
			  file2 = args[11]; 
			  layer2 = Integer.parseInt(args[12]);
			  total_block2 = Integer.parseInt(args[13]);
			 start_block2=  Integer.parseInt(args[14]);
			  end_block2 =  Integer.parseInt(args[15]);
			 
			 Constant.IP = getIpByte(args[16]);
		 }
		 	 
		 //read the start time
	     Constant.START_TIME = System.currentTimeMillis();
	    
	     
		 //firstly construct socket to send the layer information
	     InetAddress addr = InetAddress.getByAddress(Constant.IP);
	     Socket m_socket = new Socket(addr, Constant.MPORT);
	     System.out.println("The outputs are from: " +m_socket.getLocalAddress());
	     Socket os_socket = null, we_socket = null, ud_socket = null;
	     Thread os_thread = null, we_thread = null, ud_thread = null;
	     if(total_layer ==2)
	     {
	    	  we_socket = new Socket(addr, Constant.WEPORT);
	    	  we_thread = new SenderThread(m_socket, we_socket,  file1,  total_block1,  start_block1,  end_block1,  layer1);
		      ud_socket = new Socket(addr, Constant.UDPORT);
		      ud_thread = new SenderThread(m_socket, ud_socket,  file2,  total_block2,  start_block2,  end_block2,  layer2);
	     }
	     if (total_layer ==3)
	     {
	    	  os_socket = new Socket(addr, Constant.OSPORT);
	    	  os_thread = new SenderThread(m_socket, os_socket,  file0,  total_block0,  start_block0,  end_block0,  layer0);
	    	  we_socket = new Socket(addr, Constant.WEPORT);
	    	  we_thread = new SenderThread(m_socket, we_socket,  file1,  total_block1,  start_block1,  end_block1,  layer1);
		      ud_socket = new Socket(addr, Constant.UDPORT);
		      ud_thread = new SenderThread(m_socket, ud_socket,  file2,  total_block2,  start_block2,  end_block2,  layer2);
	     }
	     
	     os_thread.join();
	     os_socket.close();
	     we_thread.join();
		we_socket.close();
		ud_thread.join();
		ud_socket.close();
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
