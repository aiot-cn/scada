/**
 * ==================== 系统 变量、方法、对象 此代码作用域全局=======================
 * Packages 全局变量可用于访问Java包
 * 其中 java、javax、org、edu、com、net 等可以简写
 */

//var lang = new JavaImporter(org.nutz.lang,Packages.model.table);
var aiot = org.aiot;
var IOC = aiot.main.Constants.ioc;

var BD  = Java.type("org.aiot.device.BaseDevice");

var BS  = IOC.get(aiot.service.BaseService.class);
var CS  = IOC.get(aiot.service.ConfigService.class);
var DS  = IOC.get(aiot.service.DeviceService.class);
var PS  = IOC.get(aiot.service.PointService.class);

var MODEL = aiot.model.table;
var DICT  = aiot.model.enums.DictTypeEnum;
var PHASE = aiot.model.enums.PhaseEnum;
var CACHE = aiot.common.Cache;

var dev = DS.getDeviceMap();

function helloScript(){
    print("[JavaScript ] baseJava.js 加载成功",__FILE__, __LINE__, __DIR__);
}
//线程
new java.lang.Thread(helloScript).start();

// ====================   数据库   ====================
function _dao_(){
    //查询
    var test = BS.query(MODEL.Test.class,1);
    var testList = BS.query(MODEL.Test.class,{name_like:"%张%"});
    testList = BS.query("test",{name_like:"%张%"});
    testList = BS.queryCode("getTest",{name:"%张%"});
    testList = BS.querySql("select * from test",{name_like:"%张%"});
    //保存
    BS.daoSave(test);
    BS.daoSave("test",{id:1,name:"张三2"});
    //删除
    BS.daoDel(test);
    BS.daoDel(MODEL.Test.class,1);
    BS.daoDel("test",{score_lt:50});
    //其他数据源实例
    var mysql = BS.dao("mysql");
}

// ==================== 持久化变量 ====================
function _dict_(){
    DICT.args.val("test");
    DICT.args.set("test","v1")
}

/**
 * ====================   缓存    ====================
 * 支持二级、默认值；键值均可为对象；60分钟无访问回收
 */

function _cache_(){
    CACHE.get("test");
    CACHE.getOrDefault("test","v0");
    CACHE.put("test","60min");
    CACHE.set("test","permanent");
}
