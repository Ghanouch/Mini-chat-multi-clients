/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatsalon.coteServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author l.IsSaM.l
 */
public class Client implements Runnable {

    private static String loginDes;
    static Scanner sc;
    static PrintStream out;
    public Boolean Occuped = false;
    private String login;
    private String password;
    private Socket socket;
    private Scanner in;
    private PrintStream pr;

    public Client()
            
    {
        
    }
    public Client(String login, String password, Socket Socket) {
        this.login = login;
        this.password = password;
        this.socket = Socket;
    }

    public Client(Socket sci) throws Exception {
        socket = sci;
        pr = new PrintStream(socket.getOutputStream());

        in = new Scanner(socket.getInputStream());

        sc = in;
        out = pr;

    }

    public String getAllLogin() {
        String AllLogin = "";
        for (Client client : ServerSalon.getClients()) {
            AllLogin += client.login.equals(login) && client.password.equals(password) ? "" : "-- " + client.getLogin() + " \n";
        }

        return AllLogin;
    }

    public void newClient() throws Exception {

        pr.println(" Tapez Votre Login : ");
        login = in.nextLine();
        pr.println(" Tapez Votre Password : ");
        password = in.nextLine();

        ServerSalon.listClients.add(new Client(login, password, socket));
        chatWithFreind();
    }

    public Client getDestination(String login) {

        for (Client clt : ServerSalon.listClients) {
            if (clt.getLogin().equals(login)) {
                return clt;
            }
        }
        return null;
    }

    private class Check implements Runnable {

        @Override
        public void run() {

            while (true) {
                while (ServerSalon.NewClient == false);
                ServerSalon.NewClient = false;
                pr.print("\033[H\033[2J");
                pr.println(getAllLogin());

            }

        }

    }

    public void chatWithFreind() throws Exception {

        pr.print("\033[H\033[2J");
        String loginDestination = "";
        Client SocketDestination = null;
        PrintStream outDestination;
        Scanner scannerDestination;
        String msgEmi = "", msgRecu = "";
        if (ServerSalon.listClients.size() > 1) {
            pr.println(" Bienveue Chere : " + login + " Vous devez choisir votre destination : ");
            pr.println(getAllLogin());

            do {
                pr.print(" Que Sera Votre Destination : ");
                loginDestination = in.nextLine();
                System.out.println(loginDestination + " --");
                SocketDestination = getDestination(loginDestination);

            } while (SocketDestination == null);
            loginDes = loginDestination;
            Occuped = true;
            outDestination = new PrintStream(SocketDestination.getSocket().getOutputStream());
            scannerDestination = new Scanner(SocketDestination.getSocket().getInputStream());

            outDestination.print("\033[H\033[2J");
            pr.print("\033[H\033[2J");
            outDestination.print("----------------------------- DEBUT DE TCHAT -----------------------------\n");
            pr.print("----------------------------- DEBUT DE TCHAT -----------------------------\n");

            Thread envoi = new Thread(new Runnable() {

                public void run() {
                    String msgEmi = "";

                    while (true) {

                        msgEmi = Client.sc.nextLine();
                        outDestination.print("\n" + login + " : " + msgEmi);

                    }
                }

            });

            Thread recepter = new Thread(new Runnable() {

                public void run() {
                    String msgRecu = "";

                    while (true) {

                        msgRecu = scannerDestination.nextLine();
                        Client.out.print("\n" + Client.loginDes + " : " + msgRecu);

                    }
                }

            });

            envoi.start();
            recepter.start();

        } else {
            pr.println(" Bienveue Chere : " + login);
            pr.println(" Il n existe aucun personne disponible ");

        }
    }

    public Boolean isExist() {

        for (Client cl : ServerSalon.getClients()) {
            if (cl.getLogin().equals(login) && cl.getPassword().equals(password)) {
                return true;
            }
        }

        System.out.println(" Invalide Compte");
        return false;
    }

    public void oldClient() throws Exception {
        int i = 0;

        do {
            if (i++ > 0) {
                pr.print(" Tapez S pour sortir ");
                if (Character.toLowerCase(in.nextLine().charAt(0)) == 's') {
                    newClient();
                }
            }
            pr.println(" Login  : ");
            login = in.nextLine();
            pr.println(" Password  : ");
            password = in.nextLine();

        } while (!isExist());

        chatWithFreind();

    }

    @Override
    public void run() {
        System.out.println(" New Client ");

        try {
            char rep;

            pr.println(" Vous avez un Login  Oui:O  ||  Non:N");
            rep = in.nextLine().charAt(0);
            System.out.println(" reponse " + rep);
            switch (Character.toLowerCase(rep)) {
                case 'o':
                    oldClient();
                    break;
                case 'n':
                    newClient();
                    break;
                default:
                    pr.println(" Saisissez votre reponse  Oui:O  ||  Non:N ");
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public Socket getSocket() {
        return socket;
    }

}
