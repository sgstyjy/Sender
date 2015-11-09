package sender;

public class Constant {
	public static String FILE_IN = "default_filein.qcow2";
	public static String FILE_OUT = "default_fileout.qcow2";
	public static int START_BLOCK = 0;
	public static int END_BLOCK =0;
	//public static String FILE_IN = "test.txt";
	//public static String FILE_OUT = "testresult.txt";
	//public static String FILE_IN = "test.zip";
	//public static String FILE_OUT = "testresult.zip";
	public static String BLOCKNUM_OUT = "blocknum.txt";
	public static String SIMILARTABLE = "com_u12_u14web_4k.xls";
	public static String COMPARE_U14DEV_U14WEB_4K = "com_u14dev_u14web_4k.xls";
	
	public static int TOTALBLOCKS = 0;
    public static int TRANSFER_BUFFER = 4*1024;
	public static int COLUMNS = 10000;
    public static int CLIENT_ID = 0;
    public static long STARTTIME  = 0;
    public static long ENDTIME  = 0;
    public static int MPORT = 6212;
    public static int OSPORT=6444;
    public static int WEPORT=6445;
    public static int UDPORT=6446;
}