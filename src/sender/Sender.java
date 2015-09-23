package sender;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import jxl.read.biff.BiffException;

public class Sender {

	public static void main(String[] args) throws BiffException, IOException {
		  int threadNo = 0;
	      InetAddress addr = InetAddress.getByName("localhost");
	      System.out.println("The outputs are from the Client: ");
	      //for(threadNo = 0;threadNo<3;threadNo++)
	           new SenderThread(addr,Constant.PORT1);
	}

}
