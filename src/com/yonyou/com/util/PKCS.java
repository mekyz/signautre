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
	// 系统添加BC加密算法 以后系统中调用的算法都是BC的算法
	static {

		Security.addProvider(new BouncyCastleProvider());
	}

	public static boolean ExecPfx(String certPath)
			throws NoSuchAlgorithmException, InvalidKeyException,
			SecurityException, SignatureException, KeyStoreException,
			CertificateException, IOException {

		// certPath = "d:/jason.pfx";
		try {
			// 创建KeyStore
			KeyStore store = KeyStore.getInstance("PKCS12");
			store.load(null, null);

			/* RSA算法产生公钥和私钥 */
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
			kpg.initialize(2048);
			KeyPair keyPair = kpg.generateKeyPair();
			// 组装证书
			// CN=(名字与姓氏), OU=(组织单位名称), O=(组织名称),L=(城市或区域名称),ST=(州或省份名称), C=(单位的两字母国家代码)
			String issuer = "C=CHINA,ST=北京,L=北京,O=yonyou,OU=yonyou,CN=rlk";
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
