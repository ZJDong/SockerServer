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
    private Integer port=8090;
    //���伯��
    private ArrayList<Map<String,Object>> map = new ArrayList<Map<String,Object>>(); 
    //������
    private Map<String,String> roomName = new HashMap<String,String>();
    private ArrayList clients = new ArrayList();//����ͻ��˵��߳�
    JTextArea jta;
    int k=-1; 

    public static void main(String[] args) throws Exception{
        Server server = new Server();

    }

    public Server() throws Exception{
//        this.setTitle("��������");
//        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jta = new JTextArea();
//        this.add(jta,"Center");
//        this.setSize(400,300);
//        this.setVisible(true);      
        ss = new ServerSocket(port);//�������˿��ٶ˿ڣ���������
        new Thread(this).start();   //���ܿͻ����ӵ���ѭ����ʼ����
    }
    public void run(){
        try{
            while(true){
                t = ss.accept();
                //���ܵ�s֮��s���ǵ�ǰ�����Ӷ�Ӧ��Socket����Ӧһ���ͻ���
                //�ÿͻ�����ʱ���ܷ���Ϣ����������Ҫ����
                //���⿪��һ���̣߳�ר��Ϊ���s���񣬸��������Ϣ
                ChatThread ct = new ChatThread(t);  
                clients.add(ct);
                ct.start();
//                ct.ps.println("Connection Successful for host "+ip+" on port "+port+".");
 
            }
        }catch(Exception ex){}
    }
    class ChatThread extends Thread{//Ϊĳ��Socket���������Ϣ
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
                    String str =null;
                    while(true){
                    	if(str==null){
                        	str=br.readLine();
                        }else{
                        	break;
                        }
                    }
                    
//                    	br.readLine();//��ȡ��Socket��������Ϣ

//                    jta.append(str+"\r\n");
                    System.out.println("Input Message："+" "+str);
                    if(str!=null){
                        if(str.contains("JOIN_CHATROOM")||str.contains("LEAVE_CHATROOM")||str.contains("CHAT: ")||str.contains("DISCONNECT")||str.contains("KILL_SERVICE")){
                        	System.out.println("this is input"+str);
                        	if(str!=null&&str.startsWith("KILL_SERVICE")){
//                        		String[] split = str.trim().split("\\\\n");
//                        		if("KILL_SERVICE".equals(split[0])){
//                        			t.close();
//                        			s.close();
//                        		}
                        	}
                        	else if(str!=null&&str.startsWith("CHAT: ")){
                        		str.replaceAll(" ", "");
                        		String str2=br.readLine();
                        		String str3=br.readLine();
                        		String str4=br.readLine();
                        		if(null!=str&&str2.startsWith("JOIN_ID")&&str3.startsWith("CLIENT_NAME")&&str4.startsWith("MESSAGE")){
                            		String[] split = str.split(": ");
                            		
                            		
                            		str2.replaceAll(" ", "");
                            		String jonid[]=str2.split(": ");
                            		
                            		
                            		str3.replaceAll(" ", "");
                            		String clientname[]=str3.split(": ");
                            		
                            		
                            		str4.replaceAll(" ", "");
                            		String message[]=str4.split(": ");
//                            		String[] split2 = split[1].trim().split("\\\\n");
//                            		//jonid
//                            		String[] jonid = split[2].trim().split("\\\\n");
//                            		//dengluming
//                            		String[] clientname = split[3].trim().split("\\\\n");
//                            		//message
//                            		String[] message = split[4].trim().split("\\\\n");
                            		for(int i=0;i<map.size();i++){
                            			if(map.get(i).get("ROOM_REF").toString().equals(split[0])){
                            				Boolean isexit=true;
                            				for(int a=0;a<((ArrayList<Map<String,Object>>)map.get(i).get("stuList")).size();a++){
                            					if((((ArrayList<Map<String,Object>>)map.get(i).get("stuList")).get(a).get("JOIN_ID").toString()).equals(jonid[1])){
                            						isexit=false;
                            					}
                                			}
                            				if(isexit){
                            					ChatThread ct = new ChatThread(s); 
                            					ct.ps.println(str+"\n"+"IP: "+ip+"\n"+"Port: "+port.toString()+"\n"+"StudentID:17313074");
//                                                ct.ps.println(str);
//                                                ct.ps.println("StudentID:17313074");
//                                                ct.ps.println("IP:"+ip);
//                                                ct.ps.println("Port:"+port);
                            				}else{
                            					for(int a=0;a<((ArrayList<Map<String,Object>>)map.get(i).get("stuList")).size();a++){
//                            						if(!(((ArrayList<Map<String,Object>>)map.get(i).get("stuList")).get(a).get("JOIN_ID").toString()).equals(jonid[0])){
                                						ChatThread ctsa = new ChatThread((Socket)((ArrayList<Map<String,Object>>)map.get(i).get("stuList")).get(a).get("socket"));
                                                        if(str!=null){
                                                        	ctsa.ps.println("CHAT: "+split[1].toString()+"\n"+
                                                        			"CLIENT_NAME: "+clientname[1].toString()+"\n"+
                                                        			"MESSAGE: "+message[1].toString()+"\n"+"\n");
//                                                        	ctsa.ps.println("CHAT: "+split[1]);
//                                                        	ctsa.ps.println("CLIENT_NAME: "+clientname[1]);
//                                                        	ctsa.ps.println("MESSAGE: "+message[1]+"\n\n");
                                                        }
//                                					}
                            					}
                            				}
                            			}
                            		}
                        		}else{
                                    ChatThread ct = new ChatThread(s); 
                                    ct.ps.println(str+"\nIP: "+ip+"\nPort: "+port.toString()+"\nStudentID: 17313074");
//                                    ct.ps.println(str);
//                                    ct.ps.println("IP:"+ip);
//                                    ct.ps.println("Port:"+port);
//                                    ct.ps.println("StudentID:17313074");
                                }
                        	}
                        	else if(str!=null&&str.startsWith("DISCONNECT: ")){
//                        		String[] split = str.split(": ");
                        		String str2=br.readLine();
                        		String str3=br.readLine();
                        		if(null!=str&&str2.startsWith("PORT")&&str3.startsWith("CLIENT_NAME")){
                            		str2.replaceAll(" ", "");
                            		String jonid[]=str2.split(": ");
                            		
                            		str3.replaceAll(" ", "");
                            		String stuName[]=str3.split(": ");
                        			for(int i=0;i<map.size();i++){
                                		for(int a=0;a<((ArrayList<Map<String,Object>>)map.get(i).get("stuList")).size();a++){
                                			if(((ArrayList<Map<String,Object>>)map.get(i).get("stuList")).get(a).get("StudentName").equals(stuName[1])){
                                				((ArrayList<Map<String,Object>>)map.get(i).get("stuList")).remove(a);
                                				for(int b=0;b<((ArrayList<Map<String,Object>>)map.get(i).get("stuList")).size();b++){
                                						ChatThread ctsa = new ChatThread((Socket)((ArrayList<Map<String,Object>>)map.get(i).get("stuList")).get(b).get("socket"));
                                                        if(str!=null){
                                                        	ctsa.ps.println(stuName[0]+" has left this chatroom.");
//                                                        	ctsa.ps.println("CHAT: "+split2[0]);
//                                                        	ctsa.ps.println("CLIENT_NAME: "+clientname[0]);
//                                                        	ctsa.ps.println("MESSAGE: "+message[0]);
                                                        }
                            					}
                            				}
                                		}
                                	}
                        		}else{
                                    ChatThread ct = new ChatThread(s); 
                                    ct.ps.println(str+"\nIP: "+ip+"\nPort: "+port.toString()+"\nStudentID: 17313074");
//                                    ct.ps.println(str);
//                                    ct.ps.println("IP:"+ip);
//                                    ct.ps.println("Port:"+port);
//                                    ct.ps.println("StudentID:17313074");
                                }

                                //��ȡ�û���
//                            	String[] split2 = split[3].trim().split("\\\\n");
                        	}
                        	else if(str!=null&&str.startsWith("JOIN_CHATROOM: ")){
                        		String str2=br.readLine();
                        		String str3=br.readLine();
                        		String str4=br.readLine();
                        		if(null!=str&&str2.startsWith("CLIENT_IP")&&str3.startsWith("PORT")&&str4.startsWith("CLIENT_NAME")){
                            		str.replaceAll(" ", "");
                            		String roomName1[]=str.split(": ");
                            		
                            		str2.replaceAll(" ", "");
                            		String clientIp[]=str2.split(": ");
                            		
                            		str3.replaceAll(" ", "");
                            		String port1[]=str3.split(": ");
                            		
                            		str4.replaceAll(" ", "");
                            		String clientName1[]=str4.split(": ");
                            		
//                                	String[] split = str.split(":");
        //
//                                	String[] split2 = split[1].trim().split("\\\\n");
//                                	//��¼���û���
//                                	String[] clientName = split[4].trim().split("\\\\n");
                                	if(roomName.get(roomName1[1])==null){
                            			Map<String,Object> mp = new HashMap<String,Object>();
                            			mp.put("JOINED_CHATROOM", roomName1[1]);
                            			mp.put("ROOM_REF", map.size()+1);
                            			ArrayList<Map<String,Object>> arr = new ArrayList<Map<String,Object>>();
                            			Map<String,Object> stu = new HashMap<String,Object>();
                            			stu.put("JOIN_ID", arr.size()+1);
                            			stu.put("StudentName", clientName1[1]);
                            			stu.put("socket", s);
                            			roomName.put(roomName1[1].toString(), roomName1[1].toString());
                            			int a= map.size()+1;
                            			int b=arr.size()+1;
                            			arr.add(stu);
                                        mp.put("stuList", arr);
                                        map.add(mp);
                                        System.out.println("return join the chat room start");
                                    	ChatThread ctsab = new ChatThread(s); 
                                    	ctsab.ps.println("JOINED_CHATROOM: "+roomName1[1].toString()+"\n"
                                    			+"SERVER_IP: "+ip+"\n"+"PORT: "
                                    			+port.toString()+"\n"+"ROOM_REF: "
                                    			+String.valueOf(a)+"\n"+"JOIN_ID: "
                                    			+String.valueOf(b)+"\n"
                                    			+"CHAT: "+String.valueOf(map.size())+"\n"
                                    			+"CLIENT_NAME: "+clientName1[1].toString()+"\n"+"MESSAGE: "+clientName1[1].toString()+ " has joined the chat room");
                                    	 System.out.println("return join the chat room end");
//                                    	ChatThread ctsab1 = new ChatThread(s);
//                                    	ctsab1.ps.println("CHAT: "+String.valueOf(map.size()+1)+"\n"+"CLIENT_NAME: "+clientName1[1].toString()+"\n"+"MESSAGE: "+clientName1[1].toString()+ " has joined the chat room");
//                                        ctsab.ps.println("JOINED_CHATROOM: "+roomName1[1]);
//                                        ctsab.ps.println("SERVER_IP: "+ip);
//                                        ctsab.ps.println("PORT: "+port);
//                                        ctsab.ps.println("ROOM_REF: "+a);
//                                        ctsab.ps.println("JOIN_ID: "+b);
                                	}else{
                                		 System.out.println("join the chat room problems aa");
                            			Map<String,Object> stu = new HashMap<String,Object>();
                            			for(int i=0;i<map.size();i++){
                            				if(map.get(i).get("JOINED_CHATROOM").equals(roomName1[1])){
                            					Boolean isexit=true;
                            					for(int a=0;a<((ArrayList<Map<String,Object>>)map.get(i).get("stuList")).size();a++){
                            						//У���û����Ƿ���ȷ������ȷ������Ϣ
                                    				if(!((ArrayList<Map<String,Object>>)map.get(i).get("stuList")).get(a).get("StudentName").equals(clientName1[1])){
                                    					  System.out.println("joinroom");
//                                                        ChatThread ctsa = new ChatThread((Socket)((ArrayList<Map<String,Object>>)map.get(i).get("stuList")).get(a).get("socket"));
////                                                        ctsa.ps.println(clientName1[1].toString()+ " has joined the chat room");
//                                                        ctsa.ps.println("CHAT: "+String.valueOf(map.size()+1)+"\n"+"CLIENT_NAME:  "+clientName1[1].toString()+"\n"+"MESSAGE:  "+clientName1[1].toString()+ " has joined the chat room");
                                    				}else{
                                    					isexit=false;
                                    				}
                                    			}
                            					if(isexit){
                            						stu.put("JOIN_ID", ((ArrayList<Map<String,Object>>)map.get(i).get("stuList")).size()+1);
                                        			stu.put("StudentName", clientName1[1]);
                                        			stu.put("socket", s);
                                					ChatThread ctsab = new ChatThread(s); 
//                                                    ctsab.ps.println("JOINED_CHATROOM: "+roomName1[1]);
//                                                    ctsab.ps.println("SERVER_IP: "+ip);
//                                                    ctsab.ps.println("PORT: "+port);
//                                                    ctsab.ps.println("ROOM_REF: "+(map.get(i).get("ROOM_REF")));
                                                    int b=((ArrayList<Map<String,Object>>)map.get(i).get("stuList")).size()+1;
//                                                    ctsab.ps.println("JOIN_ID: "+b);
                                                    ((ArrayList<Map<String,Object>>)map.get(i).get("stuList")).add(stu);
                                					ctsab.ps.println("JOINED_CHATROOM: "+roomName1[1].toString()+"\n"+
                        							"SERVER_IP: "+ip+"\n"+
                        							"PORT: "+port.toString()+"\n"+
                        							"ROOM_REF: "+(map.get(i).get("ROOM_REF").toString())+"\n"+"JOIN_ID: "+String.valueOf(((ArrayList<Map<String,Object>>)map.get(i).get("stuList")).size())+"\n");
                            					}
                            					System.out.println("join the chat room problems bb");
                            					for(int a=0;a<((ArrayList<Map<String,Object>>)map.get(i).get("stuList")).size();a++){
                            						//У���û����Ƿ���ȷ������ȷ������Ϣ
                                                        ChatThread ctsa = new ChatThread((Socket)((ArrayList<Map<String,Object>>)map.get(i).get("stuList")).get(a).get("socket"));
//                                                        ctsa.ps.println(clientName1[1].toString()+ " has joined the chat room");
                                                        ctsa.ps.println("CHAT: "+String.valueOf(map.size()+1)+"\n"+"CLIENT_NAME:  "+clientName1[1].toString()+"\n"+"MESSAGE:  "+clientName1[1].toString()+ " has joined the chat room");
                                    			}
                            					
                            				}
                            				
                            				
                            			}
                            			
                                	}
                        		}else{
                        			System.out.println("shuchu"+str);
                                    ChatThread ct = new ChatThread(s); 
                                    ct.ps.println(str+"\nIP: "+ip+"\nPort: "+port.toString()+"\nStudentID: 17313074");
//                                    ct.ps.println(str);
//                                    ct.ps.println("IP:"+ip);
//                                    ct.ps.println("Port:"+port);
//                                    ct.ps.println("StudentID:17313074");
                                }

                            }
                         else if(str!=null&&str.startsWith("LEAVE_CHATROOM: ")){
                        	System.out.println("join something"+"aa"+"join things"+"bb");
                        	String str2=br.readLine();
                     		String str3=br.readLine();
                     		System.out.println("output something"+str2+"another things"+str3);
                    		ChatThread ctsav = new ChatThread(s);
                    		ctsav.ps.println("LEFT_CHATROOM: "+ String.valueOf(1)+"\nJOIN_ID: "+ String.valueOf(1));
                     		if(null!=str&&str2.startsWith("JOIN_ID")&&str3.startsWith("CLIENT_NAME")){
                         		str.replaceAll(" ", "");
                        		String roomref[]=str.split(": ");
                         		
                         		str2.replaceAll(" ", "");
                        		String joinid[]=str2.split(": ");
                        		
                        		str3.replaceAll(" ", "");
                        		String clientName[]=str3.split(": ");
                        		ChatThread ctsab = new ChatThread(s);
                        		ctsab.ps.println("LEFT_CHATROOM: "+ roomref[1].toString()+"\nJOIN_ID: "+ joinid[1].toString());
                     			for(int i=0;i<map.size();i++){
                            		if(map.get(i).get("ROOM_REF").toString().equals(roomref[1])){
                            			for(int a=0;a<((ArrayList<Map<String,Object>>)map.get(i).get("stuList")).size();a++){
                            				if((((ArrayList<Map<String,Object>>)map.get(i).get("stuList")).get(a).get("JOIN_ID").toString()).equals(joinid[1])){
                            					ChatThread ctsa = new ChatThread((Socket)((ArrayList<Map<String,Object>>)map.get(i).get("stuList")).get(a).get("socket"));
//                                				ctsa.ps.println("LEFT_CHATROOM: "+ roomref[1]);
//                            					ctsa.ps.println("JOIN_ID: "+ joinid[1]);
                            					ctsa.ps.println("LEFT_CHATROOM: "+ roomref[1].toString()+"\nJOIN_ID: "+ joinid[1].toString());
                            					((ArrayList<Map<String,Object>>)map.get(i).get("stuList")).remove(a);
                            				}else{
                            					ChatThread ctsa = new ChatThread((Socket)((ArrayList<Map<String,Object>>)map.get(i).get("stuList")).get(a).get("socket"));
//                                				ctsa.ps.println("LEFT_CHATROOM: "+ roomref[1]);
//                            					ctsa.ps.println("JOIN_ID: "+ joinid[1]);
                            					ctsa.ps.println("LEFT_CHATROOM: "+ roomref[1].toString()+"\nJOIN_ID: "+ joinid[1].toString());
                            				}
                            			}
                            			for(int a=0;a<((ArrayList<Map<String,Object>>)map.get(i).get("stuList")).size();a++){
                            				ChatThread ctsa = new ChatThread((Socket)((ArrayList<Map<String,Object>>)map.get(i).get("stuList")).get(a).get("socket"));
                            				ctsa.ps.println(clientName[1].toString()+" has left this chatroom.");
                            			}
                            			
                            		}
                            	}
                     		}else{
                                ChatThread ct = new ChatThread(s); 
                                ct.ps.println(str+"\nIP: "+ip+"\nPort: "+port.toString()+"\nStudentID: 17313074");
//                                ct.ps.println(str);
//                                ct.ps.println("IP:"+ip);
//                                ct.ps.println("Port:"+port);
//                                ct.ps.println("StudentID:17313074");
                            }

//                        	String[] split = str.split(":");
//                         	String[] split2 = split[1].trim().split("\\\\n");
//                         	String[] split3 = split[2].trim().split("\\\\n");
//                         	//ѧ������
//                         	String[] stuName = split[3].trim().split("\\\\n");

                          }
                        }else{
                            ChatThread ct = new ChatThread(s); 
                            ct.ps.println(str+"\nIP: "+ip+"\nPort: "+port.toString()+"\nStudentID: 17313074");
                            System.out.println(str+"\nIP: "+ip+"\nPort: "+port.toString()+"\nStudentID: 17313074");
//                            ct.ps.println(str);
//                            ct.ps.println("IP:"+ip);
//                            ct.ps.println("Port:"+port);
//                            ct.ps.println("StudentID:17313074");
                        }
                    }else{
                    	ChatThread ct = new ChatThread(s); 
                        ct.ps.println(str+"\nIP: "+ip+"\nPort: "+port.toString()+"\nStudentID: 17313074");
                        System.out.println(str+"\nIP: "+ip+"\nPort: "+port.toString()+"\nStudentID: 17313074");
//                       ct.ps.println(str);
//                        ct.ps.println("IP:"+ip);
//                        ct.ps.println("Port:"+port);
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
