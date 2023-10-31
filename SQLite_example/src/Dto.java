package sqlite_example.resources;

import java.io.Serializable;

public class Dto implements Serializable {
    static final long serialVersionUID = 1L;

    protected String request;
    protected String response;

    public Dto(String request) {
        this.setRequest(request);
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
