package Charts;

import javafx.concurrent.Task;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.URL;

public class ChartBackService extends Service {


    public static ChartBackService service;


    protected ChartBackService(URL wsdlDocumentLocation, QName serviceName) {
        super(wsdlDocumentLocation, serviceName);
        javafx.concurrent.Service service = new javafx.concurrent.Service() {
            @Override
            protected Task createTask() {
                return new Task() {
                    @Override
                    protected Object call() throws Exception {
                        // label_welcome.textProperty().bind(service.messageProperty());
                        return null;
                    }
                };
            }
        };
    }


}
