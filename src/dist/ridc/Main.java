package dist.ridc;

import oracle.stellent.ridc.IdcClient;
import oracle.stellent.ridc.IdcClientManager;
import oracle.stellent.ridc.model.DataBinder;
import dist.config.ConfigurationController;
import dist.database.AccessLogExportController;
import dist.database.OracleDatabaseManager;

public class Main {
	public static void main(String[] args) throws Exception {
		ConfigurationController config = new ConfigurationController("/Users/akiba/properties.xml");
		OracleDatabaseManager database = new OracleDatabaseManager(config.getProperites());
		AccessLogExportController accessLogExporter = new AccessLogExportController(config.getProperty("exportfilepath"));
		accessLogExporter.export(database.getConnection());
		//database.GetSctAccessLog();
		//database.GetRevisions();
		//database.GetDocMeta();
	}
	
	public void RIDCTest () throws Exception {
		IdcClientManager manager =new IdcClientManager();
		IdcClient <?,?,?> client = manager.createClient("http://192.168.56.101:16200/cs/idcplg");
		//IdcContext	context =new IdcContext("weblogic", "welcome1");
		
		DataBinder binder = client.createBinder();
		binder.putLocal("IdcService", "DOC_INFO");
		binder.putLocal("dID", "3401");
		binder.putLocal("dDocName", "MZD003601");
		
		
		//DataBinder result = client.sendRequest(context, binder).getResponseAsBinder();
	}
}
