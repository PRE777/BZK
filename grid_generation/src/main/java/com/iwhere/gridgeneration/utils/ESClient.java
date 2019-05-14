package com.iwhere.gridgeneration.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import com.iwhere.gridgeneration.config.properties.ESProperties;


@SuppressWarnings("all")
public class ESClient {
	
	private static Client client ;
	
	public static void createInstance() {
		// 获取读取xml的对象
		// 集群IP
		String esClusterIp = ESProperties.getIp();
		// 集群端口号
		String esClusterPort = ESProperties.getPort();
		// 设置client.transport.sniff为true来使客户端去嗅探整个集群的状态,把集群中其它机器的ip地址加到客户端中
	    // 设置ES连接
		TransportAddress address;
		try {
			/*address = new InetSocketTransportAddress(
			        InetAddress.getByName(esClusterIp), Integer.valueOf(esClusterPort));*/
			Settings settings = Settings.builder()
	                .put("client.transport.sniff", false)
	                .put("cluster.name", ESProperties.getName())
	                .build();
	       /* client = new PreBuiltTransportClient(settings).addTransportAddress(address);*/
			client = new PreBuiltTransportClient(settings)
			        .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(esClusterIp), Integer.valueOf(esClusterPort)));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	}

	public static Client getInstance(){
		if (client == null){
			synchronized(ESClient.class){		
				if (client == null){
					createInstance();
				}
			}
		}
		return client;
	}
}
