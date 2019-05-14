package com.iwhere.gridgeneration.comm.enums;

/**
 * 状态响应码
 * @author wenbronk
 * @time 2017年4月14日  下午2:19:25  2017
 */
public enum PublicRespEnum {

	RESCODE_200("200", "sucess"),
	RESCODE_201("201", "task create"),
	RESCODE_300("300", "缺东西??"),
	RESOCDE_306("306", "程序正常, 但没有相应数据"),
	RESCODE_400("400", "unsupport param"),
	RESCODE_403("403", "authority forbidden"),
    RESCODE_500("500", "服务器挂了, 我也不知道为什么..."),
	RESCODE_503("503", "overload"),
	RESCODE_504("504", "socketTimeOut"),
	RESCODE_1001("1001", "no status"),
	RESCODE_5102("5102", "no source");
	
	private String server_status;
	private String info;
	
	private PublicRespEnum(String server_status, String info) {
		this.server_status = server_status;
		this.info = info;
	}

	public String getServer_status() {
		return server_status;
	}

	public void setServer_status(String server_status) {
		this.server_status = server_status;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}
}