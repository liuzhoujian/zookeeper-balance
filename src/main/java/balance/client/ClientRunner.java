package balance.client;

import balance.client.inters.BalanceProvider;
import balance.client.inters.Client;
import balance.client.inters.impl.ClientImpl;
import balance.client.inters.impl.DefaultBalanceProvider;
import balance.server.model.ServerData;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ClientRunner {
	
	    private static final int  CLIENT_QTY = 3;
		private static final String  ZOOKEEPER_SERVER = "hadoop102:2181,hadoop103:2181,hadoop104:2181";
	    private static final String  SERVERS_PATH = "/servers";
		
		public static void main(String[] args) {
			List<Thread> threadList = new ArrayList<Thread>(CLIENT_QTY);
			final List<Client> clientList = new ArrayList<Client>();
			final BalanceProvider<ServerData> balanceProvider = new DefaultBalanceProvider(ZOOKEEPER_SERVER, SERVERS_PATH);
			
			try{
			
				for(int i = 0; i < CLIENT_QTY; i++){
					
					Thread thread = new Thread(new Runnable() {
						
						public void run() {					
							Client client = new ClientImpl(balanceProvider);
							clientList.add(client);
							try {
								client.connect();								
							} catch (Exception e) {
								e.printStackTrace();
							}											
						}
					});			
					threadList.add(thread);
					thread.start();
					//延时
					Thread.sleep(2000);
					
				}
				
	            System.out.println("敲回车键退出！\n");
	            new BufferedReader(new InputStreamReader(System.in)).readLine();
	            
				
			} catch(Exception e){
				e.printStackTrace();
			} finally{
	            //关闭客户端
				for (int i=0; i<clientList.size(); i++){
					try {
						clientList.get(i).disconnect();
					} catch (Exception ignore) {
						//ignore
					}					
				}
				//关闭线程
				for (int i=0; i<threadList.size(); i++){
					threadList.get(i).interrupt();
					try{
						threadList.get(i).join();
					}catch (InterruptedException e){
						//ignore
					}								
				}								
			}			
		}
}
