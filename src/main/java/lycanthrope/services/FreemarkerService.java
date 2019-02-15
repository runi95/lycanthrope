package lycanthrope.services;

import freemarker.template.Template;
import lycanthrope.models.WebSocketResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import java.io.StringWriter;
import java.util.Map;

@Service
public class FreemarkerService {

    @Autowired
    private FreeMarkerConfig freeMarkerConfig;

    public WebSocketResponseMessage<String> parseTemplate(String templateName, Map model) throws Exception {
        StringWriter stringWriter = new StringWriter();

        Template temp = freeMarkerConfig.getConfiguration().getTemplate(templateName + ".ftlh");
        temp.process(model, stringWriter);

        WebSocketResponseMessage webSocketResponseMessage = new WebSocketResponseMessage();
        webSocketResponseMessage.setAction("changeView");
        webSocketResponseMessage.setContent(stringWriter.getBuffer().toString());
        webSocketResponseMessage.setStatus(200);

        return webSocketResponseMessage;
    }
}
