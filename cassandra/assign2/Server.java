import java.rmi.*;
import java.rmi.registry.*;
import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.net.DatagramSocket;
 


public class Server{
    final static String INET_ADDR = "224.0.0.3";
    final static int PORT = 9999;

    public static void main(String args[]) throws UnknownHostException, InterruptedException, IOException {

	if (args.length != 1) {
	    System.out.println ("Correct Usage : java DistributedChat <myClientId>");
	    System.exit (1);
	}

	String id = args[0];
	
	//--------------------------------------------------------------------------------------
	/*------Code added by Rohan Gyani
	 * 
	 */
    
	InetAddress addr = InetAddress.getByName(INET_ADDR);
	// Create a buffer of bytes, which will be used to store
	// the incoming bytes containing the information from the server.
	// Since the message is small here, 256 bytes should be enough.

	String IP_addr;
	BufferedReader br = new BufferedReader (new InputStreamReader (System.in));
	IP_addr = br.readLine();

	Chat stub = new ChatRemote();
    
	try{
	    Naming.rebind("rmi://" + IP_addr + ":5000/" + id,stub);
	}
	catch(Exception e){System.out.println(e);}
    
	stub.setIdIp(id, IP_addr);
		
	// Open a new DatagramSocket, which will be used to send the data.
	try (DatagramSocket serverSocket = new DatagramSocket()) {
		String msg = id + ";" + IP_addr;

		// Create a packet that will contain the data
		// (in the form of bytes) and send it.
		DatagramPacket msgPacket = new DatagramPacket(msg.getBytes(),
							      msg.getBytes().length, addr, PORT);
		serverSocket.send(msgPacket);
	    } catch (IOException ex) {
	    ex.printStackTrace();
	}

	Input in  = new Input (stub);
	in.start();
	
	// Create a new Multicast socket (that will allow other sockets/programs
	// to join it as well.
	try (MulticastSocket clientSocket = new MulticastSocket(PORT)){
		//Joint the Multicast group.
		clientSocket.joinGroup(addr);
		while (true) {
		    byte[] buf = new byte[256];
		    // Receive the information and print it.
		    DatagramPacket msgPacket = new DatagramPacket(buf, buf.length);
		    clientSocket.receive(msgPacket);
		    String msg = new String(buf, 0, buf.length);
		    int index;
		    for (index = 0; index < msg.length(); ++index) {
			if (msg.charAt (index) == '\0') {
			    break;
			}
		    }
		    msg = msg.substring (0, index);
		    String [] parts = msg.split (";");
		    stub.insertIdIp (parts[0], parts[1]);
		}
	    } catch (IOException ex) {
	    ex.printStackTrace();
	}
	//----------------------------------------------------------------------------------------
    
    }

}
