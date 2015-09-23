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
	//client socket
	private Socket socket;    
	//client ID
    int cid = Constant.CLIENT_ID++;
	private DataOutputStream cout;
	//private FileInputStream cin;
	RandomAccessFile cin;
	
	public SenderThread(InetAddress addr, int port) throws IOException, BiffException 
	{
		//File IO
		//File file_in = new File(Constant.FILE_IN);
		//cin = new FileInputStream(file_in);    
		cin = new RandomAccessFile(Constant.FILE_IN,"r");
		long size = cin.length();
		System.out.println("The length of original file is: "+ size);
		
		socket = new Socket(addr, port);
		socket.setSoTimeout(6000);
		cout = new DataOutputStream(socket.getOutputStream());
		
		//start thread
		start();
     }
  
	//thread main method
	public void run()
	{
		byte[] bb = new byte[Constant.TRANSFER_BUFFER];
		int i =0;
		int sendbytes =0;
		int sendblocks = 0;
		int send_length = 0;
		try {
				//read the similarity table
				File file_in2 = new File(Constant.COMPARE_U12_U14WEB_4K);
				//InputStream hashtable2 = new FileInputStream(file_in2);
				Workbook book = Workbook.getWorkbook(file_in2);
				Sheet sheet = book.getSheet(0);
		       
				//read the items in compare result table, get the block numbers, and send the data 
	    		while(i<Constant.TOTALBLOCKS){
	    			String tempstr = sheet.getCell(i/Constant.COLUMNS, i%Constant.COLUMNS).getContents();
	    			if(!tempstr.equals("A") ){
	    			    long templ = Long.parseLong(tempstr);
	    			    cin.seek(templ*Constant.TRANSFER_BUFFER);
	    			   send_length = cin.read(bb, 0, Constant.TRANSFER_BUFFER);
		    			cout.write(bb, 0, send_length);
		    			cout.flush();
		    			sendbytes += send_length;
		    			sendblocks++;
		    			send_length=cin.read(bb, 0, Constant.TRANSFER_BUFFER);
		    			i++;
	    			}
	    			else
	    				i++;	
			    }
	    		socket.close();
	    		cin.close();
	    		cout.close();
	    		System.out.println("The total blocks are:"+i );
				System.out.println("The send data blocks are:"+sendblocks);
				System.out.println("The data amount of is [in bytes]:"+sendbytes);
		} catch (IOException | BiffException e) {
			e.printStackTrace();
		}
	}
}
