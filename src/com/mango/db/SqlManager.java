package com.mango.db;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;


/**
 * @author 陈贵豪
 * @since 2017-12-10
 * @version 1.0
 */
//单例模式
public class SqlManager {
	private String dbName;
	private String loginName;
	private String password;
	private String url;
	//此处要设定编码类型为utf-8，否则无法完成中文的模糊查询
	//useSSL是为了消除未指明是否使用SSL登录的警告 测试通过：2017-11-17
	public Connection connection = null;
	private Statement statement = null;
	
	private static volatile SqlManager instance = null;

	private SqlManager() {
		
		Properties prop = new Properties();
		InputStream is = SqlManager.class.getResourceAsStream("config.properties");
		try {
			prop.load(is);
			is.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		url = prop.getProperty("url").trim();
		loginName = prop.getProperty("loginName").trim();
		password = prop.getProperty("password").trim();
		dbName = prop.getProperty("dbName");
		
		
		//1.注册驱动
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//2.新建链接
		try {
			this.connection = (Connection) DriverManager.getConnection(url, loginName, password);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//3.在链接的基础之上新建statement
		try {
			statement = (Statement) connection.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static SqlManager getInstance(){
		synchronized(SqlManager.class){
			if (instance == null){
				instance = new SqlManager();
			}
		}
		return instance;
	}
	
	public ResultSet executeSqlQuery(String sql) throws SQLException{
		ResultSet resultSet = null;
		try {
			resultSet = this.statement.executeQuery(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resultSet;
	}
	
	public int executesqlUpdate(String sql) throws SQLException{
		return this.statement.executeUpdate(sql);
		
	}
	
	public boolean closeAllConnection() {
		try {
			this.statement.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		try {
			this.connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	
	/** 
     * 根据路径生成备份数据库的Shell字符串 
     * @param targetName 要备份的对象名：只能为表名和数据库名称 
     * @return 实际执行的shell命令 
     */  
    public String getBackupShellString(String targetName){  
        String basepath="/usr/bin/" ;
//        String basepath=Thread.currentThread().getContextClassLoader().getResource("").toString();  
        String backFile = "";  
//        String database_tools_path=basepath.substring(6, basepath.length()-4)+"dbtools/";//备份工具路径  
        if(targetName.equals(this.dbName)){//若要备份整个数据库  
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");  
            backFile = "/var/lib/mysql/backup/"+targetName+"_"+sdf.format(new Date())+".sql";//要备份的文件  
            targetName = "";  
        }else{  
            backFile = "/var/lib/mysql/backup/"+targetName+".sql";  
        }  
        String OSType = System.getProperty("os.name");  
        String shellStr = "";  
        if(OSType.indexOf("Windows")!=-1){  
            shellStr = basepath+"mysqldumpwin.exe -hlocalhost -P3306 -u"+this.loginName+" -p"+  
            this.password+" --result-file="+backFile+" --default-character-set=utf8 "+this.dbName+" "+targetName;  
        }else{  
            shellStr = basepath+"mysqldump -hlocalhost -P3306 -u"+this.loginName+" -p"+  
            this.password+" --result-file="+backFile+" --default-character-set=utf8 "+this.dbName+" "+targetName;  
        }  
        System.out.print("##############"+shellStr);  
        return shellStr;  
    }  
    /** 
     * 备份数据库 
     * @param targetName 要备份的对象名：只能为表名和数据库名称 
     * @return 成功:TRUE 失败:FALSE 
     * 备份表直接备份在指定文件夹，备份库则按日期备份到指定的文件夹 
     * 
     */  
    public boolean backup(String targetName){  
          
        String backFilePath = "";  
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");  
        String backDirString = "/var/lib/mysql/backup/";//默认备份库  
        try {  
            if(!targetName.equals(this.dbName)){//备份表  
                File tableDir = new File("/var/lib/mysql/backup/tables/");  
                if(!tableDir.exists()){//存放表的文件夹不存在  
                    tableDir.mkdir();  
                    System.out.println("--------->"+tableDir);  
                }  
                backFilePath ="/var/lib/mysql/backup/"+targetName+".sql";//要备份的文件  
              
            }else {//备份库  
                backFilePath ="/var/lib/mysql/backup/tables/"+targetName+"_"+sdf.format(new Date())+".sql";//要备份的文件  
                File backDir = new File(backDirString);  
                if(!backDir.exists()){//存放库的文件夹不存在  
                    backDir.mkdir();  
                }  
            }  
            //判断要备份的文件是否已存在  
            File backFile = new File(backFilePath);  
            if(backFile.exists()){  
                backFile.delete();  
            }  
            Runtime runt = Runtime.getRuntime();  
            //Process proc = runt.exec("D:/myec6_tomcat/webapps/cms/dbtools/mysqldumpwin.exe -h 127.0.0.1 -P3306   -uroot -p123 --result-file=F:/tables/menuinfo.sql --default-character-set=gbk bizoss_cms menuinfo");  
          
            String shellCommand = getBackupShellString(targetName);
            String[] cmd= new String[]{"sh","-c",shellCommand}; 
            Process proc = runt.exec(cmd); 
            
            
            InputStream stderr = proc.getErrorStream();  
            InputStreamReader isr = new InputStreamReader(stderr);  
            BufferedReader br = new BufferedReader(isr);  
            String line = null;  
            System.out.println("<error></error>");  
            while ((line = br.readLine()) != null)  
                System.out.println(line);  
            System.out.println("");  
            int tag = proc.waitFor();//等待进程终止  
            System.out.println("Process exitValue: " + tag);  
            
            if(tag==0){  
                return true;  
            }else{  
                return false;  
            }  
        } catch (Exception e) {  
            e.printStackTrace(); 
            return false;  
        }  
          
    }  
    /** 
     * 恢复数据库 
     * @param targetName 要备份的对象名：只能为表名和数据库名称 
     * @return 成功:TRUE 失败:FALSE 
     */  
    public boolean restore(String targetName){  
        try {  
            Runtime runt = Runtime.getRuntime();  
            Process proc;  
            String cmdtext = this.getRestoreShellString(targetName);  
            if(System.getProperty("os.name").indexOf("Windows")!=-1){  
                String[] cmd= { "cmd", "/c", cmdtext};  
                proc= runt.exec(cmd);  
            }else{  
                String[] cmd= { "sh","-c",cmdtext};  
                proc = runt.exec(cmd);  
            }   
            System.out.println(cmdtext);  
            int tag = proc.waitFor();//等待进程终止  
            System.out.println("进程返回值为tag:"+tag);  
            if(tag==0){  
                return true;  
            }else{  
                return false;  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return false;  
    }  
    /** 
     * 根据路径生成恢复数据库的Shell字符串 
     * @param targetName targetName 要还原的对象名：只能为表名和数据库名称 
     * @return 恢复数据时实际执行的shell 
     */  
    public String getRestoreShellString(String targetName){  
//        String basepath=Thread.currentThread().getContextClassLoader().getResource("").toString();  
        String basepath="/usr/bin/" ;
//        String database_tools_path=basepath.substring(6, basepath.length()-4)+"dbtools/";//备份工具路径  
        String backFile = "";//已备份的文件  
        if(targetName.indexOf(this.dbName) == -1){//还原表  
            backFile = "/root/MedicineSellingSystemAPI/tables/"+targetName+".sql";  
        }else{//还原库  
               backFile ="/root/MedicineSellingSystemAPI/tables/"+targetName;  
        }  
        String OSType = System.getProperty("os.name");  
        String shellStr = "";  
        if(OSType.indexOf("Windows")!=-1){  
            shellStr = basepath+"mysqlwin.exe -hlocalhost -P3306 -u"+this.loginName+" -p"+  
            this.password+" --default-character-set=utf8 "+this.dbName +" < "+backFile;  
        }else{  
            shellStr = basepath+"mysql -hlocalhost -P3306 -u"+this.loginName+" -p"+  
            this.password+"  --default-character-set=utf8 "+this.dbName+" < "+backFile;  
        }  
        return shellStr;  
    }  
}
