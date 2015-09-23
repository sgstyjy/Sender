package sender;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import jxl.read.biff.BiffException;

public class Sender {

	public static void main(String[] args) throws BiffException, IOException {
          //args[0]  the send image file name; args[1]  the destination IP address
		 Constant.FILE_IN = args[0];
		  InetAddress addr = InetAddress.getByName(args[1]);
		// InetAddress addr = InetAddress.getByName("localhost");
	      System.out.println("The outputs are from the Client: ");
	      System.out.println("The send image file is:  " + Constant.FILE_IN);
         
	           //read the start time
	  		 Date date = new Date();
	  	    Long starttime = date.getTime();
	  		
		      //for(threadNo = 0;threadNo<3;threadNo++)
	           new SenderThread(addr,Constant.PORT1);
	           
	     		//read the end time
	     		Date date1 = new Date();
	     		Long endtime = date1.getTime();
	     		Long duration = endtime-starttime;
	     		System.out.println("The send time  is:"+duration);
	}

}
