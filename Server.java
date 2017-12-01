import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;
public class Server implements Runnable{
	private Socket t = null;
    private ServerSocket ss = null;
    private boolean close = false;
    //ip
    private String ip="134.226.56.4";
    //port
    private Integer port=8888;
    //房间集合
    private ArrayList<Map<String,Object>> map = new ArrayList<Map<String,Object>>(); 
    //房间名
    private Map<String,String> roomName = new HashMap<String,String>();
    private ArrayList clients = new ArrayList();//保存客户端的线程
    JTextArea jta;
    int k=-1; 

    public static void main(String[] args) throws Exception{
        Server server = new Server();

    }

    public Server() throws Exception{   
        jta = new JTextArea();	
        ss = new ServerSocket(port);//服务器端开辟端口，接受连接
        new Thread(this).start();   //接受客户连接的死循环开始运行
    }
    public void run(){
        try{
            while(true){
                t = ss.accept();
                //接受到s之后，s就是当前的连接对应的Socket，对应一个客户端
                //该客户端随时可能发信息过来，必须要接收
                //另外开辟一个线程，专门为这个s服务，负责接受信息
                ChatThread ct = new ChatThread(t);  
                clients.add(ct);
                ct.start(); 
            }
        }catch(Exception ex){}
    }
    class ChatThread extends Thread{//为某个Socket负责接受信息
        private Socket s = null;
        private BufferedReader br = null;
        public PrintStream ps = null;
        public ChatThread(Socket s) throws Exception    {   
            this.s = s; 
            br = new BufferedReader(new InputStreamReader(s.getInputStream()));

            ps = new PrintStream(s.getOutputStream());
        }
        public void run(){
            try{
                while(true){
                    String str = br.readLine();//读取该Socket传来的信息
                    jta.append(str+"\r\n");
                    if(str.contains("JOIN_CHATROOM")||str.contains("LEAVE_CHATROOM")||str.contains("CHAT: ")||str.contains("DISCONNECT")||str.contains("KILL_SERVICE")){
                    	if(str!=null&&str.contains("KILL_SERVICE")){
                    		String[] split = str.trim().split("\\\\n");
                    		if("KILL_SERVICE".equals(split[0])){
                    			t.close();
                    			s.close();
                    		}
                    	}
                    	else if(str!=null&&str.contains("CHAT: ")){
                    		str.replaceAll(" ", "");
                    		String[] split = str.split(":");
                    		String[] split2 = split[1].trim().split("\\\\n");
                    		//jonid
                    		String[] jonid = split[2].trim().split("\\\\n");
                    		//dengluming
                    		String[] clientname = split[3].trim().split("\\\\n");
                    		//message
                    		String[] message = split[4].trim().split("\\\\n");
                    		for(int i=0;i<map.size();i++){
                    			if(map.get(i).get("ROOM_REF").toString().equals(split2[0])){
                    				Boolean isexit=true;
                    				for(int a=0;a<((ArrayList<Map<String,Object>>)map.get(i).get("stuList")).size();a++){
                    					if((((ArrayList<Map<String,Object>>)map.get(i).get("stuList")).get(a).get("JOIN_ID").toString()).equals(jonid[0])){
                    						isexit=false;
                    					}
                        			}
                    				if(isexit){
                    					ChatThread ct = new ChatThread(s); 
                    					ct.ps.println(str+"\n"+"IP:"+ip+"\n"+"Port:"+port+"\n"+"StudentID:17313074");
//                                       ct.ps.println(str);
//                                       ct.ps.println("StudentID:17313074");
//                                      ct.ps.println("IP:"+ip);
//                                       ct.ps.println("Port:"+port);
                    				}else{
                    					for(int a=0;a<((ArrayList<Map<String,Object>>)map.get(i).get("stuList")).size();a++){
//                    						if(!(((ArrayList<Map<String,Object>>)map.get(i).get("stuList")).get(a).get("JOIN_ID").toString()).equals(jonid[0])){
                        						ChatThread ctsa = new ChatThread((Socket)((ArrayList<Map<String,Object>>)map.get(i).get("stuList")).get(a).get("socket"));
                                                if(str!=null){
                                                	ctsa.ps.println("CHAT: "+split2[0]+"\n"+
                                                			"CLIENT_NAME: "+clientname[0]+"\n"+
                                                			"MESSAGE: "+message[0]+"\n"+"\n");
 //                                               	ctsa.ps.println("CHAT: "+split2[0]);
 //                                               	ctsa.ps.println("CLIENT_NAME: "+clientname[0]);
 //                                               	ctsa.ps.println("MESSAGE: "+message[0]+"\n\n");
                                                }
//                        					}
                    					}
                    				}
                    			}
                    		}
                    	}
                    	else if(str!=null&&str.contains("DISCONNECT")){
                    		String[] split = str.split(":");
                            //获取用户名
                        	String[] split2 = split[3].trim().split("\\\\n");
                        	for(int i=0;i<map.size();i++){
                        		for(int a=0;a<((ArrayList<Map<String,Object>>)map.get(i).get("stuList")).size();a++){
                        			if(((ArrayList<Map<String,Object>>)map.get(i).get("stuList")).get(a).get("StudentName").equals(split2[0])){
                        				((ArrayList<Map<String,Object>>)map.get(i).get("stuList")).remove(a);
                        				for(int b=0;b<((ArrayList<Map<String,Object>>)map.get(i).get("stuList")).size();b++){
                        						ChatThread ctsa = new ChatThread((Socket)((ArrayList<Map<String,Object>>)map.get(i).get("stuList")).get(b).get("socket"));
                                                if(str!=null){
                                                	ctsa.ps.println(split2[0]+" has left this chatroom.");
//                                                	ctsa.ps.println("CHAT: "+split2[0]);
//                                                	ctsa.ps.println("CLIENT_NAME: "+clientname[0]);
//                                                	ctsa.ps.println("MESSAGE: "+message[0]);
                                                }
                    					}
                    				}
                        		}
                        	}
                        	
                        	
                        	
                    	}
                    	else if(str!=null&&str.contains("JOIN_CHATROOM")){
                    		str.replaceAll("\\\\s", "");
                        	String[] split = str.split(":");

                        	String[] split2 = split[1].trim().split("\\\\n");
                        	//登录的用户名
                        	String[] clientName = split[4].trim().split("\\\\n");
                        	if(roomName.get(split2[0])==null){
                    			Map<String,Object> mp = new HashMap<String,Object>();
                    			mp.put("JOINED_CHATROOM", split2[0]);
                    			mp.put("ROOM_REF", map.size()+1);
                    			ArrayList<Map<String,Object>> arr = new ArrayList<Map<String,Object>>();
                    			Map<String,Object> stu = new HashMap<String,Object>();
                    			stu.put("JOIN_ID", arr.size()+1);
                    			stu.put("StudentName", clientName[0]);
                    			stu.put("socket", s);
                    			roomName.put(split2[0], split2[0]);
                    			int a= map.size()+1;
                    			int b=arr.size()+1;
                    			arr.add(stu);
                                mp.put("stuList", arr);
                                map.add(mp);
                            	ChatThread ctsab = new ChatThread(s); 
                            	ctsab.ps.println("JOINED_CHATROOM: "+split2[0]+"\\\n"+"SERVER_IP: "+ip+"\\\n"+"PORT: "+port+"\n"+"ROOM_REF: "+a+"\n"+"JOIN_ID: "+b+"\n");
//                                ctsab.ps.println("JOINED_CHATROOM: "+split2[0]);
//                                ctsab.ps.println("SERVER_IP: "+ip);
//                                ctsab.ps.println("PORT: "+port);
//                                ctsab.ps.println("ROOM_REF: "+a);
//                                ctsab.ps.println("JOIN_ID: "+b);
                                
                    			
                        	}else{
                    			Map<String,Object> stu = new HashMap<String,Object>();
                    			for(int i=0;i<map.size();i++){
                    				if(map.get(i).get("JOINED_CHATROOM").equals(split2[0])){
                    					Boolean isexit=true;
                    					for(int a=0;a<((ArrayList<Map<String,Object>>)map.get(i).get("stuList")).size();a++){
                    						//校验用户名是否正确，不正确推送消息
                            				if(!((ArrayList<Map<String,Object>>)map.get(i).get("stuList")).get(a).get("StudentName").equals(clientName[0])){
                                                ChatThread ctsa = new ChatThread((Socket)((ArrayList<Map<String,Object>>)map.get(i).get("stuList")).get(a).get("socket"));
                                                ctsa.ps.println(clientName[0]+ " has joined the chat room");
                            				}else{
                            					isexit=false;
                            				}
                            			}
                    					if(isexit){
                    						stu.put("JOIN_ID", ((ArrayList<Map<String,Object>>)map.get(i).get("stuList")).size()+1);
                                			stu.put("StudentName", clientName[0]);
                                			stu.put("socket", s);
                        					ChatThread ctsab = new ChatThread(s); 
                        					ctsab.ps.println("JOINED_CHATROOM: "+split2[0]+"\n"+
                        							"SERVER_IP: "+ip+"\n"+
                        							"PORT: "+port+"\n"+
                        							"ROOM_REF: "+(map.get(i).get("ROOM_REF"))+"\n");
//                                            ctsab.ps.println("JOINED_CHATROOM: "+split2[0]);
//                                            ctsab.ps.println("SERVER_IP: "+ip);
//                                            ctsab.ps.println("PORT: "+port);
//                                            ctsab.ps.println("ROOM_REF: "+(map.get(i).get("ROOM_REF")));
                                            int b=((ArrayList<Map<String,Object>>)map.get(i).get("stuList")).size()+1;
                                            ctsab.ps.println("JOIN_ID: "+b);
                                            ((ArrayList<Map<String,Object>>)map.get(i).get("stuList")).add(stu);
                    					}
                    					
                    				}
                    				
                    				
                    			}
                    			
                        	}
                        }
                     else if(str!=null&&str.contains("LEAVE_CHATROOM")){
                    	String[] split = str.split(":");
                     	String[] split2 = split[1].trim().split("\\\\n");
                     	String[] split3 = split[2].trim().split("\\\\n");
                     	//学生名字
                     	String[] stuName = split[3].trim().split("\\\\n");
                    	for(int i=0;i<map.size();i++){
                    		if(map.get(i).get("ROOM_REF").toString().equals(split2[0])){
                    			for(int a=0;a<((ArrayList<Map<String,Object>>)map.get(i).get("stuList")).size();a++){
                    				if((((ArrayList<Map<String,Object>>)map.get(i).get("stuList")).get(a).get("JOIN_ID").toString()).equals(split3[0])){
                    					ChatThread ctsa = new ChatThread((Socket)((ArrayList<Map<String,Object>>)map.get(i).get("stuList")).get(a).get("socket"));
                        				ctsa.ps.println("LEFT_CHATROOM: "+ split2[0]);
                    					ctsa.ps.println("JOIN_ID: "+ split3[0]);
                    					((ArrayList<Map<String,Object>>)map.get(i).get("stuList")).remove(a);
                    				}
                    			}
                    			for(int a=0;a<((ArrayList<Map<String,Object>>)map.get(i).get("stuList")).size();a++){
                    				ChatThread ctsa = new ChatThread((Socket)((ArrayList<Map<String,Object>>)map.get(i).get("stuList")).get(a).get("socket"));
                    				ctsa.ps.println(stuName[0]+" has left this chatroom.");
                    			}
                    			
                    		}
                    	}
                    } 
                    }else{
                        ChatThread ct = new ChatThread(s); 
                        ct.ps.println(str+"\n"+"IP:"+ip+"\n"+"Port:"+port+"\n"+"StudentID:17313074");
//                        ct.ps.println(str);
//                        ct.ps.println("IP:"+ip);
 //                       ct.ps.println("Port:"+port);
//                        ct.ps.println("StudentID:17313074");
                    }
 
                }
            }catch(Exception ex){}  
        }       
    }
    public void shutDown() throws IOException {
        if (ss != null) {
          synchronized (ss) {
            ss.close();
          }
        }
        System.out.println("StopClose.shutDown() complete");
      }
}
