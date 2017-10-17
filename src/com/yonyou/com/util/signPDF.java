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
	 *            :原文档
	 * @param signedFile
	 *            :加密后的文档
	 * @return void
	 * @throws IOException
	 * @throws DocumentException
	 */
	public static boolean signDoc(String srcFile,String cert_path,String cert_pwd)
			throws DocumentException, IOException {
		// 添加providers (在%JAVA_HOME%/jre/lib/security/java.security
		// 中可以找到sun提供的providers )
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
		 Image image = Image.getInstance(imgpath+"img\\sign.png"); //使用png格式透明图片  
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
	 * 证书加密解密（不能对文件加密，这个只能对一小段字符加密解密）
	 * 
	 * @param certPath
	 *            证书路径
	 * @param password
	 *            证书密码
	 */
	private static void encrypAndDecryption(String certPath,
			String certPassWord, String encryptData) {
		// 此类表示密钥和证书的存储设施
		KeyStore keyStore;
		String alias = "";
		// String testEncrypt = "certificate encrypt decryption";
		System.out.println("加密前: " + encryptData);
		try {
			FileInputStream is = new FileInputStream(certPath);
			// 得到KeyStore实例
			keyStore = KeyStore.getInstance("PKCS12");
			// 从指定的输入流中加载此 KeyStore。
			keyStore.load(is, certPassWord.toCharArray());
			is.close();
			// 获取keyStore别名
			alias = (String) keyStore.aliases().nextElement();
			Certificate cert = keyStore.getCertificate(alias);
			// 根据给定别名获取相关的私钥
			PrivateKey priKey = (PrivateKey) keyStore.getKey(alias,
					certPassWord.toCharArray());
			// 获取证书的公钥
			PublicKey pubKey = cert.getPublicKey();
			// 获取Cipher的实例 getInstance(算法/模式/填充)或getInstance("算法")
			Cipher cipher = Cipher.getInstance("RSA");// Cipher.getInstance(
			// "RSA/ECB/PKCS1Padding"
			// )
			// 公钥初始化Cipher 公钥加密
			cipher.init(Cipher.ENCRYPT_MODE, pubKey);
			// 加密
			byte[] encodeEncryp = cipher.doFinal(encryptData.getBytes());
			System.out.println("加密后: " + new String(encodeEncryp));
			// encodeEncryp = cipher.doFinal(getFileContentToByte(encrypFile));
			// 私钥初始化Cipher 私钥解密
			cipher.init(Cipher.DECRYPT_MODE, priKey);
			// 解密
			byte[] encodeDecryption = cipher.doFinal(encodeEncryp);

			String content = new String(encodeDecryption);
			System.out.println("解密后: " + content + "length: "
					+ content.length());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
