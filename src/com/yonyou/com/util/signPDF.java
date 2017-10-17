package com.yonyou.com.util;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.Certificate;

import javax.crypto.Cipher;

import org.bouncycastle.jce.provider.BouncyCastleProvider;




import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfSignatureAppearance.RenderingMode;
import com.itextpdf.text.pdf.PdfStamper;

public class signPDF {
	/**
	 * 
	 * @description
	 * @author machunlin
	 * @date 2012-11-5
	 * @param srcFile
	 *            :ԭ�ĵ�
	 * @param signedFile
	 *            :���ܺ���ĵ�
	 * @return void
	 * @throws IOException
	 * @throws DocumentException
	 */
	public static boolean signDoc(String srcFile,String cert_path,String cert_pwd)
			throws DocumentException, IOException {
		// ���providers (��%JAVA_HOME%/jre/lib/security/java.security
		// �п����ҵ�sun�ṩ��providers )
		// Security.addProvider(new BouncyCastleProvider());
		KeyStoreFactory ksFactory = KeyStoreFactory.getInstance();
		PdfReader reader = null;
		FileOutputStream fout = null;
		PdfStamper stp = null;
		PdfSignatureAppearance sap = null;
		ByteArrayOutputStream encryptByteOut = new ByteArrayOutputStream();
		try {
			ksFactory.initKeyStore(cert_path, cert_pwd);

			reader = new PdfReader(srcFile);
			

//			stp = PdfStamper.createSignature(reader, fout, '\0');
			
//			stp = PdfStamper.createSignature(reader, fout, '\0', new
//					 File("C:\\temp"), true);
			stp = PdfStamper.createSignature(reader, encryptByteOut, '\0');

			sap = stp.getSignatureAppearance();

			sap.setCrypto(ksFactory.getPrivateKey(cert_pwd), ksFactory
					.getCertificateChain(), null,
					PdfSignatureAppearance.WINCER_SIGNED);
		} catch (FileNotFoundException e) {
			throw new SignatureException(SignatureException.RCE_INVALID, e
					.getMessage());
		} catch (DocumentException e) {
			throw new SignatureException(SignatureException.RCE_INVALID, e
					.getMessage());
		} catch (IOException e) {
			throw new SignatureException(SignatureException.RCE_INVALID, e
					.getMessage());
		} 
		catch (KeyStoreException e) {
			throw new SignatureException(SignatureException.RCE_INVALID, e
					.getMessage());
		}
		String imgpath=cert_path.substring(0,cert_path.indexOf("pfx"));
		 Image image = Image.getInstance(imgpath+"img\\sign.png"); //ʹ��png��ʽ͸��ͼƬ  
		 sap.setSignatureGraphic(image);  
		 sap.setAcro6Layers(true);  
	     sap.setRenderingMode(RenderingMode.DESCRIPTION);  

		sap.setReason("I'm the author");
		sap.setLocation("Lisbon");
		sap.setCertificationLevel(PdfSignatureAppearance.CERTIFIED_FORM_FILLING_AND_ANNOTATIONS);
		sap.setVisibleSignature(new Rectangle(100, 100, 200, 200), 1, null);
		reader.close();
		stp.close();
		fout = new FileOutputStream(srcFile);
		 fout.write(encryptByteOut.toByteArray());
		 fout.close();
		 return true;
	}

	/**
	 * ֤����ܽ��ܣ����ܶ��ļ����ܣ����ֻ�ܶ�һС���ַ����ܽ��ܣ�
	 * 
	 * @param certPath
	 *            ֤��·��
	 * @param password
	 *            ֤������
	 */
	private static void encrypAndDecryption(String certPath,
			String certPassWord, String encryptData) {
		// �����ʾ��Կ��֤��Ĵ洢��ʩ
		KeyStore keyStore;
		String alias = "";
		// String testEncrypt = "certificate encrypt decryption";
		System.out.println("����ǰ: " + encryptData);
		try {
			FileInputStream is = new FileInputStream(certPath);
			// �õ�KeyStoreʵ��
			keyStore = KeyStore.getInstance("PKCS12");
			// ��ָ�����������м��ش� KeyStore��
			keyStore.load(is, certPassWord.toCharArray());
			is.close();
			// ��ȡkeyStore����
			alias = (String) keyStore.aliases().nextElement();
			Certificate cert = keyStore.getCertificate(alias);
			// ���ݸ���������ȡ��ص�˽Կ
			PrivateKey priKey = (PrivateKey) keyStore.getKey(alias,
					certPassWord.toCharArray());
			// ��ȡ֤��Ĺ�Կ
			PublicKey pubKey = cert.getPublicKey();
			// ��ȡCipher��ʵ�� getInstance(�㷨/ģʽ/���)��getInstance("�㷨")
			Cipher cipher = Cipher.getInstance("RSA");// Cipher.getInstance(
			// "RSA/ECB/PKCS1Padding"
			// )
			// ��Կ��ʼ��Cipher ��Կ����
			cipher.init(Cipher.ENCRYPT_MODE, pubKey);
			// ����
			byte[] encodeEncryp = cipher.doFinal(encryptData.getBytes());
			System.out.println("���ܺ�: " + new String(encodeEncryp));
			// encodeEncryp = cipher.doFinal(getFileContentToByte(encrypFile));
			// ˽Կ��ʼ��Cipher ˽Կ����
			cipher.init(Cipher.DECRYPT_MODE, priKey);
			// ����
			byte[] encodeDecryption = cipher.doFinal(encodeEncryp);

			String content = new String(encodeDecryption);
			System.out.println("���ܺ�: " + content + "length: "
					+ content.length());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
