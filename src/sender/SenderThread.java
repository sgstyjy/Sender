package sender;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.Socket;

import jxl.read.biff.BiffException;

class SenderThread extends Thread 
{
	  String t_filein;
	  int t_totalblock;
	  int t_startblock;
	  int t_endblock;
	  int t_layer;
	   DataOutputStream cout;
	   RandomAccessFile cin;
	   InetAddress ip;
	
	public SenderThread(Socket socket, String file_in, int total_block, int start_block, int end_block, int layer) throws IOException, BiffException 
	{
		t_filein=file_in;
		t_totalblock=total_block;
		t_startblock=start_block;
		t_endblock=end_block;
		t_layer = layer;
		ip = socket.getLocalAddress();
		
	    cin = new RandomAccessFile(file_in, "r");
		cin.seek(t_startblock*Constant.TRANSFER_BUFFER);       //set the start read pointer
		cout = new DataOutputStream(socket.getOutputStream());
		start();
    }
  
	public void run()
	{
		 //calculate the parameters for transferring in 60KB
		 int send_times = 0;
		 long last_bytes = 0;
		 int send_block = t_endblock - t_startblock;
		 //if the part is the last part, specially tackle it, because the last block may be not a full block.
		 if(t_endblock == t_totalblock)
		 {
			 long total_bytes = 0;
			try {
				total_bytes = cin.length();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 System.out.println("The total bytes are:  " + total_bytes);
			 send_times = send_block/15;
			 last_bytes = (total_bytes - t_startblock*4096-send_times*61440);
		 }
		 else
		 {
			 send_times = send_block/15;
			 last_bytes = (send_block%15)*4096;
		 }
		 
		 System.out.println("The start block number is:  " + t_startblock);
		 System.out.println("The end block number is:  " + t_endblock);
		 System.out.println("The send times are:  " + send_times);
		 System.out.println("The last bytes are:  " + last_bytes);
		 
		try {
			cout.writeInt(t_startblock);
			cout.writeInt(send_block);
			cout.writeUTF(t_filein);
			cout.flush();
			System.out.println("Send the basic information end!");
			
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
			Long duration = endtime - Constant.START_TIME;
			System.out.println("The send time of"+ ip+ " of layer " + t_layer + " is: " + duration);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
