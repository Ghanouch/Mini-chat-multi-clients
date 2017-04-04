/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatsalon.coteServer;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

/**
 *
 * @author l.IsSaM.l
 */
public class ServerSalon {
    
    public static LinkedList<Client> listClients= new LinkedList<Client>();
    public static Boolean NewClient = false ;
    
    public static LinkedList<Client> getClients()
    {
        return listClients;
    }
    
    public static void refresh() throws IOException, InterruptedException
    {
        Thread.currentThread().sleep(500);
        PrintWriter pr ;
        for ( Client cl : listClients)
        {
            if( cl.Occuped == false)
            {
                pr = new PrintWriter(cl.getSocket().getOutputStream());
                pr.print("\033[H\033[2J");
                pr.print(new Client().getAllLogin());
            }
                  
        }
    }
    
    public void ServerSalon()
    {
       listClients = new LinkedList<Client>();
    }
    
    public static void main(String[] str) throws Exception
    {
        
        System.out.println(" Démarrage Serveur ");
        ServerSocket ss = new ServerSocket(8000);
        int i = 0 ;
        while(true)
        {
            Socket sc = ss.accept();
            Thread Tclt = new Thread(new Client(sc));
            Tclt.start();
            refresh();
        }
        
       
    }
    
}
