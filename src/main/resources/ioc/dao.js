var ioc = {
		dao : {
	        type : "org.nutz.dao.impl.NutDao",
	        args : [{refer:"dataSource"}]
	    },
		dataSource : {
	        type : "com.alibaba.druid.pool.DruidDataSource",
	        events : {
	            depose : 'close'
	        },
	        fields : {
//	        	url : "jdbc:sqlite:D://aiot.db",
//			 	url : {java : 'main.Constants.SQLitePath'},
				url : {java : 'org.aiot.main.MainSetup.daoPath'},
	            maxWait: 1000,
	            defaultAutoCommit : false,
	            validationQuery : "SELECT 1"
	        }
	    },
	    conf : {
			type : "org.nutz.ioc.impl.PropertiesProxy",
			fields : {
				paths : ["custom/"]
			}
		}
}