import java.rmi.*;
import java.rmi.server.*;
import java.io.*;

public class AdderRemote extends UnicastRemoteObject implements Adder{

//stub1: Interaction
//stub2: keyword
String table1="interaction";
String table2="keyword";

AdderRemote()throws RemoteException{
super();
}

public int add(int x,int y){return x+y;}


//Code added by Rohan Gyani
public int login(String username, String password)
{
	//filename: login_cred
	String filename="login_cred.txt";
	String line=null;
	
	try{
		Filereader fl=new FileReader(filename);
		BufferedReader br=new BufferedReader(fl);
		while((line=br.readLine())!=null)
		{
			String[] parts=line.split(",");
			if(parts[0].equals(username))
			{
				if(parts[1].equals(password))
					return 1;
				return -1;	//Wrong Password
			}
		}
		return -1;	//Invalid Creds
	}
	catch(Exception ex)
	{
		System.out.println(ex);
	}

}

public int logout(String username)
{
	//All changes made on the client side
	return 1;
}

public String search_chat(String username, String username2)
{
	String s=stub1.get(table1, username, username2);
	return s;	//Need to process s at client
}

public String search_keyword(String username, String keyword)
{
	String s=stub2.get(table2, username, keyword);
	return s;	//Need to process s at client
}

public int send(String username, String msg, String username2)
{
	//Inserting Interaction
	stub1.insert(table1, username, username2, msg);
	String[] parts=msg.split(" ");

	//Inserting keyword
	int i;
	for(i=0;i<parts.length;i++)
	{
		stub2.insert(table2, username, parts[i], msg);
	}
	
	return 1;
}

}