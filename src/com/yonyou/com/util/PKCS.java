package com.yonyou.com.util;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.x509.X509V3CertificateGenerator;

import javax.security.auth.x500.X500Principal;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;

public class PKCS {
	// ϵͳ���BC�����㷨 �Ժ�ϵͳ�е��õ��㷨����BC���㷨
	static {

		Security.addProvider(new BouncyCastleProvider());
	}

	public static boolean ExecPfx(String certPath)
			throws NoSuchAlgorithmException, InvalidKeyException,
			SecurityException, SignatureException, KeyStoreException,
			CertificateException, IOException {

		// certPath = "d:/jason.pfx";
		try {
			// ����KeyStore
			KeyStore store = KeyStore.getInstance("PKCS12");
			store.load(null, null);

			/* RSA�㷨������Կ��˽Կ */
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
			kpg.initialize(2048);
			KeyPair keyPair = kpg.generateKeyPair();
			// ��װ֤��
			// CN=(����������), OU=(��֯��λ����), O=(��֯����),L=(���л���������),ST=(�ݻ�ʡ������), C=(��λ������ĸ���Ҵ���)
			String issuer = "C=CHINA,ST=����,L=����,O=yonyou,OU=yonyou,CN=rlk";
			String subject = issuer;
			
			X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();

			certGen.setSerialNumber(BigInteger.valueOf(System
					.currentTimeMillis()));
			certGen.setIssuerDN(new X500Principal(issuer));
			certGen.setNotBefore(new Date(System.currentTimeMillis() - 50000));
			certGen.setNotAfter(new Date(System.currentTimeMillis() + 500000));
			certGen.setSubjectDN(new X500Principal(subject));
			certGen.setPublicKey(keyPair.getPublic());
			certGen.setSignatureAlgorithm("SHA256WithRSAEncryption");

			X509Certificate cert = certGen.generateX509Certificate(keyPair
					.getPrivate());
			// System.out.print(keyPair.getPrivate().toString());
			System.out.println(cert.toString());
			// System.out.println(keyPair.getPrivate());
			// store.setCertificateEntry(alias, cert);

			store.setKeyEntry("atlas", keyPair.getPrivate(),
					"111111".toCharArray(), new Certificate[] { cert });

			FileOutputStream fout = new FileOutputStream(certPath);
			store.store(fout, "111111".toCharArray());
			fout.close();
			return true;
		} catch (Exception ex) {
			return false;
		}
	}
}
