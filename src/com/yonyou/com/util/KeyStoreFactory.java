package com.yonyou.com.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import org.apache.log4j.Logger;


public class KeyStoreFactory {
	public static final Logger log = Logger.getLogger(KeyStoreFactory.class);
	private static KeyStoreFactory instance = null;
	private static KeyStore ks = null;

	public static synchronized KeyStoreFactory getInstance() {
		if (instance == null) {
			instance = new KeyStoreFactory();
		}
		return instance;
	}

	public KeyStore initKeyStore(String certFilePath, String password)
			throws IOException {

		FileInputStream certFis = null;
		try {
			certFis = new FileInputStream(certFilePath);
			ks = KeyStore.getInstance("PKCS12");

			char[] nPassword = null;
			if ((password == null) || password.trim().equals("")) {
				nPassword = null;
			} else {
				nPassword = password.toCharArray();
			}

			ks.load(certFis, nPassword);

		} catch (Exception e) {
			e.printStackTrace();
			throw new SignatureException(SignatureException.RCE_CERT,
					"初始化keyStore失败" + e.getMessage());
		} finally {
			certFis.close();
		}
		return ks;
	}

	/**
	 * 获取用户.p12证书中的私钥
	 * 
	 * @param ks
	 *            KeyStore对象
	 * @param password
	 *            密码
	 * @return 证书中的私钥
	 * 
	 *
	 *t
	 *t
	 */
	public PrivateKey getPrivateKey(String password) {
		validateKeyStore();
		PrivateKey prikey = null;
		try {
			Enumeration<String> enum2 = ks.aliases();
			String keyAlias = null;
			if (enum2.hasMoreElements()) // 只读�?��证书
			{
				keyAlias = (String) enum2.nextElement();
			}

			prikey = (PrivateKey) ks.getKey(keyAlias, password.toCharArray());

		} catch (Exception e) {
			throw new SignatureException(SignatureException.RCE_CERT, "获取私钥失败"
					+ e.getMessage());
		}
		return prikey;
	}

	/**
	 * 获取用户.p12证书中的公钥
	 * 
	 * @param ks
	 *            KeyStore对象
	 * @return 证书中的公钥
	 */
	public PublicKey getPublicKey() {
		validateKeyStore();
		PublicKey publickey = null;
		try {
			Enumeration<String> enum2 = ks.aliases();
			String keyAlias = null;
			if (enum2.hasMoreElements()) // 只读�?��证书
			{
				keyAlias = (String) enum2.nextElement();
			}
			Certificate cert = ks.getCertificate(keyAlias);
			publickey = cert.getPublicKey();
		} catch (Exception e) {
			throw new SignatureException(SignatureException.RCE_CERT, "获取公钥失败:"
					+ e.getMessage());

		}
		return publickey;
	}

	/**
	 * 获取p12证书中的X509证书对象
	 * 
	 * @param ks
	 *            KeyStore对象
	 * @return X509证书对象
	 */
	public X509Certificate getSignerX509Cert() {
		validateKeyStore();
		X509Certificate x509certificateOfP12 = null;
		try {
			Enumeration<String> enum2 = ks.aliases();
			String keyAlias = null;
			if (enum2.hasMoreElements()) // 只读�?��证书
			{
				keyAlias = (String) enum2.nextElement();
			}
			x509certificateOfP12 = (X509Certificate) ks
					.getCertificate(keyAlias);

		} catch (Exception e) {
			throw new SignatureException(SignatureException.RCE_CERT, "获取证书失败:"
					+ e.getMessage());
		}
		return x509certificateOfP12;
	}

	public String getKeyAlias() {
		validateKeyStore();
		String keyAlias = null;
		try {
			Enumeration<String> enum2 = ks.aliases();

			if (enum2.hasMoreElements()) // 只读一个证书
			{
				keyAlias = (String) enum2.nextElement();
			}

		} catch (Exception e) {
			throw new SignatureException(SignatureException.RCE_CERT,
					"获取keyAlias失败:" + e.getMessage());
		}
		return keyAlias;
	}

	public Certificate[] getCertificateChain() throws KeyStoreException {
		return ks.getCertificateChain(getKeyAlias());
	}

	private void validateKeyStore() {
		if (ks == null) {
			throw new SignatureException(SignatureException.RCE_INVALID,
					"keyStore没有被初始化");
		}
	}
	
	public static void main(String[] args){
		
		KeyStoreFactory privkey=new KeyStoreFactory();
		try {
			privkey.initKeyStore("D:/test/PDF-SIGN/abd1.cer","88888888");
			privkey.getPrivateKey("88888888");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
