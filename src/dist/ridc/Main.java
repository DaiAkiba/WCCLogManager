package dist.ridc;

import oracle.stellent.ridc.IdcClient;
import oracle.stellent.ridc.IdcClientManager;
import oracle.stellent.ridc.model.DataBinder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import dist.config.ConfigurationController;
import dist.database.AccessLogExportController;
import dist.database.OracleDatabaseManager;

public class Main {
	private static Log logger = LogFactory.getLog(Main.class);
	
	public static void main(String[] args) throws Exception {
		logger.info("処理を開始しました");
		ConfigurationController config = new ConfigurationController("data/properties.xml");
		OracleDatabaseManager database = new OracleDatabaseManager(config.getProperites());
		AccessLogExportController accessLogExporter = new AccessLogExportController(config.getProperty("exportDirectoryPath") , config.getProperty("exportFileName"));
		String strStartDate = "20140401";
		String strEndDate = "20140531";
		accessLogExporter.export(database.getConnection(), strStartDate, strEndDate, "");
		database.getConnection().close();
		logger.info("処理を終了しました");
		//database.GetSctAccessLog();
		//database.GetRevisions();
		//database.GetDocMeta();
	}
	
	public void RIDCTest () throws Exception {
		IdcClientManager manager =new IdcClientManager();
		IdcClient <?,?,?> client = manager.createClient("http://192.168.56.199:16200/cs/idcplg");
		//IdcContext	context =new IdcContext("weblogic", "welcome1");
		
		DataBinder binder = client.createBinder();
		binder.putLocal("IdcService", "DOC_INFO");
		binder.putLocal("dID", "3401");
		binder.putLocal("dDocName", "MZD003601");
		
		
		//DataBinder result = client.sendRequest(context, binder).getResponseAsBinder();
	}
}
