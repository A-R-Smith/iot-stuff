package com.arsmith.iot.broker;

	import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.SocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MqttSecurePublisher extends MqttPublisher {
	private final static String KEYSTORE_PATH = "/home/alex/keystore.p12";
	private final static String KEYSTORE_PASS = "Nationwide2016";

	public MqttSecurePublisher(String brokerUrl, String clientId, String infoTopic, MessageQueue queue) {
		super(brokerUrl, clientId, infoTopic, queue);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void connectToBroker() {
        try {
            mqttClient = new MqttClient(brokerUrl, clientId, new MemoryPersistence());
            
            MqttConnectOptions conOptions = new MqttConnectOptions();
            conOptions.setSocketFactory(getSocketFactory());
            conOptions.setCleanSession(CLEAN);
            conOptions.setKeepAliveInterval(KEEP_ALIVE);

            mqttClient.connect(conOptions);
               
        } catch (MqttException e) {
        	logger.error("MqttException in MqttSecurePublisher::run connectToBroker ", e);
        }
	}
	
	private SocketFactory getSocketFactory() {
		SSLContext sslContext =null;
		
		try {
			sslContext = SSLContext.getInstance("SSL");
//			TrustManagerFactory trustManagerFactory = TrustManagerFactory
//					.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			KeyStore keyStore = readKeyStore();
//			trustManagerFactory.init(keyStore);
			//sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());
			KeyManagerFactory kmfactory = KeyManagerFactory.getInstance(
		            KeyManagerFactory.getDefaultAlgorithm());
			kmfactory.init(keyStore,"Nationwide2016".toCharArray());
			sslContext.init(null, certs, new SecureRandom());
		} catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
			logger.error("Problem creating SSL Socket Factory: stacktrace:",e);
		} catch (UnrecoverableKeyException e) {
			// TODO Auto-generated catch block
			logger.error("Problem creating KeyManagerFactory, cannot read keystore. stacktrace:",e);
		}

		
		return sslContext.getSocketFactory();
	}
	
	private KeyStore readKeyStore() {
		KeyStore keystore = null;
		try {
			FileInputStream is = new FileInputStream(KEYSTORE_PATH);
			keystore = KeyStore.getInstance(KeyStore.getDefaultType());
			keystore.load(is, KEYSTORE_PASS.toCharArray());
		} catch (FileNotFoundException e) {
			logger.error("Keystore not found, stacktrace:",e);
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
			logger.error("Problem loading keystore, stacktrace:",e);
		} 
		
		return keystore;
	}
	
    static TrustManager[] certs = new TrustManager[]{
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType)
                        throws CertificateException {
                }

                public void checkClientTrusted(X509Certificate[] chain, String authType)
                        throws CertificateException {
                }
            }
    };
	
}
