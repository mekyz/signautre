package com.yonyou.com;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.yonyou.com.util.PKCS;
import com.yonyou.com.util.signPDF;

public class UpLoadFilePdf extends HttpServlet {
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;utf-8");
		response.setCharacterEncoding("utf-8");
		   PrintWriter outPut=response.getWriter();
        //�õ��ϴ��ļ��ı���Ŀ¼�����ϴ����ļ������WEB-INFĿ¼�£����������ֱ�ӷ��ʣ���֤�ϴ��ļ��İ�ȫ
         String savePath = this.getServletContext().getRealPath("/WEB-INF/upload");
       //  String savePath1=this.getServletContext().getRealPath("/WEB-INF/upload");
         //��������֤��·��
         String pfxPath = this.getServletContext().getRealPath("/WEB-INF/pfx");
          File file = new File(savePath);
                 //�ж��ϴ��ļ��ı���Ŀ¼�Ƿ����
                 if (!file.exists() && !file.isDirectory()) {
                     System.out.println(savePath+"Ŀ¼�����ڣ���Ҫ����");
                    //����Ŀ¼
                     file.mkdir();
                 }
                 file=new File(pfxPath);
                 //�ж��ϴ��ļ��ı���Ŀ¼�Ƿ����
                 if (!file.exists() && !file.isDirectory()) {
                     System.out.println(savePath+"Ŀ¼�����ڣ���Ҫ����");
                    //����Ŀ¼
                     file.mkdir();
                 }
                 //��Ϣ��ʾ
                 String message = "";
                 try{       
             		//ʹ�ý���������
                	 //ʹ��Apache�ļ��ϴ���������ļ��ϴ����裺
                     //1������һ��DiskFileItemFactory����
                     DiskFileItemFactory factory = new DiskFileItemFactory();
                    //2������һ���ļ��ϴ�������
                     ServletFileUpload upload = new ServletFileUpload(factory);
                     //����ϴ��ļ�������������
                    // upload.setHeaderEncoding("UTF-8"); 
                     //3���ж��ύ�����������Ƿ����ϴ���������
//                    if(!ServletFileUpload.isMultipartContent(request)){
//                         //���մ�ͳ��ʽ��ȡ����
//                         return;
//                     }
                    //4��ʹ��ServletFileUpload�����������ϴ����ݣ�����������ص���һ��List<FileItem>���ϣ�ÿһ��FileItem��Ӧһ��Form����������
                     List<FileItem> list = upload.parseRequest(request);
                     for(FileItem item : list){
                        //���fileitem�з�װ������ͨ�����������
                         if(item.isFormField()){
                            String name = item.getFieldName();
                             //�����ͨ����������ݵ�������������
                             String value = item.getString("UTF-8");
                            //value = new String(value.getBytes("iso8859-1"),"UTF-8");
                             System.out.println(name + "=" + value);
                         }else{//���fileitem�з�װ�����ϴ��ļ�
                             //�õ��ϴ����ļ����ƣ�
                             String filename = item.getName();                       
                             if(filename==null || filename.trim().equals("")){
                                 continue;
                             }
                             //��ȡ�ļ�����
                             String fileType= item.getContentType();
                             if(!"pdf".equals(fileType.substring(fileType.lastIndexOf("/")+1))){
                            	 outPut.print("���ϴ����ļ����Ͳ���ȷ�����ϴ�pdf��ʽ�ļ�!");
                            	 break;
                             }
                            //ע�⣺��ͬ��������ύ���ļ����ǲ�һ���ģ���Щ������ύ�������ļ����Ǵ���·���ģ��磺  c:\a\b\1.txt������Щֻ�ǵ������ļ������磺1.txt
                            //�����ȡ�����ϴ��ļ����ļ�����·�����֣�ֻ�����ļ�������
                             filename = filename.substring(filename.lastIndexOf("\\")+1);
                            //��ȡitem�е��ϴ��ļ���������
                            InputStream in = item.getInputStream();
                             //����һ���ļ������
                             FileOutputStream out = new FileOutputStream(savePath + "\\" + filename);
                             savePath=savePath + "\\" + filename;
                             //����һ��������
                             byte buffer[] = new byte[1024];
                            //�ж��������е������Ƿ��Ѿ�����ı�ʶ
                            int len = 0;
                            //ѭ�������������뵽���������У�(len=in.read(buffer))>0�ͱ�ʾin���滹������
                             while((len=in.read(buffer))>0){
                                //ʹ��FileOutputStream�������������������д�뵽ָ����Ŀ¼(savePath + "\\" + filename)����
                                out.write(buffer, 0, len);
                           }
                            //�ر�������
                             in.close();
                            //�ر������
                            out.close();
                            //ɾ�������ļ��ϴ�ʱ���ɵ���ʱ�ļ�
                             item.delete(); 
                         }
                         //����֤���ļ�
                         pfxPath=pfxPath+"\\demo.pfx";
                         boolean bool=PKCS.ExecPfx(pfxPath);
                         //pdf�ļ���ӵ��ӱ�ǩ
                     	boolean boolpdf=false;
                         if(bool){
                        	 boolpdf=signPDF.signDoc(savePath, pfxPath, "111111"); 
                         }
                         if(boolpdf){
                        	 outPut.print( "�ļ��ϴ��ɹ���");
                         }else{
                        	 outPut.print("�ļ��ϴ�ʧ��");
                         }
                     }
                 }catch (Exception e) {
                    message= "�ļ��ϴ�ʧ�ܣ�";
                     e.printStackTrace();   
                }
	}
}
