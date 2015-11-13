package sender;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Date;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

class SenderThread extends Thread 
{
	  String t_filein;
	  int t_totalblock;
	  int t_startblock;
	  int t_endblock;
	   DataOutputStream cout;
	   RandomAccessFile cin;
	   InetAddress ip;
	
	public SenderThread(Socket m_socket, Socket socket, String file_in, int total_block, int start_block, int end_block, int layer) throws IOException, BiffException 
	{
		t_filein=file_in;
		t_totalblock=total_block;
		t_startblock=start_block;
		t_endblock=end_block;
		ip = socket.getLocalAddress();
		//send the layer information
		 DataOutputStream  mout = new DataOutputStream(m_socket.getOutputStream());
	     mout.writeInt(layer);
	     mout.flush();
	     //m_socket.close();
	     System.out.println(" The output is from the layer: " + layer);
	     
	     cin = new RandomAccessFile(file_in, "r");
		 cin.seek(t_startblock*Constant.TRANSFER_BUFFER);       //set the start read pointer
		 cout = new DataOutputStream(socket.getOutputStream());
		 start();
     }
  
	//thread main method
	public void run()
	{
		 //calculate the parameters for transferring in 60KB
		 int send_times = 0;
		 long last_bytes = 0;
		 int send_block = t_endblock - t_startblock;
		 //if the part is the last part, specially tackle it, because the last block may be not a full block.
		 if(t_endblock == t_totalblock)
		 {
			 long total_bytes = t_filein.length();
			 send_times = send_block/15;
			 last_bytes = (total_bytes - Constant.START_BLOCK*4096-send_times*61440);
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
			cout.writeInt(Constant.START_BLOCK);
			cout.writeInt(send_block);
			cout.writeUTF(Constant.FILE_IN);
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
			System.out.println("The send time of"+ ip+"  is :  " + duration);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
